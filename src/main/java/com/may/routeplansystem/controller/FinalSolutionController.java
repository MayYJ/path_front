package com.may.routeplansystem.controller;

import com.may.routeplansystem.algorithm.AlgorithmContext;
import com.may.routeplansystem.constant.Response;
import com.may.routeplansystem.dao.QuestionDao;
import com.may.routeplansystem.entity.dto.ResponseEntity;
import com.may.routeplansystem.entity.vo.FinalSolutionVo;
import com.may.routeplansystem.service.FinalSolutionService;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.may.routeplansystem.constant.ResponseStatu.SUCCESS;

/**
 * @author 10587
 */
@RestController
@RequestMapping("finalSolution")
@Api(tags = "方案模块")
@Validated
public class FinalSolutionController {

    @Resource
    private FinalSolutionService finalSolutionService;

    @Resource
    private QuestionDao questionDao;

    @Resource
    private AlgorithmContext algorithmContext;


    @GetMapping("getAllFinalSolution")
    @ApiOperation("得到一个问题的所有方案")
    @ApiImplicitParam(name = "questionId",paramType = "query", value = "问题ID", required = true)
    public ResponseEntity<List<FinalSolutionVo>> getAllFinalSolution
            (@NotNull(message = "questionId 不能为空") Integer questionId) {
        List<FinalSolutionVo> finalSolutionVos = finalSolutionService.getAllFinalSolutionOrdered(questionId);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, finalSolutionVos);
    }

    @GetMapping("getFinalSolution")
    @ApiOperation("获取一个方案的详细信息")
    @ApiImplicitParam(name = "finalSolutionId",paramType = "query", value = "方案Id", required = true)
    public ResponseEntity<FinalSolutionVo> getFinalSolution
            (@NotNull(message = "finalSolutionId 不能为空") Integer finalSolutionId) {
        FinalSolutionVo finalSolutionVo = finalSolutionService.getFinalSolution(finalSolutionId);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, finalSolutionVo);
    }

    @DeleteMapping("removeFinalSolution")
    @ApiOperation("删除一个方案")
    @ApiImplicitParam(name = "finalSolutionId", dataType = "int", value = "方案Id", required = true, paramType = "query")
    public ResponseEntity removeFinalSolution
            (@NotNull(message = "finalSolutionId 不能为空") Integer finalSolutionId) {
        finalSolutionService.removeFinalSolution(finalSolutionId);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, null);
    }

    @DeleteMapping("removeAllQuestionFinalSolution")
    @ApiOperation("删除一个问题的所有方案")
    @ApiImplicitParam(name = "questionId", dataType = "int", value = "方案Id", required = true, paramType = "query")
    public ResponseEntity removeAllQuestionFinalSolution
            (@NotNull(message = "questionId 不能为空") Integer questionId) {
        questionDao.updateGeneticExecuted(questionId, 0);
        questionDao.updateSimpleExecuted(questionId, 0);
        finalSolutionService.removeAllFinalSolutionByQuestionId(questionId);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, null);
    }

    @GetMapping("getOneVersionFinalSolution")
    @ApiOperation("获取一个问题下某个算法的所有方案")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionId", paramType = "query", value = "问题Id", required = true),
            @ApiImplicitParam(name = "key", paramType = "query", value = "1 表示简单算法 2 表示遗传算法", required = true)
    })
    public ResponseEntity<List<FinalSolutionVo>> getOneVersionFinalSolution
            (@NotNull(message = "questionId 不能为空") Integer questionId,
             @NotNull(message = "key 不能为空")Integer key) {
        List<FinalSolutionVo> finalSolutionVos =
                finalSolutionService.getOneVersionFinalSolution(questionId, key);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, finalSolutionVos);
    }

    @GetMapping("getAllVersion")
    @ApiOperation("获取算法数量")
    public ResponseEntity<Integer> getAllVersion() {
        int num = algorithmContext.getMap().size();
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, num);
    }

    @DeleteMapping("removeOneVersionFinalSolution")
    @ApiOperation("删除一个问题一个算法的所有方案")
    public ResponseEntity removeOneVersionFinalSolution(int questionId, int version) {
        if (version == 1) {
            questionDao.updateSimpleExecuted(questionId, 0);
        }
        if (version == 2) {
            questionDao.updateGeneticExecuted(questionId, 0);
        }
        finalSolutionService.removeOneVersionFinalSoltuion(questionId, version);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, null);
    }
}
