package com.may.routeplansystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.may.routeplansystem.constant.SessionMessage;
import com.may.routeplansystem.dao.DemoNodeDao;
import com.may.routeplansystem.entity.po.DemoNode;
import com.may.routeplansystem.entity.po.DemoSolution;
import com.may.routeplansystem.exception.NodeTransferException;
import com.may.routeplansystem.exception.ParameterException;
import com.may.routeplansystem.service.DemoNodeService;
import com.may.routeplansystem.service.util.ServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author :DengSiYuan
 * @date :2019/2/16 17:46
 * @desc :
 */
@Service
@Slf4j
public class DemoNodeImpl implements DemoNodeService {

    @Resource
    private DemoNodeDao demoNodeDao;

    /**
     * 点击二维坐标轴导入点
     *
     * @param demoNode
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertDemoNode(DemoNode demoNode, HttpSession session) {
        demoNode.setUserId(Integer.valueOf(String.valueOf(session.getAttribute(SessionMessage.USER_ID_KEY))));
        boolean flag = demoNodeDao.insertDemoNode(demoNode);
        ServiceUtil.checkSqlExecuted(flag);
    }

    /**
     * 导入二维坐标点数组
     *
     * @param demoNodesList
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertDemoNodesList(List<DemoNode> demoNodesList){
        boolean flag = demoNodeDao.insertDemoNodesList(demoNodesList);
        ServiceUtil.checkSqlExecuted(flag);
    }

    /**
     * 删除某个问题下的某个点
     *
     * @param posId
     * @param userId
     */
    @Override
    public void deleteDemoNode(int posId, int userId,int questionId) {
        if(questionId != 0 && posId != 0){
            boolean flag = demoNodeDao.deleteDemoNode(posId,userId,questionId);
            ServiceUtil.checkSqlExecuted(flag);
        }else{
            throw new ParameterException("请检查参数正确性");
        }

    }

    /**
     * 删除某个问题
     *
     * @param userId
     * @param questionId
     * */
    @Override
    public void deleteDemoQuestion(int userId,int questionId){
        if(questionId != 0){
            boolean flag = demoNodeDao.deleteDemoQuestion(userId,questionId);
            ServiceUtil.checkSqlExecuted(flag);
        }else {
            throw new ParameterException("请检查参数正确性");
        }

    }

    /**
     * 查询点的信息
     *
     * @param userId
     * @param questionId
     */
    @Override
    public List<DemoNode> selectAllDemoNodes(int userId, int questionId, Integer currentPage, Integer pageSize) {
        PageHelper.startPage(currentPage,pageSize);
        List<DemoNode> nodes = demoNodeDao.selectAllDemoNodes(userId, questionId);
        return nodes;
    }

    /**
     * 得到解决方案
     *
     * @param demoSolution
     */
    @Override
    public List<List> getDemoSolution(DemoSolution demoSolution) {
        List<DemoNode> serviceNodes = demoNodeDao.selectAllServiceNodes(demoSolution.getUserId(),
                demoSolution.getQuestionId());
        List<DemoNode> centerNotes = demoNodeDao.selectCenterNodes(demoSolution.getQuestionId(),
                demoSolution.getUserId());
        return getAllPaths(centerNotes,serviceNodes);
    }

    /**
     * 分组
     * @param centerNodes
     * @param serviceNodes
     * */
    private List<List> getAllPaths(List<DemoNode> centerNodes, List<DemoNode> serviceNodes){
        List<List> result = new ArrayList<>(16);
        int total = serviceNodes.size();
        if(total > 0){
            while(serviceNodes.size() > 0){
                result.add(getEveryPath(centerNodes,serviceNodes,total));
                total = serviceNodes.size();
            }
        }else{
            throw new NodeTransferException("点的数量少于一个");
        }
        return result;
    }

    /**
     * 每个路径中的点的信息
     * */
    private List<DemoNode> getEveryPath(List<DemoNode> centerNodes, List<DemoNode> serviceNodes, int total){
        List<DemoNode> path = new ArrayList<>();
        int centerNodeId = new Random().nextInt(centerNodes.size());
        path.add(centerNodes.get(centerNodeId));
        int sum = new Random().nextInt(total);
        for(int i = 0;i <= sum;i++){
            int nodeId = new Random().nextInt(serviceNodes.size());
            path.add(serviceNodes.get(nodeId));
            serviceNodes.remove(nodeId);
        }
        return path;
    }

    /**
     * 返回最大的questionId
     *
     * @param userId
     * @return questionId
     */
    @Override
    public Integer getMaxQuestionId(int userId) {
        Integer maxQuestion = demoNodeDao.selectMaxQuestionId(userId);
        if(maxQuestion != null){
            return maxQuestion;
        }else {
            return 0;
        }
    }
}
