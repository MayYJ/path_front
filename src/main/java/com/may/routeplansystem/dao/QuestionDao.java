package com.may.routeplansystem.dao;

import com.may.routeplansystem.entity.po.Question;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 10587
 */
@Repository
public interface QuestionDao {

    boolean insertQuestion(Question question);

    boolean deleteQuestion(int questionId);

    boolean updateQuestion(Question question);

    List<Question> findQuestionsByUserId(int userId);

    Question findQuestionByQuestionId(int questionId);

    Question findQuestionByQuestionNameAndUserId(@Param("questionName") String questionName,
                                                 @Param("userId") int userId);

    boolean updateQuestionProcessState(@Param("processState") int processState,
                                       @Param("questionId") int questionId);

    boolean updateSimpleExecuted(@Param("questionId") int questionId, @Param("state") int state);

    boolean updateGeneticExecuted(@Param("questionId") int questionId, @Param("state") int state);
    boolean updateNewGeneticExecuted(@Param("questionId") int questionId, @Param("state") int state);
    boolean test();
}
