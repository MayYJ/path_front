package com.may.routeplansystem.algorithm.imp;

import com.may.routeplansystem.algorithm.Algorithm;
import com.may.routeplansystem.constant.Constant;
import com.may.routeplansystem.constant.ProcessState;
import com.may.routeplansystem.dao.*;
import com.may.routeplansystem.entity.po.*;
import com.may.routeplansystem.exception.ParameterException;
import com.may.routeplansystem.exception.ProcessException;
import com.may.routeplansystem.exception.StopException;
import com.may.routeplansystem.service.util.ServiceUtil;
import com.may.routeplansystem.util.DateUtil;
import com.may.routeplansystem.util.RandomInt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * @author 10587
 */
@Slf4j
@Component
public class SimpleAlgorithm extends Algorithm {

    private static final int VERSION = 1;
    private static final int SCHEME_NUM = 3;
    private static final int MAX_PATH_NUM = 4;

    @Resource
    private NodeDao nodeDao;

    @Resource
    private DistanceDao distanceDao;

    @Resource
    private FinalSolutionDao finalSolutionDao;

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private UserDao userDao;

    @Resource
    private SolutionDao solutionDao;

    @Resource
    private QuestionDao questionDao;

    public static Map<Integer, Boolean> stopMap = new ConcurrentHashMap<>();

    @Override
    public void beforeExecute(int questionId) {
        Question question = questionDao.findQuestionByQuestionId(questionId);
        generalBeforeExecute(question, "随机算法");
        if (question.getSimpleExecuted() == ProcessState.ALGORITHM_IS_PROCESSING) {
            throw new ProcessException("正在执行随机算法,不能重复执行");

        }
        if (question.getSimpleExecuted() == ProcessState.ALGORITHM_COMPLETED) {
            throw new ProcessException("您已经执行过随机算法了哟");
        }
        questionDao.updateSimpleExecuted(questionId, ProcessState.ALGORITHM_IS_PROCESSING);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeAlgorithm(int questionId) {
        List<NodePojo> centerNodes = nodeDao.selectCenterNode(questionId);
        List<NodePojo> serviceNodes = nodeDao.selectServiceNode(questionId);
        if (centerNodes.isEmpty()) {
            throw new ProcessException("请选择一个中心点");
        }
        if (centerNodes.size() > 1) {
            throw new ProcessException("简单算法中心点只能有一个");
        }
        if (serviceNodes.size() < 2) {
            throw new ProcessException("服务点太少了");
        }
        NodePojo centerNode = centerNodes.get(0);
        List<Scheme> schemes = getRandomScheme(centerNode, serviceNodes, questionId);
        schemes.forEach(scheme -> {
            int finalSolutionId = getAndStoreFinalSolution(scheme, questionId);
            getAndStoreSolution(scheme, finalSolutionId);
        });
    }

    @Override
    public void afterExecute(int questionId) {
        Question question = questionDao.findQuestionByQuestionId(questionId);
        UserMessage userMessage = userDao.userMessage(String.valueOf(question.getUserId()));
        questionDao.updateSimpleExecuted(questionId, ProcessState.ALGORITHM_COMPLETED);
        generalAfterExecute(questionId,javaMailSender, userMessage.getEMail(), "遗传算法");
    }

    /**
     * @TODO 这里因为Distance 和 FinalSolution totalDistance类型没有对应使用了强制类型转换
     */
    private int getAndStoreFinalSolution(Scheme scheme, int questionId) {
        FinalSolution finalSolution = new FinalSolution();
        int schemeTotalDistance = getSchemeTotalDistance(scheme);
        String currentTime = DateUtil.formatNowDateTimeToString(Constant.DATETIME_FORMAT);
        finalSolution.setCreateTime(currentTime);
        finalSolution.setTotalDis((double) schemeTotalDistance);
        finalSolution.setQuestionId(questionId);
        finalSolution.setVersion(VERSION);
        int totalDis = getSchemeTotalDistance(scheme);
        finalSolution.setTotalDis(totalDis);
        finalSolutionDao.insertFinalSolution(finalSolution);
        return finalSolution.getFinalSolutionId();
    }

    private void getAndStoreSolution(Scheme scheme, int finalSolutionId) {
        for (Scheme.Path path: scheme) {
            Solution solution = new Solution();
            String stringPath = formatPathToString(path);
            double totalPathDistance = calculatePathDistance(path);
            solution.setFinalSolutionId(finalSolutionId);
            solution.setTotalDis(totalPathDistance);
            solution.setRoute(stringPath);
            boolean flag = solutionDao.insertSolution(solution);
            ServiceUtil.checkSqlExecuted(flag);
        }
    }

    private String formatPathToString(Scheme.Path path) {
        StringBuilder s = new StringBuilder();
        path.forEach(node -> {
            s.append(node.getLng());
            s.append(",");
            s.append(node.getLat());
            s.append(",");
            s.append(node.getNodeAddress());
            s.append(";");
        });
        return s.toString();
    }

    private List<Scheme> getRandomScheme(NodePojo centerNode, List<NodePojo> serviceNodes, int questionId) {
        List<Scheme> schemes = new ArrayList<>(SCHEME_NUM);
        Scheme scheme;
        for (int k = 0; k < SCHEME_NUM; k++) {
            if (stopMap.get(questionId) != null) {
                stopMap.remove(questionId);
                questionDao.updateSimpleExecuted(questionId, 0);
                throw new StopException("停止运行简单算法成功");
            }
            int pathNum = getRandomPathNum(serviceNodes.size());
            scheme = new Scheme(pathNum);
            List<NodePojo> serviceNodesDistribute = new ArrayList<>(serviceNodes);
            if (serviceNodesDistribute.size() == 0) {
                continue;
            }
            for (int i = 0; i < pathNum; i++) {
                int serviceNodesDistributeSize = serviceNodesDistribute.size();
                int randomNodes = serviceNodesDistributeSize == 1?
                        1:RandomInt.randomIntExceptZero(serviceNodesDistribute.size());
                Scheme.Path path = new Scheme.Path(randomNodes + 2);
                path.add(centerNode);
                if (serviceNodesDistributeSize == 1) {
                    path.addAll(serviceNodesDistribute);
                    path.add(centerNode);
                    scheme.add(path);
                    break;
                }
                if (i == pathNum - 1) {
                    path.addAll(serviceNodesDistribute);
                    path.add(centerNode);
                    scheme.add(path);
                    break;
                }

                IntStream.range(0, randomNodes).forEach(j -> {
                    int randomIndex = RandomInt.randomInt(0, serviceNodesDistribute.size());
                    path.add(serviceNodesDistribute.get(randomIndex));
                    serviceNodesDistribute.remove(randomIndex);
                });
                path.add(centerNode);
                scheme.add(path);
            }
            schemes.add(scheme);
        }
        return schemes;
    }

    private int getRandomPathNum(int serviceNodesSize) {
        int schemeNum;
        if (serviceNodesSize < MAX_PATH_NUM) {
            schemeNum = RandomInt.randomIntExceptZero(serviceNodesSize);
        } else {
            schemeNum = RandomInt.randomIntExceptZero(MAX_PATH_NUM + 1);
        }
        return schemeNum;
    }

    private int getSchemeTotalDistance(Scheme scheme) {
        return scheme.stream().mapToInt(this::calculatePathDistance).sum();
    }

    private int calculatePathDistance(Scheme.Path path) {
        int sum = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            if (path.get(i).getNodeId() == path.get(i + 1).getNodeId()) {
                return 0;
            }
            Distance distance = distanceDao.findDistanceByStartIdAndEndId(path.get(i).getNodeId(),
                    path.get(i + 1).getNodeId());
            sum += distance.getDis();
        }
        return sum;
    }

    @Override
    public void stop(int questionId) {
        Question question = questionDao.findQuestionByQuestionId(questionId);
        if (question == null) {
            throw new ParameterException("没有该ID的问题");
        }
        stopMap.put(questionId, true);
    }

    static class Scheme extends ArrayList<Scheme.Path>{

        Scheme(int size) {
            super(size);
        }

        public static class Path extends ArrayList<NodePojo> {

            public Path(int size){
                super(size);
            }
        }
    }


}
