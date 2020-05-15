package com.may.routeplansystem.algorithm;

import com.may.routeplansystem.constant.ProcessState;
import com.may.routeplansystem.dao.QuestionDao;
import com.may.routeplansystem.entity.po.Question;
import com.may.routeplansystem.exception.ProcessException;
import com.may.routeplansystem.util.taskCommit.TaskCommit;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

import static com.may.routeplansystem.constant.Constant.NCPU;

/**
 * @author 10587
 */
public abstract class Algorithm implements AlgorithmExecutor {

    @Resource
    private QuestionDao questionDao;

    /**
     * 在执行算法之前执行的方法
     * @param questionId
     */
    protected abstract void beforeExecute(int questionId);

    /**
     * 执行算法
     * @param questionId
     */
    protected abstract void executeAlgorithm(int questionId);

    /**
     * 在执行算法之后执行的方法
     * @param questionId
     */
    protected abstract void afterExecute(int questionId);


    @Override
    public void execute(int questionId){
        System.out.println("开始要执行了");
        beforeExecute(questionId);
        Runnable runnable = () -> {
            executeAlgorithm(questionId);
            afterExecute(questionId);
        };
        TaskCommit.commitTask(runnable);
    }

    protected void generalBeforeExecute(Question question, String algorithmName) {
        int processState = question.getProcessState();
        if (processState < ProcessState.COMPLETE_DISTANCE_PREPARE) {
            throw new ProcessException("前面的数据还没有准备好");
        }
        questionDao.updateQuestionProcessState(ProcessState.SOME_ALGORITHM_PROCESSING, question.getQuestionId());
    }


    protected void generalAfterExecute(int questionId, JavaMailSender mailSender,
                                       String userEmail, String algorithm) {
        questionDao.updateQuestionProcessState(ProcessState.COMPLETE_DISTANCE_PREPARE, questionId);
//        sendMail(userEmail, questionId, algorithm, mailSender);
    }

    private void sendMail(String toMail, int questionId,
                          String algorithm, JavaMailSender mailSender) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("campus.mis@foxmail.com");
        simpleMailMessage.setTo(toMail);
        simpleMailMessage.setSubject("路径规划系统通知");
        String text = "您问题编号为 " + questionId + " 执行" + algorithm +"已经完成";
        simpleMailMessage.setText(text);
        mailSender.send(simpleMailMessage);
    }


}
