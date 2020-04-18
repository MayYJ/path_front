package com.may.routeplansystem.algorithm.imp;

import com.may.routeplansystem.algorithm.Algorithm;
import com.may.routeplansystem.cache.Cache;
import com.may.routeplansystem.constant.ProcessState;
import com.may.routeplansystem.dao.*;
import com.may.routeplansystem.entity.po.*;
import com.may.routeplansystem.entity.vo.RouteTemp;
import com.may.routeplansystem.exception.ParameterException;
import com.may.routeplansystem.exception.ProcessException;
import com.may.routeplansystem.exception.StopException;
import com.may.routeplansystem.util.RandomInt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 10587
 */
@Slf4j
@Component
public class GeneticAlgorithm extends Algorithm {

    @Resource
    private NodeDao nodeDao;

    @Resource
    private DistanceDao distanceDao;

    @Resource
    private SolutionDao solutionDao;

    @Resource
    private FinalSolutionDao finalSolutionDao;

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private QuestionDao questionDao;

    @Resource
    private UserDao userDao;

    private static final int VERSION = 2;

    /**
     * 保存算法停止状态的Map
     */
    private final Map<Integer, Boolean> stopMap = new ConcurrentHashMap<>();


    @Override
    public void beforeExecute(int questionId) {
        Question question = questionDao.findQuestionByQuestionId(questionId);
        generalBeforeExecute(question, "遗传算法");
        if (question.getGeneticExecuted() == ProcessState.PROCESSING_GENETIC) {
            throw new ProcessException("正在执行遗传算法,不能重复执行");

        }
        if (question.getGeneticExecuted() == ProcessState.COMPLETE_GENETIC) {
            throw new ProcessException("您已经执行过遗传算法了哟");
        }
        questionDao.updateGeneticExecuted(ProcessState.PROCESSING_GENETIC, questionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeAlgorithm(int questionId) {
        List<NodePojo> centerNodes = nodeDao.selectCenterNode(questionId);
        List<NodePojo> serviceNodes = nodeDao.selectServiceNode(questionId);
        int minDistance;
        /** 初始化的方案数*/
        int solutionCount = 20;
        int hybridMutationCount = 5;
        int i = 0;
        List<RouteTemp> solutions = initializeRoute(solutionCount, serviceNodes, centerNodes);
        if (!solutions.isEmpty()) {
            TreeMap<Integer, RouteTemp> treeMap = saveDistanceAndRoute(solutions);
            minDistance = treeMap.firstKey();

            while (i < hybridMutationCount) {
                if (stopMap.get(questionId) != null) {
                    stopMap.remove(questionId);
                    questionDao.updateGeneticExecuted(questionId, 0);
                    throw new StopException("停止运行遗传算法成功");
                }

                treeMap = hybridMutation(treeMap);
                int min = treeMap.firstKey();
                i++;
//                if (minDistance == min) {
//                    i++;
//                } else {
//                    minDistance = min;
//                    i = 0;
//                }
            }
            removeSurplusSolutions(treeMap);

            Set<Map.Entry<Integer, RouteTemp>> set = treeMap.entrySet();
            Iterator<Map.Entry<Integer, RouteTemp>> iterator = set.iterator();

            iterator.forEachRemaining(entry -> {
                RouteTemp routes = entry.getValue();
                double totalDis = entry.getKey();
                int finalSolutionId = createFinalSolutionAndInsert(questionId, totalDis);
                List<Solution> solutionList = madeRoute(routes.getRoute(), finalSolutionId);
                solutionList.forEach(solutionDao::insertSolution);
            });
        }
    }

    @Override
    public void afterExecute(int questionId) {
        Question question = questionDao.findQuestionByQuestionId(questionId);
        UserMessage userMessage = userDao.userMessage(String.valueOf(question.getUserId()));
        questionDao.updateGeneticExecuted(questionId, ProcessState.COMPLETE_GENETIC);
//        generalAfterExecute(questionId,javaMailSender, userMessage.getEMail(), "遗传算法");
    }

    private int createFinalSolutionAndInsert(int questionId, double totalDis) {
        FinalSolution finalSolution = new FinalSolution();
        finalSolution.setVersion(VERSION);
        finalSolution.setQuestionId(questionId);
        finalSolution.setTotalDis(totalDis);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nowStr = formatter.format(now);
        finalSolution.setCreateTime(nowStr);
        finalSolutionDao.insertFinalSolution(finalSolution);
        return finalSolution.getFinalSolutionId();
    }

    private void removeSurplusSolutions(Map<Integer, RouteTemp> treeMap) {
        log.info("删除前 treeMap 的size：" + treeMap.size());
        Set<Map.Entry<Integer, RouteTemp>> set = treeMap.entrySet();
        Iterator<Map.Entry<Integer, RouteTemp>> it = set.iterator();
        int i = 0;
        while (it.hasNext()) {
            i++;
            if (i <= 4) {
                it.next();
                continue;
            }
            it.next();
            it.remove();
        }
        log.info("删除后 treeMap size: " + treeMap.size());
    }

    private List<Solution> madeRoute(List<List<NodePojo>> nodes, int finalSolutionId) {
        List<Solution> list = new ArrayList<>();
        StringBuilder s;

        //路线数
        for (int i = 0; i < nodes.size(); i++) {
            double dis = 0;
            double time = 0;
            s = new StringBuilder();

            List<NodePojo> node = nodes.get(i);
            //每条路线节点数
            for (int k = 0; k < node.size(); k++) {
                NodePojo b = node.get(k);
                s.append(b.getLng() + "," + b.getLat() + "," + b.getNodeAddress() + ";");
            }

            for (int k = 0; k < node.size() - 1; k++) {
                NodePojo a = node.get(k);
                NodePojo b = node.get(k + 1);
                Distance distance1 = distanceDao.findDistanceByStartIdAndEndId(a.getNodeId(), b.getNodeId());
                dis = dis + distance1.getDis();
                time = time + distance1.getTime();
            }
            log.info(s.toString());
            Solution route = new Solution();
            route.setTotalTime(time);
            route.setTotalDis(dis);
            route.setRoute(s.toString());
            route.setFinalSolutionId(finalSolutionId);
            list.add(route);
        }
        return list;
    }

    private TreeMap<Integer, RouteTemp> hybridMutation(TreeMap<Integer, RouteTemp> treeMap) {
        //先轮盘赌，找到父代
        Map.Entry<Integer, RouteTemp> entry = lunPanDu(treeMap);
        List<List<NodePojo>> route = entry.getValue().getRoute();
        int routeCount = route.size();
        //直接变异
        if (routeCount == 1) {
            List<List<NodePojo>> routeTemp = mutation(route);
            entry.getValue().setRoute(routeTemp);
        } else {
            //默认必须杂交
            route = hybrid(route, routeCount);
            //变异随机
            route = mutation(route);
            int distance = caculateDistance(route);
            int maxDistance = treeMap.lastKey();
            if (!treeMap.containsKey(distance)) {
                if (distance < maxDistance) {
                    treeMap.pollLastEntry();
                    RouteTemp routeTemp = new RouteTemp(route);
                    treeMap.put(distance, routeTemp);
                }
            }
        }
        return treeMap;
    }

    private int caculateDistance(List<List<NodePojo>> route) {
        int size = route.size();
        int totalDis = 0;
        for (int i = 0; i < size; i++) {
            List<NodePojo> nodes = route.get(i);
            int nodeSize = nodes.size();
            totalDis = getTotalDis(totalDis, nodes, nodeSize);
        }
        return totalDis;
    }

    private int getTotalDis(int totalDis, List<NodePojo> nodes, int nodeSize) {
        for (int j = 0; j < nodeSize - 1; j++) {
            NodePojo a = nodes.get(j);
            NodePojo b = nodes.get(j + 1);
            Distance distance1 = distanceDao.findDistanceByStartIdAndEndId(a.getNodeId(), b.getNodeId());
            totalDis = totalDis + distance1.getDis();
        }
        return totalDis;
    }

    private List<List<NodePojo>> hybrid(List<List<NodePojo>> route, int routeCount) {
        int route1 = (int) (Math.random() * routeCount);
        int route2 = (int) (Math.random() * routeCount);
        while (route1 == route2) {
            route2 = (int) (Math.random() * routeCount);
        }
        List routeWay1 = route.get(route1);
        List routeWay2 = route.get(route2);
//        if (routeWay1.size() == 2 || routeWay2.size() == 2){
//            return route;
//        }
        int routeWaySize1 = routeWay1.size();
        int routeWaySize2 = routeWay2.size();
        int routeIndex1 = (int) (Math.random() * (routeWaySize1 - 2) + 1);
        int routeIndex2 = (int) (Math.random() * (routeWaySize2 - 2) + 1);
        List head1 = new ArrayList(routeWay1.subList(0, routeIndex1));
        List tail1 = new ArrayList(routeWay1.subList(routeIndex1, routeWaySize1));
        List head2 = new ArrayList(routeWay2.subList(0, routeIndex2));
        List tail2 = new ArrayList(routeWay2.subList(routeIndex2, routeWaySize2));
        head1.addAll(tail2);
        head2.addAll(tail1);
        route.set(route1, head1);
        route.set(route2, head2);
        return route;
    }

    private void logRoutemp(RouteTemp routeTemp) {
        routeTemp.getRoute().forEach(route -> {
            StringBuilder stringBuilder = new StringBuilder();
            route.forEach(nodePojo -> {
                stringBuilder.append(nodePojo.getNodeName());
            });
        });
    }

    private List<List<NodePojo>> mutation(List<List<NodePojo>> route) {
        int routeSize = route.size();
        int route1 = (int) (Math.random() * routeSize);
        List<NodePojo> routeWay1 = route.get(route1);
        int size = routeWay1.size();
        int node1 = 0;
        int node2 = 0;
        node1 = (int) (Math.random() * (size - 2) + 1);
        node2 = (int) (Math.random() * (size - 2) + 1);
        if (node1 == node2) {
            return route;
        } else {
            NodePojo serviceNode = routeWay1.get(node1);
            routeWay1.set(node1, routeWay1.get(node2));
            routeWay1.set(node2, serviceNode);
            return route;
        }
    }

    private int notInHeadAndTail(int routeWaySize1, int routeIndex1) {

        while (routeIndex1 == 0 || routeIndex1 == routeWaySize1 - 1) {
            routeIndex1 = (int) (Math.random() * routeWaySize1);
        }
        return routeIndex1;
    }


    private Map.Entry<Integer, RouteTemp> lunPanDu(TreeMap<Integer, RouteTemp> treeMap) {
        int size = treeMap.size();
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            for (int j = i; j <= size; j++) {
                list.add(i);
            }
        }
        int index = list.get((int) Math.random() * list.size());

        Set<Map.Entry<Integer, RouteTemp>> set = treeMap.entrySet();
        Iterator<Map.Entry<Integer, RouteTemp>> iterator = set.iterator();
        Map.Entry<Integer, RouteTemp> parentEntry = null;
        for (int i = 0; i < index; i++) {
            parentEntry = iterator.next();
        }
        return parentEntry;
    }

    private TreeMap<Integer, RouteTemp> saveDistanceAndRoute(List<RouteTemp> routeList) {
        TreeMap<Integer, RouteTemp> map = new TreeMap<>();
        int totalDis = 0;
        List<NodePojo> nodes;
        int routeSize = routeList.size();
        for (int i = 0; i < routeSize; i++) {
            RouteTemp routeTemp = routeList.get(i);
            List<List<NodePojo>> route = routeTemp.getRoute();
            int routeCount = route.size();
            for (int j = 0; j < routeCount; j++) {
                int size = route.get(j).size();
                nodes = route.get(j);
                totalDis = getTotalDis(totalDis, nodes, size);
            }
            map.put(totalDis, routeTemp);
        }
        return map;
    }

    private List<RouteTemp> initializeRoute(int count, List<NodePojo> serviceNodes,
                                            List<NodePojo> centerNodes) {
        if (centerNodes.isEmpty()) {
            throw new ProcessException("请选择一个中心点");
        }
        if (centerNodes.size() > 1) {
            throw new ProcessException("遗传算法中中心点只能有一个");
        }
        if (serviceNodes.isEmpty()) {
            throw new ProcessException("服务点不能为空");
        }
        NodePojo centerNode = centerNodes.get(0);
        List<RouteTemp> solutions = new LinkedList<>();
        int routeRandom = 20;
        RouteTemp oneRoute;
        for (int i = 0; i < count; i++) {
            List<NodePojo> serviceNodesCopy = new LinkedList<>(serviceNodes);
            int routeCount = RandomInt.randomIntExceptZero(routeRandom);
            oneRoute = new RouteTemp(routeCount);
            for (int j = 0; j < routeCount; j++) {
                if (serviceNodesCopy.isEmpty()) {
                    break;
                }
                List<NodePojo> everyPath = oneRoute.getRoute().get(j);
                everyPath.add(centerNode);
                if (j == routeCount - 1) {
                    everyPath.addAll(serviceNodesCopy);
                    everyPath.add(centerNode);
                    break;
                }
                int serviceNodesCopySize = serviceNodesCopy.size();
                if (serviceNodesCopySize == 1) {
                    everyPath.add(serviceNodesCopy.get(0));
                    everyPath.add(centerNode);
                    break;
                }
                int everyRouteNodeCount = RandomInt.randomIntExceptZero(serviceNodesCopySize);
                for (int k = 0; k < everyRouteNodeCount; k++) {
                    int index = RandomInt.randomInt(0, serviceNodesCopy.size());
                    NodePojo serviceNode = serviceNodesCopy.get(index);
                    oneRoute.getRoute().get(j).add(serviceNode);
                    serviceNodesCopy.remove(index);
                }
                oneRoute.getRoute().get(j).add(centerNode);
            }
            logRoutemp(oneRoute);
            removeEmptyRoute(oneRoute);
            solutions.add(oneRoute);
        }
        return solutions;
    }

    private void removeEmptyRoute(RouteTemp routeTemp) {
        int k = 0;
        while (k < routeTemp.getRoute().size()) {
            List list = routeTemp.getRoute().get(k);
            if (!list.isEmpty()) {
                k++;
            } else {
                routeTemp.getRoute().remove(k);
            }
        }
    }


    @Override
    public void stop(int questionId) {
        Question question = questionDao.findQuestionByQuestionId(questionId);
        if (question == null) {
            throw new ParameterException("没有该ID的问题");
        }
        if (question.getProcessState() != ProcessState.PROCESSING_GENETIC) {
            throw new ProcessException("遗传算法还没有执行，不能停止");
        }
        stopMap.put(questionId, true);
    }
}
