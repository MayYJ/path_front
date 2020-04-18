package com.may.routeplansystem.entity.po;

import com.may.routeplansystem.util.validation.insertAndUpdateGroup.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author :DengSiYuan
 * @date :2019/2/22 10:21
 * @desc :
 */
@Data
@ToString
@ApiModel
public class DemoSolution {

    @NotNull(message = "questionId不能为空",groups = Update.class)
    @ApiModelProperty("questionId")private Integer questionId;
    @NotNull(message = "userId不能为空",groups = Update.class)
    @ApiModelProperty("userId")private Integer userId;

}
