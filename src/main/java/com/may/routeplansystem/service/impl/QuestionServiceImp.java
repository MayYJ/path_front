package com.may.routeplansystem.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.may.routeplansystem.constant.ProcessState;
import com.may.routeplansystem.dao.*;
import com.may.routeplansystem.entity.po.NodePojo;
import com.may.routeplansystem.entity.po.Question;
import com.may.routeplansystem.entity.po.VehicleMessage;
import com.may.routeplansystem.exception.ParameterException;
import com.may.routeplansystem.service.*;
import com.may.routeplansystem.service.util.ServiceUtil;
import com.may.routeplansystem.util.Tupple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 10587
 */
@Service
public class QuestionServiceImp implements QuestionService {

    @Resource
    private QuestionDao questionDao;

    @Resource
    private FinalSolutionService finalSolutionService;

    @Resource
    private DistanceService distanceService;

    @Resource
    private NodeService nodeService;

    @Resource
    private NodeDao nodeDao;

    @Resource
    private VehicleService vehicleService;

    @Resource
    private VehicleDao vehicleDao;

    @Resource
    private FinalSolutionDao finalSolutionDao;

    @Resource
    private SolutionDao solutionDao;

    @Override
    public int insertQuestion(Question question) {
        checkQuestionNameDuplicate(question);
        questionDao.insertQuestion(question);
        return question.getQuestionId();
    }

    private void checkQuestionNameDuplicate(Question question) {
        String questionName = question.getQuestionName();
        int userId = question.getUserId();
        Question checkQuestion = questionDao.findQuestionByQuestionNameAndUserId(questionName, userId);
        if (checkQuestion != null) {
            throw new ParameterException("问题名称已经被使用过了");
        }
    }

    @Override
    public Tupple<List<Question>, Long> getQuestions(int userId, Integer currentPage, Integer pageSize) {
        PageHelper.startPage(currentPage,pageSize);
        List<Question> questions = questionDao.findQuestionsByUserId(userId);
        PageInfo<Question> pageInfo = new PageInfo<>(questions);
        long total = pageInfo.getTotal();
        return new Tupple<>(questions, total);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeQuestion(int questionId) {
        finalSolutionService.removeAllFinalSolutionByQuestionId(questionId);
        distanceService.deleteDistanceByQuestionId(questionId);
        NodePojo nodePojo = new NodePojo();
        nodePojo.setQuestionId(questionId);
        nodeService.deleteNodeByQuestionId(questionId);
        vehicleService.removeVehicleByQuestionId(questionId);
        questionDao.deleteQuestion(questionId);
    }

    @Override
    public void updateQuestion(Question question) {
        ServiceUtil.checkSqlExecuted(questionDao.updateQuestion(question));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyQuestion(int questionId) throws CloneNotSupportedException {
        Question question = questionDao.findQuestionByQuestionId(questionId);
        Question questionClone = (Question) question.clone();
        List<NodePojo> nodes = nodeDao.selectAllNodes(questionId);
        List<VehicleMessage> vehicleMessages = vehicleDao.searchVehicleByQuestionId(questionId);
        nodes.forEach(nodePojo -> nodePojo.setQuestionId(questionClone.getQuestionId()));
        nodeDao.insertNodeBatch(nodes);
        vehicleMessages.forEach(vehicle ->vehicle.setQuestionId(questionClone.getQuestionId()));
        vehicleDao.insertVehiclesBatch(vehicleMessages);
        List<Integer> solutionIds = finalSolutionDao.findAllFinalSolutionId(questionId);

        boolean deleteSolutionFlag = solutionIds.stream().allMatch(solutionid ->
                solutionDao.deleteSolution(solutionid));
        ServiceUtil.checkSqlExecuted(questionDao.insertQuestion(questionClone)
                && nodeDao.insertNodeBatch(nodes) && vehicleDao.insertVehiclesBatch(vehicleMessages)
                && finalSolutionDao.deleteFinalSolutionByQuestionId(questionId)
                && deleteSolutionFlag);
    }
    @Override
    public Map<Integer, String> getExecutedAlgorithm(int questionId) {
        return getAlgorithmStateMap(questionId, ProcessState.ALGORITHM_COMPLETED);
    }

    @Override
    public Map<Integer, String> getExecutingAlgorithm(int questionId) {
        return getAlgorithmStateMap(questionId, ProcessState.ALGORITHM_IS_PROCESSING);
    }

    @Override
    public Map<Integer, String> getNotExecuteAlgorithm(int questionId) {
        return getAlgorithmStateMap(questionId, ProcessState.ALGORITHM_NOT_EXECUTE);
    }

    public Map<Integer, String> getAlgorithmStateMap(int questionId, int state) {
        Question question = questionDao.findQuestionByQuestionId(questionId);
        Map<Integer,String> map = new HashMap<>();
        int generateState = question.getGeneticExecuted();
        int simpleState = question.getSimpleExecuted();
        int newGenerateState = question.getNewGeneticExecuted();
        if (generateState == state) {
            map.put(2, "遗传算法");
        }
        if (simpleState == state) {
            map.put(1, "简单算法");
        }
        if (newGenerateState == state) {
            map.put(3, "优化算法");
        }
        return map;
    }

}
