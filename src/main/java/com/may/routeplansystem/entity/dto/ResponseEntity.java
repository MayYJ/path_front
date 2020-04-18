package com.may.routeplansystem.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class ResponseEntity<T> {
    @ApiModelProperty("0 成功; 1  参数错误; 2 没有登录; 3  文件传输数据错误;  4  \t\n" +
            "第三方服务错误;  5  服务器运行错误;  6  用户操作错误") private int status;
    @ApiModelProperty("提示信息") private String msg;
    @ApiModelProperty("业务数据") private T object;
}
