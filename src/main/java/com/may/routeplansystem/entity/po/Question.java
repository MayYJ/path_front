package com.may.routeplansystem.entity.po;

import com.may.routeplansystem.util.validation.insertAndUpdateGroup.Insert;
import com.may.routeplansystem.util.validation.insertAndUpdateGroup.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author 10587
 */
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
@ToString
public class Question implements Cloneable {

    @NotNull(message = "questionId 不能为空", groups = Update.class)
    @ApiModelProperty("问题Id") private int questionId;
    @NotNull(message = "questionName 不能为空", groups = {Insert.class, Update.class})
    @Size(max = 20, message = "questionName 不能超过20个字符", groups = {Insert.class, Update.class})
    @ApiModelProperty("问题名称") private String questionName;
    @NotNull(message = "userId 不能为空", groups = Insert.class)
    @ApiModelProperty("问题所属的用户Id") private int userId;
    @ApiModelProperty("执行状态, 0 表示是新建立的问题 1 表示导入了点数据 2 表示导入了车辆数据 3 表示正在计算距离 4表示距离计算完成")
    private int processState;
    @ApiModelProperty("随机算法执行状态 0 表示基本数据没有准备完成 7 表示正在进行随机算法" +
            "8 表示简单算法执行完成") private int simpleExecuted;
    @ApiModelProperty("遗传算法执行状态 0 表示基本数据没有准备完成 5 表示进行正在进行遗传算法" +
            "6 表示遗传算法执行完成") private int geneticExecuted;
    @ApiModelProperty("遗传优化算法执行状态 0 表示基本数据没有准备完成 5 表示进行正在进行遗传算法" +
            "6 表示遗传算法执行完成") private int newGeneticExecuted;
    @ApiModelProperty("无用") private int delFlag;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProcessState() {
        return processState;
    }

    public int getSimpleExecuted() {
        return simpleExecuted;
    }

    public void setSimpleExecuted(int simpleExecuted) {
        this.simpleExecuted = simpleExecuted;
    }

    public int getGeneticExecuted() {
        return geneticExecuted;
    }

    public void setGeneticExecuted(int geneticExecuted) {
        this.geneticExecuted = geneticExecuted;
    }

    public int getNewGeneticExecuted() {
        return newGeneticExecuted;
    }

    public void setNewGeneticExecuted(int newGeneticExecuted) {
        this.newGeneticExecuted = newGeneticExecuted;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    public void setProcessState(int processState) {
        this.processState = processState;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
