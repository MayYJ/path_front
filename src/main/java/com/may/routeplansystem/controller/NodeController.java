package com.may.routeplansystem.controller;

import com.may.routeplansystem.constant.Response;
import com.may.routeplansystem.entity.dto.ResponseEntity;
import com.may.routeplansystem.entity.po.NodePojo;
import com.may.routeplansystem.service.NodeService;
import com.may.routeplansystem.util.validation.insertAndUpdateGroup.Insert;
import com.may.routeplansystem.util.validation.insertAndUpdateGroup.Update;
import io.swagger.annotations.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

import static com.may.routeplansystem.constant.ResponseStatu.SUCCESS;

/**
 * @author 10587
 */
@RestController
@RequestMapping("node")
@Api(tags = "中心点服务点模块")
@Validated
public class NodeController {

    @Resource
    private NodeService nodeService;

    @PostMapping(value = "newNode")
    @ApiOperation("通过点击地图方式导入点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionId", dataType = "int", value = "问题Id", required = true),
            @ApiImplicitParam(name = "nodeName", dataType = "string", value = "点名称", required = true),
            @ApiImplicitParam(name = "nodeAddress", dataType = "string", value = "点详细地址", required = true),
            @ApiImplicitParam(name = "lat", dataType = "double", value = "纬度", required = true),
            @ApiImplicitParam(name = "lng", dataType = "double", value = "经度", required = true),
            @ApiImplicitParam(name = "isCenter", dataType = "int", value = "是不是中心点, 默认不是")
    })
    public ResponseEntity insertNode(@Validated(Insert.class) NodePojo nodePojo, BindingResult result) {
        if (result.hasErrors()){
            String message = result.getAllErrors().get(0).getDefaultMessage();
            return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, message);
        }
        nodeService.insertNode(nodePojo);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, null);
    }

    @PostMapping("newNodeBatch")
    @ApiOperation("批量导入点信息")
    public ResponseEntity insertNodeBatch(@RequestBody List<NodePojo> nodePojos) {
        nodeService.insertNodeBatch(nodePojos);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, null);
    }

    @PostMapping(value = "excelNodeInfo/{questionId}")
    @ApiOperation("通过Excel导点")
    public ResponseEntity upload(@NotNull(message = "文件不能为空" )
                                 @RequestParam(value = "file", required = false)
                                         MultipartFile file, HttpServletRequest request,
                                 @PathVariable("questionId") int questionId) {
        return nodeService.nodeBatchImport(file, request, questionId);
    }


    @DeleteMapping("deleteNode")
    @ApiOperation("删除指定点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nodeId", value = "点Id", dataType = "int", required = true),
            @ApiImplicitParam(name = "questionId", value = "问题Id", dataType = "int", required = true)
    })
    public ResponseEntity deleteNode(NodePojo nodePojo) {
        nodeService.deleteNodeByNodeId(nodePojo.getQuestionId(), nodePojo.getNodeId());
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, null);
    }


    @PatchMapping("updateNode")
    @ApiOperation("修改点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nodeId", dataType = "int", value = "点Id", required = true),
            @ApiImplicitParam(name = "questionId", dataType = "int", value = "服务点Id", required = true),
            @ApiImplicitParam(name = "nodeName", dataType = "string", value = "点名称", required = true),
            @ApiImplicitParam(name = "nodeAddress", dataType = "string", value = "点详细地址", required = true),
            @ApiImplicitParam(name = "lat", dataType = "double", value = "纬度", required = true),
            @ApiImplicitParam(name = "lng", dataType = "double", value = "经度", required = true),
            @ApiImplicitParam(name = "isCenter", dataType = "int", value = "是不是中心点, 默认不是")
    })
    public ResponseEntity updateNode(@Validated(Update.class) NodePojo nodePojo, BindingResult result) {
        if (result.hasErrors()){
            String message = result.getAllErrors().get(0).getDefaultMessage();
            return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, message);
        }
        nodeService.updateNode(nodePojo);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, null);
    }

    @GetMapping("getQuestionNodes")
    @ApiOperation("获取某个问题所有的点信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "questionId", value = "问题Id", paramType = "query"),
            @ApiImplicitParam(name = "currentPage",dataType = "int",value = "当前页码",required = true),
            @ApiImplicitParam(name = "pageSize",dataType = "int",value = "当前页容量",required = true)
    })
    @Validated
    public ResponseEntity<List<NodePojo>> getQuestionNodes(@NotNull(message = "questionId 不能为空") Integer questionId) {
        List<NodePojo> nodes = nodeService.getQuestionNodes(questionId);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, nodes);
    }
}