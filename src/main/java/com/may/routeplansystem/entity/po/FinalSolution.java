package com.may.routeplansystem.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class FinalSolution {
    @ApiModelProperty("方案Id") private int finalSolutionId;
    @ApiModelProperty("问题Id") private int questionId;
    @ApiModelProperty("总距离") private double totalDis;
    @ApiModelProperty("该方案属于的算法") private int version;
    @ApiModelProperty("用户是否选择这个方案为自己的理想方案的标志") private int userChoice;
    @ApiModelProperty("创建时间") private String createTime;
}
