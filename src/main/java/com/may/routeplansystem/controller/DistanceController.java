package com.may.routeplansystem.controller;

import com.may.routeplansystem.constant.Response;
import com.may.routeplansystem.entity.dto.ResponseEntity;
import com.may.routeplansystem.service.DistanceService;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import static com.may.routeplansystem.constant.ResponseStatu.SUCCESS;

@RequestMapping("distance")
@RestController
@Api(tags = "距离模块")
@Validated
public class DistanceController {

    @Resource
    private DistanceService distanceService;

    @GetMapping("generateDistance")
    @ApiOperation("得到该问题下所有点两两之间的距离")
    @ApiImplicitParam(name = "questionId", paramType = "query", value = "问题Id", required = true)
    public ResponseEntity generateDistance(@NotNull(message = "问题Id不能为空") Integer questionId) {
        distanceService.generateDistanceFromNode(questionId);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, null);
    }

    @GetMapping("stopGenerateDistance")
    @ApiOperation("停止准备距离数据")
    @ApiImplicitParam(name = "questionId", paramType = "query", value = "问题Id", required = true)
    public ResponseEntity stopGenerateDsitance(@NotNull(message = "问题Id不能为空") Integer questionId){
        distanceService.stopCalculateDistance(questionId);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, null);
    }
}
