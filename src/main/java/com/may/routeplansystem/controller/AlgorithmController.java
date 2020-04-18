package com.may.routeplansystem.controller;

import com.may.routeplansystem.algorithm.AlgorithmContext;
import com.may.routeplansystem.constant.Response;
import com.may.routeplansystem.entity.dto.ResponseEntity;
import com.may.routeplansystem.service.QuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import java.util.Map;

import static com.may.routeplansystem.constant.ResponseStatu.SUCCESS;

/**
 * @author 10587
 */
@RestController("algorithm")
@Api(tags = "算法")
@Validated
public class AlgorithmController {

    @Resource
    private AlgorithmContext algorithmContext;

    @Resource
    private QuestionService questionService;

    @GetMapping("executeAlgorithm")
    @ApiOperation("选择算法并执行")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", paramType = "query", value = "1 表示执行简单算法; 2 表示执行遗传算法", required = true),
            @ApiImplicitParam(name = "questionId",paramType = "query", value = "问题Id", required = true)
    })
    public ResponseEntity executeAlgorithm(@NotNull(message = "算法key值不能为空") Integer key,
                                           @NotNull(message = "问题Id不能为空") Integer questionId) {
        System.out.println("数据进来了！");
        algorithmContext.execute(key, questionId);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, null);
    }

    @GetMapping("stopAlgorithm")
    @ApiOperation("停止指定问题的指定算法")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", paramType = "query", value = "1 表示执行简单算法; 2 表示执行遗传算法", required = true),
            @ApiImplicitParam(name = "questionId",paramType = "query", value = "问题Id", required = true)
    })
    public ResponseEntity stopAlgorithm(@NotNull(message = "算法key值不能为空")int key,
                                        @NotNull(message = "问题Id不能为空")int questionId) {
        algorithmContext.stop(questionId, key);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, null);
    }

    @GetMapping("getAllAlgorithmNames")
    @ApiOperation("获得算法键 和 算法名")
    public ResponseEntity getAllAlgorithmNames() {
        return new ResponseEntity<Map>(SUCCESS, Response.SUCCESSFUL, algorithmContext.getNameMap());
    }

    @GetMapping("getExecutedAlgorithm")
    @ApiOperation("获得执行过的算法")
    public ResponseEntity getExecutedAlgorithm(int questionId) {
        Map<Integer,String> map = questionService.getExecutedAlgorithm(questionId);
        return new ResponseEntity<Map>(SUCCESS, Response.SUCCESSFUL, map);
    }

    @GetMapping("getExecutingAlgorithm")
    @ApiOperation("获得执行过的算法")
    public ResponseEntity getExecutingAlgorithm(int questionId) {
        Map<Integer,String> map = questionService.getExecutingAlgorithm(questionId);
        return new ResponseEntity<Map>(SUCCESS, Response.SUCCESSFUL, map);
    }

    @GetMapping("getNotExecutingAlgorithm")
    public ResponseEntity getNotExecutAlgorithm(int questionId) {
        Map<Integer, String> map = questionService.getNotExecuteAlgorithm(questionId);
        return new ResponseEntity<Map>(SUCCESS, Response.SUCCESSFUL, map);
    }
}
