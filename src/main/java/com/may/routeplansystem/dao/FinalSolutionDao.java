package com.may.routeplansystem.dao;

import com.may.routeplansystem.entity.po.FinalSolution;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinalSolutionDao {

    /**
     * 添加方案
     *
     * @param finalSolution
     * @return 插入方案的id
     */
    boolean insertFinalSolution(FinalSolution finalSolution);

    boolean deleteFinalSolution(int finalSolutionId);

    boolean deleteOneVersionFinalSolution(@Param("questionId") int questionId, @Param("version") int version);

    boolean deleteFinalSolutionByQuestionId(int questionId);
    /**
     * 得到问题的最大版本号
     *
     * @param questionId
     * @return
     */
    Integer findMaxVersion(int questionId);

    /**
     * 得到一个问题所有版本号
     *
     * @param questionId
     * @return
     */
    List<Integer> findAllVersion(int questionId);

    /**
     * 找到所有版本中最好的四个方案
     *
     * @param questionId
     * @return 有序的方案
     */
    List<Integer> findAllFinalSolutionOrdered(int questionId);

    /**
     * 查找到所有属于问题的解决方案的id
     *
     * @param questionId
     * @return
     */
    List<Integer> findAllFinalSolutionId(int questionId);

    /**
     * 找到一个问题的一个版本的解决方案
     *
     * @param questionId 问题id
     * @param version    版本
     * @return
     */
    List<Integer> findDifferentVersionFinalSolution(@Param("questionId") int questionId,
                                                    @Param("version") int version);

    /**
     * 通过finalSolutionId找到方案
     *
     * @param finalSolutionId
     * @return
     */
    FinalSolution findFinalSolutionByFinalSolutionId(int finalSolutionId);

    /**
     * 修改被用户选中的方案的状态
     *
     * @param finalSolutionId
     * @return
     */
    boolean updateFinalSolutionUserChoice(int finalSolutionId);

    /**
     * 得到被用户选中的方案的数量
     *
     * @return
     * @throws com.may.routeplansystem.exception.FinalSolutionUserChoiceException
     */
    int findNumOfUserChoice(int questionId);

}
