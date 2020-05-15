package com.may.routeplansystem.service;

import com.may.routeplansystem.entity.dto.PageResponseEntity;
import com.may.routeplansystem.entity.po.Question;
import com.may.routeplansystem.util.Tupple;

import java.util.List;
import java.util.Map;

public interface QuestionService {

    /**
     * 添加问题
     */
    int insertQuestion(Question question);

    /**
     * 得到一个用户的所有问题，包括执行过算法和没有执行过算法
     *
     * @param userId 用户Id
     * @param currentPage
     * @param pageSize
     * @return
     */
    Tupple<List<Question>, Long> getQuestions(int userId, Integer currentPage, Integer pageSize);

    /**
     * 删除问题
     */
    void removeQuestion(int questionId);

    /**
     * 修改问题，其实就是修改问题的名称
     */
    void updateQuestion(Question question);

    /**
     * 复制问题，拥有对应quesition除了距离以外所有数据
     * @param questionId
     */
    void copyQuestion(int questionId) throws CloneNotSupportedException;

    Map<Integer, String> getExecutedAlgorithm(int questionId);

    Map<Integer, String> getExecutingAlgorithm(int questionId);

    Map<Integer, String> getNotExecuteAlgorithm(int questionId);
}
