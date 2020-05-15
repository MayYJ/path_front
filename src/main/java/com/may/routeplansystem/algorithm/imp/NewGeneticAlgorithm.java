package com.may.routeplansystem.algorithm.imp;

import com.may.routeplansystem.algorithm.Algorithm;
import com.may.routeplansystem.constant.ProcessState;
import com.may.routeplansystem.dao.*;
import com.may.routeplansystem.entity.po.*;
import com.may.routeplansystem.entity.vo.Plan;
import com.may.routeplansystem.entity.vo.VehicleTemp;
import com.may.routeplansystem.exception.ParameterException;
import com.may.routeplansystem.exception.ProcessException;
import com.may.routeplansystem.util.RandomInt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author :DengSiYuan
 * @date :2019/10/4 20:23
 * @desc :
 */
@Slf4j
@Component
public class NewGeneticAlgorithm extends Algorithm {

    @Resource
    private QuestionDao questionDao;
    @Resource
    private UserDao userDao;
    @Resource
    private NodeDao nodeDao;
    @Resource
    private VehicleDao vehicleDao;
    @Resource
    private FinalSolutionDao finalSolutionDao;
    @Resource
    private DistanceDao distanceDao;
    @Resource
    private SolutionDao solutionDao;
    @Resource
    private JavaMailSender javaMailSender;
    private static final int VERSION = 3;

    private static final double insuranceStrategy = 0.05;
    /**
     * 保存算法停止状态的Map
     */
    private final Map<Integer, Boolean> stopMap = new ConcurrentHashMap<>();

    /**
     * 在执行算法之前执行的方法
     * @param questionId
     */
    @Override
    public void beforeExecute(int questionId) {
        Question question = questionDao.findQuestionByQuestionId(questionId);
        generalBeforeExecute(question, "优化版遗传算法");
        if (question.getNewGeneticExecuted() == ProcessState.ALGORITHM_IS_PROCESSING) {
            throw new ProcessException("正在执行优化版遗传算法,不能重复执行");

        }
        if (question.getNewGeneticExecuted() == ProcessState.ALGORITHM_COMPLETED) {
            throw new ProcessException("您已经执行过优化版遗传算法了哟");
        }
        questionDao.updateNewGeneticExecuted(questionId, ProcessState.ALGORITHM_IS_PROCESSING);
    }

    /**
     * 执行算法
     * @param questionId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeAlgorithm(int questionId) {
        List<NodePojo> centerNodes = nodeDao.selectCenterNode(questionId);
        List<NodePojo> serviceNodes = nodeDao.selectServiceNode(questionId);
        if (serviceNodes.size() == 0){
            throw new ProcessException("没有服务点接受服务");
        }
        //生成初始解200个
        List<Plan> plans = initSolution(serviceNodes,questionId);
        //检验解的有效性
        System.out.println("检验");
        isVaild(plans,serviceNodes.size());
        //将中心点加入每个路线的首部
        plans.forEach(initSolution ->
                initSolution.getVehicleTempList().forEach(vehicleTemp ->
                        vehicleTemp.getVehicleServiceNodes().add(0,centerNodes.get(0))));
        //计算每个个体的适应度值
        fitness(plans);
        System.out.println("-------------------");
        //根据适应度值进行筛选，找出最优值进行下一代的培养
        plans.sort(Comparator.comparingDouble(Plan::getFitness).reversed());
        System.out.println("培养");
        List<Plan> finalPlans = finalPlans(plans,serviceNodes.size());
        fitness(finalPlans);
        System.out.println("排序");
        finalPlans.sort(Comparator.comparingDouble(Plan::getFitness).reversed());
        //筛选出前五条进行入库 去重
        insertDataBase(questionId,new ArrayList<>(new HashSet<>(finalPlans)));
    }


    private List<Plan> initSolution(List<NodePojo> serviceNodes, int questionId) {
        //初始化返回结果集
        Set<Plan> planSet = new HashSet<>();
        //查找可用车辆
        List<VehicleMessage> vehicleMessages = vehicleDao.searchVehicleByQuestionId(questionId);
        //判断车数是否符合要求
        int vehicleCount = vehicleMessages.size();
        if (vehicleCount < 1){
            throw new ProcessException("没有车辆可以提供服务");
        }
        int num = serviceNodes.size() > 5 ? 200 : (int) (factBig(serviceNodes.size()).intValue() * 0.8);
        //生成200个初始结果
        for (int i = 0; i < num; i++) {
            //复制车辆信息和服务点信息
            List<VehicleMessage> originalVehicles = new LinkedList<>(vehicleMessages);
            List<NodePojo> serviceNode = new LinkedList<>(serviceNodes);
            vehicleCount = originalVehicles.size();
            int serviceNodeCount = serviceNode.size();
            //初始化一个方案
            Plan plan = new Plan();
            //初始化一个路径
            List<VehicleTemp> vehicleTemps = new ArrayList<>();
            //多少个车投入使用
            int vehicleUsed = RandomInt.randomIntExceptZero(vehicleCount+1);
            int vehicleInUsed = vehicleUsed;
            //根据投入使用的车辆数量开始构造初始结果
            for (int j = 0; j < vehicleUsed; j++) {
                //初始化一个车的运行路线
                VehicleTemp vehicleTemp = new VehicleTemp();
                int vehicleId = RandomInt.randomInt(0,vehicleCount);
                //当前车辆的服务点数量
                int currVehicleNodeCount;
                //存入车辆信息并进行清除
                vehicleTemp.setVehicleMessage(originalVehicles.get(vehicleId));
                originalVehicles.remove(vehicleId);
                vehicleCount--;
                //生成当前车辆的服务点数量
                if (--vehicleInUsed <= 1){
                    currVehicleNodeCount = serviceNodeCount;
                }else {
                    System.out.println("-------"+(serviceNodeCount - vehicleCount + 1));
                    currVehicleNodeCount = RandomInt.randomIntExceptZero(serviceNodeCount - vehicleCount + 2);
                }
                //初始化当前车辆的服务点
                List<NodePojo> currVehicleNodes = new ArrayList<>();
                for (int m = 0; m < currVehicleNodeCount; m++) {
                    int nodeId = RandomInt.randomInt(0,serviceNodeCount );
                    //存入服务点的信息并进行移除
                    currVehicleNodes.add(serviceNode.get(nodeId));
                    serviceNode.remove(nodeId);
                    serviceNodeCount--;
                }
                vehicleTemp.setServiceNodeCount(currVehicleNodeCount);
                vehicleTemp.setVehicleServiceNodes(currVehicleNodes);
                vehicleTemps.add(vehicleTemp);
            }
            plan.setVehicleTempList(vehicleTemps);
            planSet.add(plan);
            //planList.add(plan);
        }
        System.out.println("初始化");
        List<Plan> planList = new ArrayList<>(planSet);
        int total = 0;
        for (VehicleTemp vehicleTemp : planList.get(0).getVehicleTempList()) {
            total += vehicleTemp.getServiceNodeCount();
        }
        System.out.println("----"+total);
        return planList;

    }

    /**
     * 检验解的有效性，取出无效解
     * @param initSolutions
     * @param serviceNodes
     */
    private void isVaild(List<Plan> initSolutions,int serviceNodes) {
        for (int i = 0; i < initSolutions.size(); i++) {
            Plan plan = initSolutions.get(i);
            List<VehicleTemp> vehicleTemps = plan.getVehicleTempList();
            int serviceNodeCount = 0;
            if (vehicleTemps.isEmpty()){
                initSolutions.remove(plan);
                i--;
            }else {
                for (int j = 0; j < vehicleTemps.size(); j++) {
                    VehicleTemp vehicleTemp = vehicleTemps.get(j);
                    if (vehicleTemp.getVehicleServiceNodes().size() == 0 || vehicleTemp.getServiceNodeCount() == 0){
                        plan.getVehicleTempList().remove(vehicleTemp);
                        j--;
                    }
                    serviceNodeCount += vehicleTemp.getServiceNodeCount();
                }
                if (serviceNodes != serviceNodeCount){
                    initSolutions.remove(plan);
                    i--;
                }
            }
        }
    }

    /**
     * 计算每个解的适应度值
     * @param plans
     */
    private void fitness(List<Plan> plans) {
        int q = 0;
        for (Plan plan : plans) {
            //获取一个方案的所有路线
            List<VehicleTemp> vehicleTemps = plan.getVehicleTempList();
            long totalDistance = 0L;
            long totalTime = 0L;
            double fitness = 0.0;
            //遍历每一条路线
            for (VehicleTemp vehicleTemp : vehicleTemps) {
                long distance = 0L;
                long time = 0L;
                List<NodePojo> nodePojos = vehicleTemp.getVehicleServiceNodes();
                for (int i = 0,j = i + 1; i < nodePojos.size() -1;i = j, j = i + 1) {
                    NodePojo startNode = nodePojos.get(i);
                    NodePojo endNode = nodePojos.get(j);
                    if (startNode == endNode){
                        continue;
                    }
                    Distance distanceTemp = distanceDao.findDistanceByStartIdAndEndId(
                            startNode.getNodeId(),endNode.getNodeId());
                    distance += distanceTemp.getDis();
                    time += distanceTemp.getTime();
                }
                vehicleTemp.setRouteDistance(distance);
                vehicleTemp.setRouteTime(time);
                //一个方案的所有路径的累和
                totalDistance += distance;
                totalTime += time;
                fitness += distance * vehicleTemp.getVehicleMessage().getOil() + vehicleTemp.getVehicleMessage().getPrice();
            }
            plan.setTotalDistance(totalDistance);
            plan.setTotalTime(totalTime);
            plan.setFitness(1.0 / fitness);
        }
    }

    private BigDecimal factBig(int n){

        if(n == 1 || n==0){
            return BigDecimal.valueOf(1);
        }else{
            return BigDecimal.valueOf(n).multiply(factBig(n - 1));
        }

    }

    /**
     * 最终子代方案集合(200代)
     * @param plans
     * @param serviceNodesSize
     */
    private List<Plan> finalPlans(List<Plan> plans,int serviceNodesSize) {
        System.out.println("开始培养");
        Set<Plan> planList = new HashSet<>();
        int circle = 0;
        int result;
        //如果服务点个数超过5个
        if (serviceNodesSize <= 5){
            result = serviceNodesSize;
        }else if (serviceNodesSize <= 10){
            result = 3;
        }else {
            result = 200;
        }
        //代数
        while (circle++ <= result){
            int size = serviceNodesSize > 40 ? (int) (plans.size() * insuranceStrategy) : 2;
            //保优策略前 5 %
            for (int i = 0; i < size; i++) {
                planList.add(plans.get(i));
            }
            int num = serviceNodesSize > 5 ? 200 : (int) (factBig(serviceNodesSize).intValue() * 0.8);
            //每代数量
            while (planList.size() <= num){
                //选择操作
                List<Plan> planList1 = planSelect(new ArrayList<>(plans),serviceNodesSize);
                planList.addAll(planList1);
            }
        }
        System.out.println("培养结束");
        return new ArrayList<>(planList);
    }
    /**
     * 选择操作
     * @param planList
     * @param serviceNodesSize
     * @return
     */
    private List<Plan> planSelect(List<Plan> planList,int serviceNodesSize) {
        //轮盘赌算法产生的两个个体，X和Y
        Set<Plan> routeSet = new HashSet<>(planList);
        List<Plan> rouletteList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            rouletteList.add(planList.get(roulette(planList)));
        }
        //判断是否筛选了两个
        if (rouletteList.size() != 2){
            throw new ProcessException("轮盘赌算法发生异常！");
        }
        //X、Y车辆进行单点交叉、服务点进行多点交叉
        //车辆单点交叉
        List<VehicleTemp> vehicleTempListX = rouletteList.get(0).getVehicleTempList();
        List<VehicleTemp> vehicleTempListY = rouletteList.get(1).getVehicleTempList();
        crossVehicle(vehicleTempListX,vehicleTempListY,serviceNodesSize);
        //服务点多点交叉
        crossServiceNode(vehicleTempListX,vehicleTempListY,serviceNodesSize);
        //进行服务点变异操作
        variation(vehicleTempListX,vehicleTempListY);
        Plan planA = new Plan();
        planA.setVehicleTempList(vehicleTempListX);
        Plan planB = new Plan();
        planB.setVehicleTempList(vehicleTempListY);
        routeSet.add(planA);
        routeSet.add(planB);
        rouletteList.addAll(routeSet);
        fitness(rouletteList);
        return rouletteList;
    }

    private void variation(List<VehicleTemp> vehicleTempListX, List<VehicleTemp> vehicleTempListY) {

    }

    /**
     * 车辆交叉
     * @param vehicleTempListX
     * @param vehicleTempListY
     * @param serviceNodesSize
     */
    private void crossVehicle(List<VehicleTemp> vehicleTempListX, List<VehicleTemp> vehicleTempListY,int serviceNodesSize) {
        int vehicleSizeX = vehicleTempListX.size();
        int vehicleSizeY = vehicleTempListY.size();
        int min = vehicleSizeX <= vehicleSizeY ? vehicleSizeX : vehicleSizeY;
        int max = vehicleSizeX > vehicleSizeY ? vehicleSizeX : vehicleSizeY;
        //产生随机的交换分界点
        int random = RandomInt.randomInt(0,vehicleSizeX <= vehicleSizeY ? vehicleSizeX : vehicleSizeY);
        //从交换分界点开始进行整体交换
        for (int i = random; i < min; i++) {
            VehicleTemp tempList = vehicleTempListX.get(random);
            vehicleTempListX.get(i).setVehicleMessage(vehicleTempListY.get(i).getVehicleMessage());
            vehicleTempListX.get(i).setServiceNodeCount(vehicleTempListY.get(i).getServiceNodeCount());
            vehicleTempListY.get(i).setVehicleMessage(tempList.getVehicleMessage());
            vehicleTempListY.get(i).setServiceNodeCount(vehicleTempListX.get(i).getServiceNodeCount());
        }
        //进行车辆服务点数量的调整
        adjustVehicle(vehicleTempListX,serviceNodesSize);
        adjustVehicle(vehicleTempListY,serviceNodesSize);
    }

    private void crossServiceNode(List<VehicleTemp> vehicleTempListX, List<VehicleTemp> vehicleTempListY, int serviceNodesSize) {
        //取出所有服务点，准备重新分配
        List<NodePojo> nodePojosA = new ArrayList<>(serviceNodesSize);
        List<NodePojo> nodePojosB = new ArrayList<>(serviceNodesSize);
        for (VehicleTemp vehicleTemp : vehicleTempListX) {
            nodePojosA.addAll(vehicleTemp.getVehicleServiceNodes());
        }
        for (VehicleTemp vehicleTemp : vehicleTempListY) {
            nodePojosB.addAll(vehicleTemp.getVehicleServiceNodes());
        }
        int randomA = RandomInt.randomIntExceptZero(serviceNodesSize+1);
        int randomB = RandomInt.randomIntExceptZero(serviceNodesSize+1);
        int min = randomA <= randomB ? randomA : randomB;
        int max = randomA > randomB ? randomA : randomB;
        while (max - min < 1){
            randomA = RandomInt.randomIntExceptZero(serviceNodesSize+1);
            randomB = RandomInt.randomIntExceptZero(serviceNodesSize+1);
            min = randomA <= randomB ? randomA : randomB;
            max = randomA > randomB ? randomA : randomB;
        }
        List<Integer> aNeed = new ArrayList<>();
        List<Integer> bNeed = new ArrayList<>();
        for (int i = min; i < max; i++) {
            boolean statusA = true;
            boolean statusB = true;
            for (int j = min; j < max; j++) {
                if (nodePojosA.get(i).equals(nodePojosB.get(j))){
                    statusA = false;
                }
                if (nodePojosB.get(i).equals(nodePojosA.get(j))){
                    statusB = false;
                }
            }
            if (statusA){
                aNeed.add(i);
            }
            if (statusB){
                bNeed.add(i);
            }
        }
        for (int i = min; i < max; i++) {
            NodePojo nodePojo = nodePojosA.get(i);
            nodePojosA.set(i,nodePojosB.get(i));
            nodePojosB.set(i,nodePojo);
        }
        if (aNeed.size() > 0 && bNeed.size() > 0){
            adjustServiceNode(nodePojosA,nodePojosB,aNeed,bNeed);
        }
        //TODO 进行重新分配
        for (VehicleTemp tempListX : vehicleTempListX) {
            int count = tempListX.getServiceNodeCount();
            for (int i = 0; i < count; i++) {
                tempListX.getVehicleServiceNodes().set(i,nodePojosA.get(i));
            }
            for (int j = 0,m = 0;m == count;){
                nodePojosA.remove(j);
                m++;
            }
        }
        for (VehicleTemp tempListY : vehicleTempListY) {
            int count = tempListY.getServiceNodeCount();
            for (int i = 0; i < count; i++) {
                tempListY.getVehicleServiceNodes().set(i,nodePojosB.get(i));
            }
            for (int j = 0,m = 0;m == count;){
                nodePojosB.remove(j);
                m++;
            }
        }
    }

    /**
     * 调整双点交叉后的服务点
     * @param nodePojoA
     * @param nodePojoB
     */
    private void adjustServiceNode(List<NodePojo> nodePojoA, List<NodePojo> nodePojoB,List<Integer> aNeed,List<Integer> bNeed) {
        List<NodePojo> nodeATemp = new ArrayList<>();
        List<NodePojo> nodeBTemp = new ArrayList<>();
        nodeATemp.addAll(nodePojoA);
        nodeBTemp.addAll(nodePojoB);
        for (int b : bNeed) {
            List<Integer> aNeedTemp = new ArrayList<>();
            aNeedTemp.addAll(aNeed);
            int minDisNodeId = aNeedTemp.get(0);
            double distance = Double.MAX_VALUE;
            int minIndex = 0;
            for (int i = 0; i < aNeedTemp.size(); i++) {
                if ((b-1) == i || i == (b+1)){
                    continue;
                }
                try {
                    double temp = distanceDao.findDistanceByStartIdAndEndId(nodeATemp.get(b-1).getNodeId(), nodeBTemp.get(aNeedTemp.get(i)).getNodeId()).getDis() +
                            distanceDao.findDistanceByStartIdAndEndId(nodeBTemp.get(aNeedTemp.get(i)).getNodeId(), nodeATemp.get(b + 1).getNodeId()).getDis();
                    if (temp < distance) {
                        distance = temp;
                        minDisNodeId = aNeedTemp.get(i);
                        minIndex = i;
                    }
                }catch (Exception e){
                    continue;
                }
            }
            nodePojoA.set(b, nodePojoB.get(minDisNodeId));
            aNeedTemp.remove(minIndex);
        }
        for (int a : aNeed) {
            List<Integer> bNeedTemp = new ArrayList<>();
            bNeedTemp.addAll(bNeed);
            int minDisNodeId = bNeed.get(0);
            double distance = Double.MAX_VALUE;
            int minIndex = 0;
            for (int i = 0; i < bNeedTemp.size(); i++) {
                if (a == bNeedTemp.get(i) || bNeedTemp.get(i) == (a+1)){
                    continue;
                }
                System.out.println((a-1) +""+bNeedTemp.get(i) + "" + (a+1));
                try {
                    double temp = distanceDao.findDistanceByStartIdAndEndId(nodeBTemp.get(a-1).getNodeId(),nodeATemp.get(bNeedTemp.get(i)).getNodeId()).getDis()
                            + distanceDao.findDistanceByStartIdAndEndId(nodeATemp.get(bNeedTemp.get(i)).getNodeId(),nodeBTemp.get(a+1).getNodeId()).getDis();
                    if (temp < distance){
                        distance = temp;
                        minDisNodeId = bNeedTemp.get(i);
                        minIndex = i;
                    }
                }catch (Exception e){
                    continue;
                }
            }
            nodePojoB.set(a,nodePojoA.get(minDisNodeId));
            bNeedTemp.remove(minIndex);
        }
    }

    /**
     * 调整车辆服务点数量信息
     * @param vehicleTempList
     * @param serviceNodesSize
     */
    private void adjustVehicle(List<VehicleTemp> vehicleTempList,int serviceNodesSize) {
        int total = 0;
        //取出所有服务点，准备重新分配
        List<NodePojo> nodePojos = new ArrayList<>(serviceNodesSize);
        for (VehicleTemp vehicleTemp : vehicleTempList) {
            total += vehicleTemp.getServiceNodeCount();
            nodePojos.addAll(vehicleTemp.getVehicleServiceNodes());
        }
        System.out.println(total+"---"+serviceNodesSize);
        //调整服务点数量
        //求当前车辆服务点数量实际总和
        int serviceNodeSum = 0;
        //排个序容易筛选出最大最小
        vehicleTempList.sort(Comparator.comparingDouble(VehicleTemp::getServiceNodeCount));
        int maxIndex = vehicleTempList.size() - 1;
        int minIndex = 0;
        for (VehicleTemp vehicleTemp : vehicleTempList) {
            serviceNodeSum += vehicleTemp.getServiceNodeCount();
        }
        if (serviceNodeSum < serviceNodesSize){
            int old = vehicleTempList.get(minIndex).getServiceNodeCount();
            vehicleTempList.get(minIndex).setServiceNodeCount(old + serviceNodesSize - serviceNodeSum);
        }else if (serviceNodeSum > serviceNodesSize){
            int diff = serviceNodeSum - serviceNodesSize;
            int old = vehicleTempList.get(maxIndex).getServiceNodeCount();
            while (diff > 0){
                if (old > diff){
                    vehicleTempList.get(maxIndex).setServiceNodeCount(old - diff);
                }else {
                    vehicleTempList.get(maxIndex).setServiceNodeCount(1);
                    diff -= old - 1;
                    vehicleTempList.sort(Comparator.comparingDouble(VehicleTemp::getServiceNodeCount));
                }
            }
        }
        //进行遍历车辆重新分配点
        for (VehicleTemp vehicleTemp : vehicleTempList) {
            int count = vehicleTemp.getServiceNodeCount();
            for (int i = 0; i < count; i++) {
                vehicleTemp.getVehicleServiceNodes().set(i,nodePojos.get(i));
            }
            for (int j = 0,m = 0;m == count;){
                nodePojos.remove(j);
                m++;
            }
        }
    }

    /**
     * 轮盘赌算法
     * @param planList
     * @return
     */
    private int roulette(List<Plan> planList) {
        //求当前适应度总和
        double fitnessSum = fitnessSum(planList);
        int size = planList.size();
        double[] probability = new double[size];
        for (int i = 0; i < size; i++) {
            probability[i] = planList.get(i).getFitness() / fitnessSum;
        }
        //获取落点概率值
        double rand=Math.random();
        //pointer指示每个区段的右边界，从左往右扫描判断
        double pointer=0;
        for(int i = 0;i<probability.length;i++){
            pointer += probability[i];
            if(rand<=pointer) {
                return i;
            }
        }
        return -1;
    }


    private double fitnessSum(List<Plan> planList) {
        double fitnessSum = 0.0;
        for (Plan plan : planList) {
            fitnessSum += plan.getFitness();
        }
        return fitnessSum;
    }

    /**
     * 在执行算法之后执行的方法
     *
     * @param questionId
     */
    @Override
    public void afterExecute(int questionId) {
        Question question = questionDao.findQuestionByQuestionId(questionId);
        UserMessage userMessage = userDao.userMessage(String.valueOf(question.getUserId()));
        questionDao.updateNewGeneticExecuted(questionId, ProcessState.ALGORITHM_COMPLETED);
        generalAfterExecute(questionId,javaMailSender, userMessage.getEMail(), "优化版遗传算法");
    }

    private void insertDataBase(int questionId, List<Plan> finalPlans) {
        int num = 0;
        List<Double> fitness = new ArrayList<>();
        for (Plan finalPlan : finalPlans) {
            log.info("插入数据->{}",finalPlan.toString());
        }
        if (finalPlans.size() >= 5){
            //遍历五个最优方案
            for (int i = 0; num < 5; i++) {
                FinalSolution finalSolution = new FinalSolution();
                finalSolution.setVersion(VERSION);
                finalSolution.setQuestionId(questionId);
                finalSolution.setTotalDis(finalPlans.get(i).getTotalDistance());
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String nodeStr = formatter.format(now);
                finalSolution.setCreateTime(nodeStr);
                finalSolutionDao.insertFinalSolution(finalSolution);
                fitness.add(finalPlans.get(i).getFitness());
                num++;
                //进行solution的构建和插入,某个方案的所有路径
                List<Solution> solutionList = madeRoute(finalPlans.get(i), finalSolution.getFinalSolutionId());
                Collections.sort(solutionList,Comparator.comparing(Solution::getTotalDis));
                solutionList.forEach(solutionDao::insertSolution);
            }
        }
    }
    private List<Solution> madeRoute(Plan plan, int finalSolutionId) {
        List<Solution> list = new ArrayList<>();
        StringBuilder s;

        //路线数
        List<VehicleTemp> vehicleTemps = plan.getVehicleTempList();
        for (int i = 0; i < vehicleTemps.size(); i++) {
            s = new StringBuilder();
            List<NodePojo> node = vehicleTemps.get(i).getVehicleServiceNodes();
            //每条路线节点数
            for (int k = 0; k < node.size(); k++) {
                NodePojo b = node.get(k);
                s.append(b.getLng() + "," + b.getLat() + "," + b.getNodeAddress() + ";");
            }
            log.info(s.toString());
            Solution route = new Solution();
            route.setTotalTime(vehicleTemps.get(i).getRouteTime());
            route.setTotalDis(vehicleTemps.get(i).getRouteDistance());
            route.setRoute(s.toString());
            route.setFinalSolutionId(finalSolutionId);
            list.add(route);
        }
        return list;
    }

    @Override
    public void stop(int questionId) {
        Question question = questionDao.findQuestionByQuestionId(questionId);
        if (question == null) {
            throw new ParameterException("没有该ID的问题");
        }
        stopMap.put(questionId, true);
    }
}
