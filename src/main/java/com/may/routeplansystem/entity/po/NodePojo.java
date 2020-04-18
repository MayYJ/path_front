package com.may.routeplansystem.entity.po;

import com.may.routeplansystem.util.validation.insertAndUpdateGroup.Insert;
import com.may.routeplansystem.util.validation.insertAndUpdateGroup.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * @author:dengsiyuan
 * @Date:2018/10/23 17:03
 */
@Data
@ApiModel
@ToString
public class NodePojo {

    @NotNull(message = "nodeId 不能为null")
    @ApiModelProperty("点Id") private Integer nodeId;
    @NotNull(message = "quesitonId 不能为空", groups = {Insert.class})
    @ApiModelProperty("问题Id") private Integer questionId;
    @NotNull(message = "nodeName 不能为空", groups = Insert.class)
    @Size(max = 20, message = "nodeName 不能超过20个字符", groups = Insert.class)
    @ApiModelProperty("点名称") private String nodeName;
    @NotNull(message = "nodeAddress 不能为空", groups = Insert.class)
    @ApiModelProperty("点详细地址") private String nodeAddress;
    @ApiModelProperty("纬度") private double lat;
    @ApiModelProperty("经度") private double lng;
    @ApiModelProperty("是不是中心点， 默认不是") private int isCenter;
    @ApiModelProperty("删除标志") private int delFlag;

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        NodePojo nodePojo = (NodePojo) o;
        return Objects.equals(nodeId, nodePojo.nodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId);
    }
}
