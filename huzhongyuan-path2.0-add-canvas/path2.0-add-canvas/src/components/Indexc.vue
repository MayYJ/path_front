<template>
  <div class="from">
    <el-table :data="tableData" style="width: 1000px;margin:0px auto;">
      <el-table-column label="项目ID" width="140">
        <template slot-scope="scope">
          <span>{{ scope.row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column label="项目名称" width="140">
        <template slot-scope="scope">
          <!-- <el-popover trigger="hover" placement="top">
            <p>姓名: {{ scope.row.name }}</p>
            <p>住址: {{ scope.row.address }}</p>
            <div slot="reference" class="name-wrapper">
              <el-tag size="medium">{{ scope.row.name }}</el-tag>
            </div>
          </el-popover>-->
          <span>{{ scope.row.questionName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="准备数据" width="140">
        <template slot-scope="scope">
          <span
            @click="prepareData(scope.$index, scope.row)"
            class="mouseIn"
          >{{ scope.row.prepareDataState}}</span>
        </template>
      </el-table-column>
      <el-table-column label="计算距离" width="140">
        <template slot-scope="scope">
          <span
            class="mouseIn"
            @click="workDistance(scope.$index, scope.row)"
          >{{ scope.row.checkDataState }}</span>
        </template>
      </el-table-column>
      <el-table-column label="执行算法" width="140">
        <template slot-scope="scope">
          <span
            class="mouseIn"
            @click="executionAlgorithm(scope.$index, scope.row)"
          >{{ scope.row.ExecutionAlgorithmState }}</span>
        </template>
      </el-table-column>
      <el-table-column label="查看结果" width="140">
        <template slot-scope="scope">
          <span class="mouseIn" @click="theResult(scope.$index, scope.row)">{{ scope.row.resultsState }}</span>
        </template>
      </el-table-column>





      <el-table-column label="操作">
        <template slot-scope="scope">
          <!-- <el-button size="mini" @click="handleEdit(scope.$index, scope.row)">编辑</el-button> -->
          <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      style="display: flex;justify-content: center;margin-top:40px;"
      background
      class="pagination"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page.sync="currentPage"
      :page-size="10"
      layout="prev, pager, next, jumper"
      :total="1000"
    ></el-pagination>





    <!-- 算法选择遮罩层 -->
    <el-dialog title="算法选择" :visible.sync="dialogVisible" width="350px" :before-close="handleClose">
      <el-select v-model="value" placeholder="请选择">
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
      <el-button type="primary" @click="implement">执行</el-button>
      <el-table :data="yizhixing" style="300px" v-if="yizhixing.length != 0">
        <el-table-column label="已执行完成" width="150">
          <template slot-scope="scope">
            <span class="mouseIn">{{ scope.row.value }}</span>
          </template>
        </el-table-column>
          <el-table-column  width="150">
          <template slot-scope="scope">
            <span class="mouseIn" style="display:none">{{ scope.row.key }}</span>
          </template>
          </el-table-column>
      </el-table>
      <el-table :data="zhengzaizhixing" style="300px" v-if="zhengzaizhixing.length != 0">
        <el-table-column label="正在执行" width="150">
          <template slot-scope="scope">
            <span class="mouseIn">{{ scope.row.value }}</span>
          </template>
        </el-table-column>
        <el-table-column  width="150">
          <template slot-scope="scope">
            <span class="mouseIn" style="display:none">{{ scope.row.key }}</span><span class="mouseIn" @click="stopcarry(scope.$index, scope.row)">停止</span>
          </template>
          </el-table-column>
      </el-table>
    </el-dialog>

      <!-- 查看结果遮罩层 -->
      <el-dialog
        title="算法详情"
        :visible.sync="dialogVisible1"
        width="350px"
        :before-close="handleClose">
      <el-table :data="yizhixing" style="250" v-if="yizhixing.length != 0">
        <el-table-column label="已执行完成" width="150">
          <template slot-scope="scope">
            <span class="mouseIn">{{ scope.row.value }}</span>
          </template>
        </el-table-column>
          <el-table-column  width="50">
          <template slot-scope="scope">
            <span class="mouseIn" style="display:none">{{ scope.row.key }}</span> <span class="mouseIn" @click="checkAlgorithm(scope.$index, scope.row)">查看</span>
          </template>
          </el-table-column>
      </el-table>
      <!-- <el-table :data="zhengzaizhixing" style="300px" v-if="zhengzaizhixing.length != 0">
        <el-table-column label="正在执行" width="150">
          <template slot-scope="scope">
            <span class="mouseIn">{{ scope.row.value }}</span>
          </template>
        </el-table-column>
        <el-table-column  width="150">
          <template slot-scope="scope">
            <span class="mouseIn" style="display:none">{{ scope.row.key }}</span><span class="mouseIn" @click="stopcarry(scope.$index, scope.row)">停止</span>
          </template>
          </el-table-column>
      </el-table> -->
      </el-dialog>

  </div>
</template>

<script>
export default {
  name: "Index",
  data() {
    return {
      currentPage: 1,
      tableData: [],
      list: [], //获取数据
      dialogVisible: false,
      options: [],
      valueId: "", //点击某个问题之后执行算法储存ID
      value: "",
      workIndex: '',//执行算法存储当前index
      yizhixing: [
      ],
      zhengzaizhixing: [
      ],
      //查看结果遮罩层
      dialogVisible1: false,
      resultId: '',  //点击查看结果之后储存Id
    };
  },
  methods: {
    // handleEdit (index, row) {
    //   console.log(index, row)
    // },
    /*
删除项目
*/
    handleDelete(index, row) {
      let that = this;
      console.log(index, row);
      console.log(that.tableData[index].id);
      this.$axios
        .delete(that.$url + "/question/removeQuestion", {
          params: {
            questionId: that.tableData[index].id
          }
        })
        .then(function(response) {
          console.log(11111111111111111);
          console.log(response);
          if (response.data.status == 0) {
            that.$notify({
              title: "警告",
              message: "项目*" + that.tableData[index].questionName + "*删除成功",
              type: "warning"
            });
            that.tableData.splice(index, 1);
          } else {
            that.$notify({
              title: "警告",
              message: response.data.msg ,
              type: "warning"
            });
          }

        })
        .catch(function(error) {
          console.log(error);
        });
    },
    /*
添加项目
*/
    addPrograme(value) {
      //console.log(value)
      this.loadAllData();
      // console.log(value);
    },
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`);
    },
    handleCurrentChange(val) {
      loadAllData(val);
    },
    /*
跳转准备数据界面
*/
    prepareData(index, row) {
      let that = this;
      this.$router.push({
        path: "/DataPerpare",
        query: { id: that.tableData[index].id }
      });
    },

    //数据处理
    dealData(value) {
      console.log(parseInt(value.processState));
      console.log(parseInt(value.geneticExecuted));
      console.log(
        parseInt(value.processState) == 4 && parseInt(value.geneticExecuted) < 5
      );
      if (parseInt(value.processState) == 0) {
        let a = {
          id: value.questionId,
          questionName: value.questionName,
          prepareDataState: "进行准备", //准备数据状态
          checkDataState: "", //查看数据状态
          ExecutionAlgorithmState: "", //执行算法状态
          resultsState: ""
        };
        return a;
      } else if (parseInt(value.processState) == 1) {
        let a = {
          id: value.questionId,
          questionName: value.questionName,
          prepareDataState: "正在准备", //准备数据状态
          checkDataState: "", //查看数据状态
          ExecutionAlgorithmState: "", //执行算法状态
          resultsState: ""
        };
        return a;
      } else if (parseInt(value.processState) == 2) {
        console.log('lalalalla');
        let a = {
          id: value.questionId,
          questionName: value.questionName,
          prepareDataState: "准备完成", //准备数据状态
          checkDataState: "开始", //查看数据状态
          ExecutionAlgorithmState: "", //执行算法状态
          resultsState: ""
        };
        return a;
      } else if (parseInt(value.processState) == 3) {
        let a = {
          id: value.questionId,
          questionName: value.questionName,
          prepareDataState: "准备完成", //准备数据状态
          checkDataState: "正在计算", //查看数据状态
          ExecutionAlgorithmState: "", //执行算法状态
          resultsState: ""
        };
        return a;
      } else if (
        parseInt(value.simpleExecuted) == 8 ||
        parseInt(value.geneticExecuted) == 6 ||
        parseInt(value.newGeneticExecuted) == 10
      ) {
        let a = {
          id: value.questionId,
          questionName: value.questionName,
          prepareDataState: "准备完成", //准备数据状态
          checkDataState: "计算完成", //查看数据状态
          ExecutionAlgorithmState: "选择算法", //执行算法状态
          resultsState: "查看结果"
        };
        return a;
      } else if (parseInt(value.processState) == 4) {
        console.log('sdafdsafdasfds')
        let a = {
          id: value.questionId,
          questionName: value.questionName,
          prepareDataState: "准备完成", //准备数据状态
          checkDataState: "计算完成", //查看数据状态
          ExecutionAlgorithmState: "选择算法", //执行算法状态
          resultsState: ""
        };
        return a;
      }
    },
    //开始计算距离
    workDistance(index, row) {
      this.workIndex = index;
      console.log(this.tableData[index]);
      console.log(this.list.length);
      console.log(index);
      let length = this.list.length - index - 1;
      console.log(length);
      if (parseInt(this.list[length].processState) == 2) {
        console.log(this.list[index]);
        this.$notify({
          title: "成功",
          message: "正在开始计算距离,请等待",
          type: "success"
        });
        let that = this;

        that.$axios
          .get(that.$url + "/distance/generateDistance", {
            params: {
              questionId: that.tableData[index].id
            }
          })
          .then(function(response) {
            console.log(response);
            if (response.data.status == 0) {
              // that.$notify({
              //   title: "成功",
              //   message: "计算距离完成",
              //   type: "success"
              // });
              // that.$router.go(0);
            }  else {
            that.$notify({
              title: "警告",
              message: response.data.msg ,
              type: "warning"
            });
          }
            //that.loadAllData(); //重新加载数据
          })
          .catch(function(error) {
            console.log(error);
          });
          setTimeout(() => {
            that.loadAllData(); //重新加载数据
          },3000)

      } else {
            that.$notify({
              title: "警告",
              message: '已经计算完成，请勿重复计算' ,
              type: "warning"
            });
      }
      
    },
    //已经执行的算法
    loaded() {
      let that = this;
      that.$axios
        .get(that.$url + "/getExecutedAlgorithm", {
          params: {
            questionId: that.valueId,
          }
        })
        .then(function(response) {
          console.log(response);
          if (response.data.status == 0) {
            that.yizhixing = [];
            for (let i in response.data.object) {
                let arr =  {
                value: response.data.object[i],
                key: i
              }
              that.yizhixing.unshift(arr);
            }
          } else {
            that.$notify({
              title: "警告",
              message: response.data.msg ,
              type: "warning"
            });
          }


        })
        .catch(function(error) {
          console.log(error);
        });
    },
    //正在执行的算法
    loading() {
      let that = this;
      that.$axios
        .get(that.$url + "/getExecutingAlgorithm", {
          params: {
            questionId: that.valueId,
          }
        })
        .then(function(response) {
          if (response.data.status == 0) {
          console.log(response);
          that.zhengzaizhixing = [];
          for (let i in response.data.object) {
              let arr =  {
              value: response.data.object[i],
              key: i
            }
            that.zhengzaizhixing.unshift(arr);
          }
          }  else {
            that.$notify({
              title: "警告",
              message: response.data.msg ,
              type: "warning"
            });
          }


        })
        .catch(function(error) {
          console.log(error);
        });
    },
    //加载算法
    executionAlgorithm(index, row) {
      let that = this;
      that.valueId = that.tableData[index].id;
      that.dialogVisible = true;
      //未执行的算法
      that.$axios
        .get(that.$url + "/getNotExecutingAlgorithm", {
          params: {
            questionId: that.tableData[index].id
          }
        })
        .then(function(response) {
          if (response.data.status == 0) {
            that.options = [];
            for (let i in response.data.object) {
              // console.log(i);
              // console.log(response.data.object[i]);
              // console.log(Object.keys(response.data.object[i]))
              let arr = {
                value: i,
                label: response.data.object[i]
              };
              that.options.unshift(arr);
            }
          } else {
            that.$notify({
              title: "警告",
              message: response.data.msg ,
              type: "warning"
            });
          }

        })
        .catch(function(error) {
          console.log(error);
        });

        //已经执行的算法
        that.loaded();

        //正在执行的算法
        that.loading();
    },


    //执行算法操作后刷新界面
    flashH() {
      let that = this;
      //未执行的算法
      that.$axios
        .get(that.$url + "/getNotExecutingAlgorithm", {
          params: {
            questionId: that.valueId
          }
        })
        .then(function(response) {
          if (response.data.status == 0) {
          that.options = [];
          for (let i in response.data.object) {
            // console.log(i);
            // console.log(response.data.object[i]);
            // console.log(Object.keys(response.data.object[i]))
            let arr = {
              value: i,
              label: response.data.object[i]
            };
            that.options.unshift(arr);
          }
          }   else {
            that.$notify({
              title: "警告",
              message: response.data.msg ,
              type: "warning"
            });
          }

        })
        .catch(function(error) {
          console.log(error);
        });

      //已经执行的算法
        that.loaded();

        //正在执行的算法
        that.loading();
    },
    //执行某个算法
    implement() {
      let that = this;
      console.log(that.valueId);
      that.$notify({
        title: "成功",
        message: "正在执行算法,请稍后",
        type: "success"
      });
      if (that.value != "") {
        that.$axios
          .get(that.$url + "/executeAlgorithm", {
            params: {
              questionId: that.valueId,
              key: that.value
            }
          })
          .then(function(response) {
            console.log(response);
            if (response.data.status == 0) {
              // that.$notify({
              //   title: "成功",
              //   message: "执行算法完成,请刷新界面",
              //   type: "success"
              // });
              // that.loadAllData(); //重新加载数据
              // console.log(that.workIndex);
              // that.flashH();  //操作后刷新数据
            } else {
            that.$notify({
              title: "警告",
              message: response.data.msg ,
              type: "warning"
            });
          }
          })
          .catch(function(error) {
            console.log(error);
          });
      }
      console.log(that.value);
    },
    //停止执行某个算法
    stopcarry(index,row) {
      console.log(index);
      let that = this;
      console.log(this.zhengzaizhixing[index].key);
              that.$axios
          .get(that.$url + "/stopAlgorithm", {
            params: {
              questionId: that.valueId,
              key: that.zhengzaizhixing[index].key
            }
          })
          .then(function(response) {
            console.log(response);
            if (response.data.status == 0) {
              // that.$notify({
              //   title: "成功",
              //   message: '停止'+ that.zhengzaizhixing[index].value +'算法成功',
              //   type: "success"
              // });
              // console.log(that.workIndex);
              // that.flashH();  //操作后刷新数据
            } else {
            that.$notify({
              title: "警告",
              message: response.data.msg ,
              type: "warning"
            });
          }
          })
          .catch(function(error) {
            console.log(error);
          });
    },
    //继续执行某个算法
    stillcarry(index,row) {
      // console.log(index);
      // let that = this;
      // console.log(this.zhengzaizhixing[index].key);
      //         that.$axios
      //     .get(that.$url + "/stopAlgorithm", {
      //       params: {
      //         questionId: that.valueId,
      //         key: that.zhengzaizhixing[index].key
      //       }
      //     })
      //     .then(function(response) {
      //       console.log(response);
      //       if (response.data.status == 0) {
      //         that.$notify({
      //           title: "成功",
      //           message: '继续执行'+ that.zhengzaizhixing[index].value +'算法成功',
      //           type: "success"
      //         });
      //         // that.$router.go(0);
      //       }
      //     })
      //     .catch(function(error) {
      //       console.log(error);
      //     });

    },
    handleClose(done) {
      this.$confirm("确认关闭？")
        .then(_ => {
          done();
        })
        .catch(_ => {});
    },

    //加载数据
    loadAllData(page) {
    let that = this;
    that.list = [];
    that.tableData = [];
    that.$axios
      .get(that.$url + "/question/getQuestions", {
        params: {
          userId: sessionStorage.getItem("userId"),
          currentPage: page,
          pageSize: 10
        }
      })
      .then(function(response) {
        if (response.data.status == 0) {
        console.log(response);
        let value = response.data.object;
        that.list = value;
        //console.log(that.list);
        for (let i in value) {
          //console.log(value[i]);
          that.tableData.unshift(that.dealData(value[i]));
        }
        //this.tableData.unshift(value)
        } else {
            that.$notify({
              title: "警告",
              message: response.data.msg ,
              type: "warning"
            });
          }

      })
      .catch(function(error) {
        console.log(error);
      });
    },

    //查看结果
    theResult (index, row) {
      console.log(index);
      let that = this;
      that.dialogVisible1 = true;
      console.log(that.dialogVisible1);
      that.valueId = that.tableData[index].id;
      that.flashH();
    },

    //查看某个算法的具体方案
    checkAlgorithm(index,row) {
      let that = this;
      console.log(index);
      console.log(this.yizhixing[index]);
      console.log(this.valueId);

      this.$router.push({
        path: "/Path",
        query: { 
          id: that.valueId,
          key: that.yizhixing[index].key
       }
      });
    }
  },
  mounted() {
    let that = this;
    that.loadAllData(1);
    console.log(sessionStorage.getItem('userId'))
  //测试接口
    // that.$axios
    //   .get(that.$url + "/finalSolution/getOneVersionFinalSolution", {
    //     params: {
    //       questionId : '47',
    //       key : '2',
    //     }
    //   })
    //   .then(function(response) {
    //     console.log(response);
    //   })
    //   .catch(function(error) {
    //     console.log(error);
    //   });

  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.from {
  margin: 20px 100px;
}
.pagination{
  margin-bottom: 100px;
}
.mouseIn {
  cursor: pointer;
}
</style>
