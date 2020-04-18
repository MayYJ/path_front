package com.may.routeplansystem.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author 10587
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel
public class Distance {
    @ApiModelProperty("距离Id") private int distanceId;
    @ApiModelProperty("所属问题Id") private int questionId;
    @ApiModelProperty("起始点Id") private int startNodeId;
    @ApiModelProperty("终止点Id") private int endNodeId;
    @ApiModelProperty("两点距离") private int dis;
    @ApiModelProperty("两点时间") private int time;
}
