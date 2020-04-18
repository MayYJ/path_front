package com.may.routeplansystem.dao;

import com.may.routeplansystem.entity.po.Question;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@Transactional
public class QuestionDaoTest {

    @Resource
    private QuestionDao questionDao;

    @Test
    public void insertQuestion() {
    }

    @Test
    public void deleteQuestion() {
    }

    @Test
    public void updateQuestion() {
    }

    @Test
    public void findQuestionsByUserId() {
    }

    @Test
    public void findQuestionByQuestionId() {
        Question question = questionDao.findQuestionByQuestionId(1);
        assertEquals(0, question.getProcessState());
    }

    @Test
    public void updateSimpleExecuted() {

    }

    @Test
    public void updateGeneticeExecuted() {

    }
}