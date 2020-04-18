package com.may.routeplansystem.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class NodeVo {
    @ApiModelProperty("纬度") private double lat;
    @ApiModelProperty("经度") private double lng;
    @ApiModelProperty("点的详细地址") private String nodeAddr;
}
