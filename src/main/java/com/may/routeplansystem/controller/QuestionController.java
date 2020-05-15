package com.may.routeplansystem.controller;

import com.may.routeplansystem.cache.Cache;
import com.may.routeplansystem.constant.Response;
import com.may.routeplansystem.dao.QuestionDao;
import com.may.routeplansystem.entity.dto.PageResponseEntity;
import com.may.routeplansystem.entity.dto.ResponseEntity;
import com.may.routeplansystem.entity.po.Question;
import com.may.routeplansystem.service.QuestionService;
import com.may.routeplansystem.util.Tupple;
import com.may.routeplansystem.util.validation.insertAndUpdateGroup.Insert;
import com.may.routeplansystem.util.validation.insertAndUpdateGroup.Update;
import io.swagger.annotations.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.may.routeplansystem.constant.ResponseStatu.PARMETER_INVALIATE;
import static com.may.routeplansystem.constant.ResponseStatu.SUCCESS;

/**
 * @author 10587
 */
@RestController
@RequestMapping("question")
@Api(tags = "问题模块")
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @PostMapping("insertQuestion")
    @ApiOperation("添加问题")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionName",value = "问题名称", required = true),
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true)
    })
    public ResponseEntity insertQuestion(@Validated(Insert.class) Question question, BindingResult result) {
        if (result.hasErrors()){
            String message = result.getAllErrors().get(0).getDefaultMessage();
            return new ResponseEntity<>(PARMETER_INVALIATE, message, null);
        }
        int questionId = questionService.insertQuestion(question);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, questionId);
    }

    @Validated
    @GetMapping("getQuestions")
    @ApiOperation("获取该用户所有问题信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", paramType = "query"),
            @ApiImplicitParam(name = "currentPage",dataType = "int",value = "当前页码",required = true),
            @ApiImplicitParam(name = "pageSize",dataType = "int",value = "当前页容量",required = true)
    })

    public PageResponseEntity<List<Question>> getQuestions
            (@NotNull(message = "userId不能为空") @RequestParam(value = "userId") Integer userId,
             Integer currentPage,Integer pageSize) {
        if (currentPage == null || pageSize == null){
            currentPage = 1;
            pageSize = 5;
        }
        Tupple<List<Question>, Long> dataAndTotal = questionService.getQuestions(userId,currentPage,pageSize);
        return new PageResponseEntity<>(dataAndTotal.getT(), dataAndTotal.getR(), SUCCESS, Response.SUCCESSFUL);
    }

    @Validated
    @DeleteMapping("removeQuestion")
    @ApiOperation("删除问题")
    @ApiImplicitParam(name = "questionId", dataType = "int", value = "问题Id", paramType = "query")
    public ResponseEntity removeQuestion(@NotNull(message = "questionId不能为空") int questionId) {
        questionService.removeQuestion(questionId);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, null);
    }

    @PatchMapping("updateQuestion")
    @ApiOperation("修改问题信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionId", dataType = "int", value = "问题Id"),
            @ApiImplicitParam(name = "questionName", dataType = "string", value = "问题名称"),
    })
    public ResponseEntity updateQuestion(@Validated(Update.class) Question question, BindingResult result) {
        if (result.hasErrors()){
            String message = result.getAllErrors().get(0).getDefaultMessage();
            return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, message);
        }
        questionService.updateQuestion(question);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, null);
    }

    @GetMapping("copyQuestion")
    @ApiOperation("复制问题以及其下车辆和点数据")
    @ApiImplicitParam(name = "questionId", value = "问题Id")
    public ResponseEntity copyQuestion(Integer quetionId) throws CloneNotSupportedException {
        questionService.copyQuestion(quetionId);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, null);
    }

    @Resource
    private QuestionDao questionDao;

    @GetMapping("test")
    public ResponseEntity test() {
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, questionDao.test());
    }
}
