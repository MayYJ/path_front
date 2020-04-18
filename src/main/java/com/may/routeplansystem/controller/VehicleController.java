package com.may.routeplansystem.controller;

import com.may.routeplansystem.constant.Response;
import com.may.routeplansystem.constant.SessionMessage;
import com.may.routeplansystem.entity.dto.ResponseEntity;
import com.may.routeplansystem.entity.po.VehicleMessage;
import com.may.routeplansystem.service.VehicleService;
import com.may.routeplansystem.util.validation.insertAndUpdateGroup.Insert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.may.routeplansystem.constant.ResponseStatu.SUCCESS;


/**
 * @author 10587
 */
@RestController
@RequestMapping(value = "vehicleSystem")
@Validated
@Api(tags = "车辆模块")
public class VehicleController {

    @Resource
    private VehicleService vehicleService;

    @PostMapping(value = "/user/vehicle")
    @ApiOperation("通过参数添加车辆")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "车辆类型", required = true),
            @ApiImplicitParam(name = "capacity", value = "车辆容量", required = true),
            @ApiImplicitParam(name = "oil", value = "车辆耗油量", required = true),
            @ApiImplicitParam(name = "price", value = "车辆价格", required = true),
            @ApiImplicitParam(name = "date", value = "车辆生产日期,格式为 ****-**-**", required = true),
            @ApiImplicitParam(name = "ownerId", value = "用户Id", required = true)
    })
    public ResponseEntity userVehicleRegister(@Validated(Insert.class) VehicleMessage vehicle, BindingResult result) {
        if (result.hasErrors()){
            String message = result.getAllErrors().get(0).getDefaultMessage();
            return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, message);
        }
        vehicleService.vehicleRegister(vehicle);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, null);
    }

    @GetMapping(value = "/user/vehicle")
    @ApiOperation("获取用户车辆信息 需要登录态")
    @ApiImplicitParam(name = "questionId",value = "问题id")
    public ResponseEntity<List<VehicleMessage>> userVehicleMessage(Integer questionId) {
        List<VehicleMessage> vehicleMessages = vehicleService.userVehicleMessage(questionId);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, vehicleMessages);
    }

    @PostMapping(value = "/excelVehicleInfo/{questionId}")
    @ApiOperation("通过Excel添加车辆")
    public ResponseEntity upload(@NotNull(message = "文件不能为空")
            @RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request,
                                 @PathVariable("questionId") Integer questionId) {
        return vehicleService.vehicleBatchImport(file, request, questionId);
    }

    @DeleteMapping(value = "/user/vehicle")
    @ApiOperation("删除车辆")
    @ApiImplicitParam(name = "vehicleIdList", value = "车辆Id数组 json形式")
    public ResponseEntity userVehicleDelete
            (@RequestBody @NotNull(message = "vehicleIdList 不能为空") List<Integer> vehicleIdList) {
        vehicleService.deleteVehicleBatch(vehicleIdList);
        return new ResponseEntity<>(SUCCESS, Response.SUCCESSFUL, null);
    }
}
