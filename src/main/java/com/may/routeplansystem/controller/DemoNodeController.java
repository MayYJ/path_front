package com.may.routeplansystem.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.may.routeplansystem.constant.Response;
import com.may.routeplansystem.entity.dto.ResponseEntity;
import com.may.routeplansystem.entity.po.DemoNode;
import com.may.routeplansystem.entity.po.DemoSolution;
import com.may.routeplansystem.entity.po.JsonArray;
import com.may.routeplansystem.service.DemoNodeService;
import com.may.routeplansystem.util.validation.insertAndUpdateGroup.Insert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

import static com.may.routeplansystem.constant.ResponseStatu.SUCCESS;

/**
 * @author :DengSiYuan
 * @date :2019/2/15 16:22
 * @desc : 这是二维演示坐标导点的Controller
 */

@RestController
@RequestMapping("demoNode")
@Api(tags = "二维中心点服务点模块")
public class DemoNodeController {

    @Resource
    private DemoNodeService demoNodeService;

    @PostMapping("demoNewNode")
    @ApiOperation("通过点击二维坐标点导入点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "posId",dataType = "int",value = "点的编号",required = true),
            @ApiImplicitParam(name = "posX",dataType = "double",value = "X轴坐标",required = true),
            @ApiImplicitParam(name = "posY",dataType = "double",value = "y轴坐标",required = true),
            @ApiImplicitParam(name = "isCenter",dataType = "int",value = "是不是中心点，默认不是"),
            @ApiImplicitParam(name = "questionId",dataType = "int",value = "问题编号")
    })
    public ResponseEntity insertDemoNode(@Validated(Insert.class)DemoNode demoNode,
                                         BindingResult result,
                                         HttpSession session){
        if(result.hasErrors()){
            String message = result.getAllErrors().get(0).getDefaultMessage();
            return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL,message);
        }
        demoNodeService.insertDemoNode(demoNode,session);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL,null);
    }

    @PostMapping("demoNewNodes")
    @ApiOperation(value = "点击地图，传二维坐标群",consumes = "application/json")
    public ResponseEntity insertDemoNodes(@RequestBody JsonArray jsonArray){
        List<DemoNode> demoNodesList = JSONArray.toJavaObject(jsonArray.getJsonArray(),List.class);
        demoNodeService.insertDemoNodesList(demoNodesList);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL,null);
    }

    @DeleteMapping("demoDeleteNode")
    @ApiOperation("删除一个问题下的某个点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "posId",dataType = "int",value = "点的编号",required = true),
            @ApiImplicitParam(name = "userId",dataType = "int",value = "用户Id",required = true),
            @ApiImplicitParam(name = "questionId",dataType = "int",value = "问题编号",required = true)
    })
    public ResponseEntity deleteDemoNode(Integer posId, Integer userId, Integer questionId){
        demoNodeService.deleteDemoNode(posId,userId,questionId);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL,null);
    }

    @DeleteMapping("demoDeleteQuestion")
    @ApiOperation("删除一个问题（包含所有点）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",dataType = "int",value = "用户Id",required = true),
            @ApiImplicitParam(name = "questionId",dataType = "int",value = "问题编号",required = true)
    })
    public ResponseEntity deleteDemoQuestion(Integer userId, Integer questionId){
        demoNodeService.deleteDemoQuestion(userId,questionId);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL,null);
    }

    @GetMapping("demoSelectAllDemoNodes")
    @ApiOperation("展示所有点的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",dataType = "int",value = "用户Id",required = true),
            @ApiImplicitParam(name = "questionId",dataType = "int",value = "问题编号",required = true),
            @ApiImplicitParam(name = "currentPage",dataType = "int",value = "当前页码",required = true),
            @ApiImplicitParam(name = "pageSize",dataType = "int",value = "当前页容量",required = true)
    })
    public ResponseEntity selectAllDemoNodes(Integer userId, Integer questionId,Integer currentPage,Integer pageSize){
        List<DemoNode> nodes = demoNodeService.selectAllDemoNodes(userId,questionId,currentPage,pageSize);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL,nodes);
    }

    @PostMapping("demoSolution")
    @ApiOperation("生成路线图")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionId",dataType = "int",value = "问题编号",required = true),
            @ApiImplicitParam(name = "userId",dataType = "int",value = "用户Id",required = true)
    })
    public ResponseEntity getDemoSolution(DemoSolution demoSolution){
        System.out.println(demoSolution.toString());
        if (demoSolution != null){
            List<List> result = demoNodeService.getDemoSolution(demoSolution);
            return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL,result);
        }else {
            return new ResponseEntity<>(SUCCESS,Response.SUCCESSFUL,"参数不能为空");
        }
    }

    @GetMapping("demoNodeMaxQuestionId")
    @ApiOperation("当前用户最大问题号")
    @ApiImplicitParam(name = "userId",dataType = "int",value = "用户Id",required = true)
    public ResponseEntity getMaxQuestionId(Integer userId){
        if (userId == null){
            return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL,"userId为空");
        }else {
            return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL,demoNodeService.getMaxQuestionId(userId));
        }

    }
}
