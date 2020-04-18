package com.may.routeplansystem.entity.po;

import com.may.routeplansystem.util.validation.insertAndUpdateGroup.Insert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author :DengSiYuan
 * @date :2019/2/15 16:43
 * @desc : 二维坐标点Bean
 */

@Data
@ToString
@ApiModel
public class DemoNode {

    @ApiModelProperty("点Id")private Integer posId;
    @NotNull(message = "x轴坐标不能为Null",groups = Insert.class)
    @ApiModelProperty("X轴坐标")private double posX;
    @NotNull(message = "y轴坐标不能为Null",groups = Insert.class)
    @ApiModelProperty("Y轴坐标")private double posY;
    @ApiModelProperty("是否为中心点，默认为0不是中心点")private Integer isCenter;
    @NotNull(message = "未登录，登陆后操作",groups = Insert.class)
    @ApiModelProperty("用户Id")private Integer userId;
    @ApiModelProperty("问题编号")private Integer questionId;
}
