package com.may.routeplansystem.service.impl;

import com.alibaba.fastjson.JSON;
import com.may.routeplansystem.constant.Constant;
import com.may.routeplansystem.constant.ExceptionMessage;
import com.may.routeplansystem.constant.ProcessState;
import com.may.routeplansystem.dao.DistanceDao;
import com.may.routeplansystem.dao.NodeDao;
import com.may.routeplansystem.dao.QuestionDao;
import com.may.routeplansystem.entity.po.Distance;
import com.may.routeplansystem.entity.po.NodePojo;
import com.may.routeplansystem.entity.po.Question;
import com.may.routeplansystem.entity.vo.DistanceJsonResponse;
import com.may.routeplansystem.exception.ProcessException;
import com.may.routeplansystem.exception.ThreeServiceException;
import com.may.routeplansystem.service.DistanceService;
import com.may.routeplansystem.service.util.ServiceUtil;
import com.may.routeplansystem.util.NetWorkUtil;
import com.may.routeplansystem.util.taskCommit.TaskCommit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author 10587
 */
@Service
@Slf4j
public class DistanceServiceImp implements DistanceService {

    @Resource
    private NodeDao nodeDao;

    @Resource
    private DistanceDao distanceDao;

    @Resource
    private QuestionDao questionDao;

    /**
     * 支持中断 计算距离 下次继续算
     */
    private Map<Integer, Integer> interupeNodeIdMap = new ConcurrentHashMap<>();


    private static Map<Integer, Boolean> map = new ConcurrentHashMap<>();

    @Override
    public void updateDisAndTime(int questionId) {
        List<Distance> distances = distanceDao.findUpdateDistances(questionId);
        distances.forEach(distance -> {
            setTimeAndDis(distance);
            ServiceUtil.checkSqlExecuted(distanceDao.updateDisAndTime(distance));
        });
    }

    @Override
    public void getDistanceTimeAndDisAndInsert(Distance distance) {
        setTimeAndDis(distance);
        boolean insertFlag = distanceDao.insertDis(distance);
        ServiceUtil.checkSqlExecuted(insertFlag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateDistanceFromNode(int questionId) {
        Question question = questionDao.findQuestionByQuestionId(questionId);

        if (question == null) {
            throw new ProcessException("请先创建问题");
        }
        if (question.getProcessState() == 4) {
            throw new ProcessException("距离已经计算完成不能再计算");
        }

        log.info("将任务丢进了线程池");
        Runnable runnable = () -> {
            if (question.getProcessState() < ProcessState.CALCULATING_DISTANCE) {
                questionDao.updateQuestionProcessState(ProcessState.CALCULATING_DISTANCE, questionId);
            }
            List<NodePojo> nodes = nodeDao.selectAllNodes(questionId);
            checkNodesExist(nodes);
            log.info("开始计算距离");
            for (int i = 0; i < nodes.size(); i++) {
                if (map.get(questionId) != null) {
                    break;
                }
                for (int i1 = 0; i1 < nodes.size(); i1++) {
                    if (map.get(questionId) != null) {
                        break;
                    }
                    NodePojo node1 = nodes.get(i);
                    NodePojo node2 = nodes.get(i1);
                    if (node1 != node2) {
                        Distance distance = new Distance();
                        distance.setStartNodeId(node1.getNodeId());
                        distance.setEndNodeId(node2.getNodeId());
                        distance.setQuestionId(node1.getQuestionId());
                        setTimeAndDis(distance);
                        distanceDao.insertDis(distance);
                    }
                }
            }
            log.info("距离计算完成");
            if (map.get(questionId) == null) {
                log.info("修改了ProcessState 为完成距离计算");
                questionDao.updateQuestionProcessState(ProcessState.COMPLETE_DISTANCE_PREPARE, questionId);
            }else {
                log.info("有停止的标志刪除所有Distance數據");
                distanceDao.removeDistanceByQuestionId(questionId);
                map.remove(questionId);
            }
        };
        TaskCommit.commitTask(runnable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDistanceByQuestionId(int questionId) {
        questionDao.updateQuestionProcessState(3, questionId);
        distanceDao.removeDistanceByQuestionId(questionId);
    }

    @Override
    public void stopCalculateDistance(int questionId) {
        Question question = questionDao.findQuestionByQuestionId(questionId);
        if (question.getProcessState() != ProcessState.CALCULATING_DISTANCE) {
            throw new ProcessException("没有正在准备数据， 无法停止");
        }
        if (map.get(questionId) != null) {
            throw new ProcessException("正在停止距离准备");
        }
        map.put(questionId, true);
    }

    private void checkNodesExist(List<NodePojo> nodePojos) {
        if (nodePojos.isEmpty()) {
            throw new ProcessException(ExceptionMessage.NOT_NODES);
        }
    }

    private Distance setTimeAndDis(Distance distance) {
        Objects.requireNonNull(distance);
        String responseStr = getParseStr(distance);
        DistanceJsonResponse response = JSON.parseObject(responseStr, DistanceJsonResponse.class);
        checkResponse(response);
        distance.setDis(response.getResult().get(0).getDistance().getValue());
        distance.setTime(response.getResult().get(0).getDuration().getValue());
        return distance;
    }

    private void checkResponse(DistanceJsonResponse response) {
        if (response.getStatus() > 0) {
            throw new ThreeServiceException("百度API服务 获取失败");
        }
        Objects.requireNonNull(response, "JSON 有误");
        Objects.requireNonNull(response.getResult(), "JSON 有误");
        Objects.requireNonNull(response.getResult().get(0), "JSON 有误");
        Objects.requireNonNull(response.getResult().get(0).getDistance(), "JSON 有误");
        Objects.requireNonNull(response.getResult().get(0).getDuration(), "JSON 有误");
    }

    private String getParseStr(Distance distance) {
        Question question = questionDao.findQuestionByQuestionId(distance.getQuestionId());
        try {
            log.info(distance.getStartNodeId() + " 起始点");
            NodePojo startNode = nodeDao.selectNodeByNodeId(distance.getStartNodeId());
            NodePojo endNode = nodeDao.selectNodeByNodeId(distance.getEndNodeId());
            Objects.requireNonNull(endNode);
            Objects.requireNonNull(startNode);
            String startNodePositionStr = startNode.getLat() + "," + startNode.getLng();
            String endNodePostionStr = endNode.getLat() + "," + endNode.getLng();
            String url = "http://api.map.baidu.com/routematrix/v2/driving?output=json&origins=" + startNodePositionStr +
                    "&destinations=" + endNodePostionStr + "&ak=" + Constant.BAIDUMAP_AK_MAY;
            return NetWorkUtil.visitUrl(url);
        } catch (IOException e) {
            throw new ThreeServiceException("通过百度地图API 获取两点之间的距离失败");
        }
    }
}
