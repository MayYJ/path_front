package com.may.routeplansystem.service;

import com.may.routeplansystem.entity.po.DemoNode;
import com.may.routeplansystem.entity.po.DemoSolution;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author :DengSiYuan
 * @date :2019/2/15 16:27
 * @desc :
 */
@Service
public interface DemoNodeService {

    /**
     * 点击二维坐标轴导入点
     *
     * @param demoNode
     * @param session
     * */
    void insertDemoNode(DemoNode demoNode, HttpSession session);

    /**
     * 传入一个二维坐标数组
     *
     * @param demoNodesList
     * */
    void insertDemoNodesList(List<DemoNode> demoNodesList);

    /**
     * 删除某个问题下的某个点
     *
     * @param posId
     * @param userId
     * @param questionId
     * */
    void deleteDemoNode(int posId, int userId, int questionId);

    /**
     * 删除某个问题
     *
     * @param userId
     * @param questionId
     * */
    void deleteDemoQuestion(int userId, int questionId);

    /**
     * 查询点的信息
     *
     * @param userId
     * @param questionId
     * @param currentPage
     * @param pageSize
     * @return demoNode
     * */
    List<DemoNode> selectAllDemoNodes(int userId, int questionId, Integer currentPage, Integer pageSize);

    /**
     * 得到解决方案
     *
     * @param demoSolution
     * @return list
     * */
    List<List> getDemoSolution(DemoSolution demoSolution);

    /**
     * 返回最大的questionId
     * @param userId
     * @return questionId
     * */
    Integer getMaxQuestionId(int userId);
}
