<template>
  <div class="from">
    <div class="templateImportBox">
      <el-button type="primary" @click="centerDialogVisible = true">
        模板导入
        <i class="el-icon-upload el-icon--right"></i>
      </el-button>
    </div>
    <!-- 中心点 -->
    <div class="templateImportBox">
      <el-tag class="editor" @click="setCenter">设置中心点</el-tag>
    </div>
    <el-dialog
      title="设置中心点服务点"
      :visible.sync="dialogVisible"
      width="600px"
      :before-close="handleClose"
    >
      <el-transfer
        filterable
        :filter-method="filterMethod"
        filter-placeholder="请输入地点名"
        v-model="rightValue"
        :data="data2"
        :titles="['服务点', '中心点']"
        @change="centerChange"
      ></el-transfer>
      <span slot="footer" class="dialog-footer">
        <!-- <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="dialogVisible = false">确 定</el-button>-->
      </span>
    </el-dialog>

    <el-dialog
      title="提示"
      :visible.sync="centerDialogVisible"
      width="30%"
      center
      style="font-family:PingFang SC"
    >
      <el-upload
        class="upload-demo"
        ref="upload"
        :action="excelUp"
        :on-preview="handlePreview"
        :on-remove="handleRemove"
        :file-list="fileList"
        :auto-upload="false"
        :on-change="handleChange"
      >
        <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
        <el-button
          style="margin-left: 10px;"
          size="small"
          type="success"
          @click="submitUpload"
        >上传到服务器</el-button>
        <div slot="tip" class="el-upload__tip">上传文件</div>
      </el-upload>
      <span slot="footer" class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="centerDialogVisible = false">确 定</el-button>
      </span>
    </el-dialog>

    <el-table :data="tableData" ref="multipleTable" style="cursor: pointer;width: 780px;margin:0px auto;" >
      <el-table-column label="地址名称" width="140">
        <template slot-scope="scope">
          <span @click="toSingePoint(scope.row)">{{ scope.row.nodeName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="地址描述" width="200">
        <template slot-scope="scope">
          <!-- <el-popover trigger="hover" placement="top">
            <p>姓名: {{ scope.row.name }}</p>
            <p>住址: {{ scope.row.address }}</p>
            <div slot="reference" class="name-wrapper">
              <el-tag size="medium">{{ scope.row.name }}</el-tag>
            </div>
          </el-popover>-->
          <span @click="toSingePoint(scope.row)">{{ scope.row.nodeAddress }}</span>
        </template>
      </el-table-column>
      <el-table-column label="经度" width="100">
        <template slot-scope="scope">
          <span @click="toSingePoint(scope.row)">{{ scope.row.lng }}</span>
        </template>
      </el-table-column>
      <el-table-column label="纬度" width="100">
        <template slot-scope="scope">
          <span @click="toSingePoint(scope.row)">{{ scope.row.lat }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column type="selection" width="30"></el-table-column> -->
      <el-table-column label="是否为中心点" width="120px">
        <template slot-scope="scope">
          <span @click="toSingePoint(scope.row)">{{ isCenter(scope.row.isCenter) }}</span>
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
      class="pagination"
      background
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page.sync="currentPage"
      :page-size="100"
      layout="prev, pager, next, jumper"
      :total="1000"
    ></el-pagination>
    <footer-footer />
  </div>
</template>

<script>
import FooterFooter from "./Footer.vue";
export default {
  name: "Index",
  data() {
    return {
      currentPage: 1,
      excelUp: "", //excel导点
      centerDialogVisible: false,
      dialogVisible: false, //中心点
      data2: [],
      rightValue: [],
      filterMethod(query, item) {
        return item.pinyin.indexOf(query) > -1;
      },
      centerPoint: [],
      serverPoint: [],
      checked: false,
      multipleSelection: [],
      questionId: "",
      tableData: [],
      fileList: [
        // {
        //   name: "food.jpeg",
        //   url:
        //     "https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100"
        // },
        // {
        //   name: "food2.jpeg",
        //   url:
        //     "https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100"
        // }
      ]
    };
  },
  components: {
    FooterFooter,
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
      console.log(this.tableData[index]);
      this.$axios
        .delete(that.$url + "/node/deleteNode", {
          params: {
            questionId: that.questionId,
            nodeId: that.tableData[index].nodeId
          }
        })
        .then(function(response) {
          console.log(response);
          if (response.data.status == 0) {
          that.$notify({
            title: "成功",
            message: "点" + that.tableData[index].nodeName + "删除成功",
            type: "success"
          });
          that.tableData.splice(index, 1);
          } else {
            that.$notify({
            title: "警告",
            message: response.data.msg,
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
    // addPrograme(value) {
    //   console.log(value);
    //   this.tableData.unshift(value);
    // },
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`);
    },
    handleCurrentChange(val) {
      console.log(`当前页: ${val}`);
    },
    /*
跳转准备数据界面
*/
    prepareData(index, row) {
      console.log(index, row);
      this.$router.push({ path: "/DataPerpare", query: { id: "1" } });
    },
    //文件上传函数
    submitUpload() {
      console.log();
      this.$refs.upload.submit();
    },
    handleChange(file, fileList) {
      let that = this;
      this.fileList = fileList.slice(-3);
      console.log(that.fileList);
      if (that.fileList[0].status == "success") {
        if (that.fileList[0].response.status == 0) {
          that.getAllpoinit();
        }
      }
    },
    handleRemove(file, fileList) {
      console.log(file, fileList);
    },
    handlePreview(file) {
      console.log(file);
    },

    //获取改问题下面所有点信息
    getAllpoinit() {
      let that = this;
      this.$axios
        .get(this.$url + "/node/getQuestionNodes", {
          params: {
            questionId: that.questionId
          }
        })
        .then(res => {
          if (res.data.status == 0) {
          //显示为服务点
          for (let i in that.tableData) {
            if (that.tableData[i].isCenter == 0) {
            } else {
              that.rightValue.push(parseInt(i));
            }
          }
          console.log("server" + that.serverPoint);
          console.log("center" + that.centerPoint);
          } else {
            that.$notify({
              title: "警告",
              message: response.data.msg,
              type: "warning"
            });
          }
          console.log(res);
          that.tableData = res.data.object;


        })
        .then(data => {});
    },
    //是否为中心点
    isCenter(value) {
      return value == 0 ? "否" : "是";
    },
    //中心点遮罩层
    handleClose(done) {
      this.$confirm("确认关闭？")
        .then(_ => {
          done();
        })
        .catch(_ => {});
    },
    generateData2(arr) {
      console.log(arr);
      let data = [];
      //let cities = ["上海", "北京", "广州", "深圳", "南京", "西安", "成都"];
      let cities = [];
      for (let i in arr) {
        cities.push(arr[i].nodeName);
      }
      let pinyin = cities;
      cities.forEach((city, index) => {
        data.push({
          label: city,
          key: index,
          pinyin: pinyin[index]
        });
      });
      this.data2 = data;
    },
    setCenter() {
      this.getAllpoinit();
      this.dialogVisible = true;
      this.generateData2(this.tableData);
    },
    centerChange(value, direction, movedKeys) {
      console.log(value, direction, movedKeys);
      if (direction == "right") {
        console.log("设置中心点");
        console.log(movedKeys);
        this.changeCenter(movedKeys);
      } else {
        console.log("设置服务点");
        this.changeServer(movedKeys);
      }
    },
    updateNode(res,isCenter) {
      console.log(res);
      let that = this;
      this.$axios
        .patch(
          this.$url + "/node/updateNode",
             that.$qs.stringify({
              nodeId: res.nodeId,
              questionId: that.questionId,
              nodeName: res.nodeName,
              nodeAddress: res.nodeAddress,
              lat: res.lat,
              lng: res.lng,
              isCenter: isCenter
          }),
        )
        .then(res1 => {
          console.log(111111111);
          console.log(res1);
          if (res1.data.status == 0) {
            console.log(11111111);
            that.$notify({
            title: '成功',
            message: '点'+ res.nodeName + '设置成功',
            type: 'success'
        });
                  that.getAllpoinit();
          }  else {
            that.$notify({
              title: "警告",
              message: response.data.msg,
              type: "warning"
            });
          }

        })
          .catch(function (error) {
            this.$notify.error({
          title: '错误',
          message: '请刷新界面重新设置'
        });
  })
    },
    //变为中心点
    changeCenter(movedKeys) {

      let that = this;
      for (let i in movedKeys) {
        console.log(movedKeys[i]);
        that.updateNode(that.tableData[movedKeys[i]],'1');
      }
    },
    //变为服务点
    changeServer(movedKeys) {
      let that = this;
      for (let i in movedKeys) {
        console.log(movedKeys[i]);
        that.updateNode(that.tableData[movedKeys[i]],'0');
      }
    },
    //查看单一点
    toSingePoint (row) {
      this.$router.push({
        path: "/SinglePoint",
        query: { 
          lat: row.lat,
          lng: row.lng,
          nodeAddress: row.nodeAddress
        }
      });
    }
  },
  beforeCreate() {

  },
  mounted() {
    let that = this;
    that.excelUp = that.$url + "/node/excelNodeInfo/" + this.$route.query.id;
    console.log(that.excelUp);
    that.questionId = this.$route.query.id;
    that.getAllpoinit();
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.from {
  margin: 0px 100px;
}
.mouseIn {
  cursor: pointer;
}
.templateImportBox {
  /* position: absolute;
  left: 50px;
  width: 250px;
  height: 200px;
  top: 100px;
  margin: 0px auto;
  display: flex;
  justify-content: flex-start;
  z-index: 2; */
  width: 800px;
  margin: 0px auto;
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}
.editor {
  cursor: pointer;
}
.pagination {
  margin-top: 30px;
  margin-bottom: 100px;
}
</style>
