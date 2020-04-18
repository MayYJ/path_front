package com.may.routeplansystem.dao;

import com.may.routeplansystem.entity.po.DemoNode;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author :DengSiYuan
 * @date :2019/2/16 17:52
 * @desc :
 */
@Repository("DemoNodeDao")
public interface DemoNodeDao {

    /**
     * 导入二维坐标点
     * @param demoNode
     * @return true:导入成功 false:导入失败
     * */
    boolean insertDemoNode(DemoNode demoNode);

    /**
     * 导入二维坐标数组
     * @param demoNodesList
     * @return true:导入成功 false:导入失败
     * */
    boolean insertDemoNodesList(List<DemoNode> demoNodesList);

    /**
     * 查询所有二维坐标点
     * @param userId
     * @param questionId
     * @return demoNode
     * */
    List<DemoNode> selectAllDemoNodes(@Param("userId") int userId,
                                      @Param("questionId") int questionId);

    /**
     * 得到一个问题下的所有服务点的信息
     * @param userId
     * @param questionId
     * @return list
     * */
    List<DemoNode> selectAllServiceNodes(@Param("userId") int userId,
                                         @Param("questionId") int questionId);

    /**
     * 得到某个问题的中心点
     * @param questionId
     * @param userId
     * @return demoNode
     * */
    List<DemoNode> selectCenterNodes(@Param("questionId") int questionId,
                                     @Param("userId") int userId);

    /**
     * 删除某个二维坐标点
     * @param posId
     * @param userId
     * @param questionId
     * @return true:删除成功 false:删除失败
     */
    boolean deleteDemoNode(@Param("posId") int posId,
                           @Param("userId") int userId,
                           @Param("questionId") int questionId);

    /**
     * 删除某个问题
     *
     * @param userId
     * @param questionId
     * @return true:删除成功 false:删除失败
     * */
    boolean deleteDemoQuestion(@Param("userId") int userId,
                               @Param("questionId") int questionId);

    /**
     * 更新某个点的信息
     * @param posId
     * @param userId
     * @return true:更新成功 false:更新失败
     * */
    boolean updateDemoNode(@Param("posId") int posId, @Param("userId") int userId);

    /**
     * 查询当前的最大问题数
     * @param userId
     * @return int
     * */
    Integer selectMaxQuestionId(int userId);

}
