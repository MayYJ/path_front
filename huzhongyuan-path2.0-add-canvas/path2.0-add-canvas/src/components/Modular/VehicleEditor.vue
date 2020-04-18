<template>
  <div class="from">
    <div class="templateImportBox">
      <el-button type="primary" @click="centerDialogVisible = true">
        车辆导入
        <i class="el-icon-upload el-icon--right"></i>
      </el-button>
    </div>
    <div class="templateImportBox">
      <el-tag class="editor" @click="setCenter">手动增加车辆</el-tag>
    </div>
    <el-dialog :before-close="handleClose" title="车辆信息" style :visible.sync="dialogFormVisible">
      <el-form 
        :model="form"
        status-icon
        :rules="rules2"
        ref="ruleForm2"
        label-width="100px"
        class="demo-ruleForm"
      >
        <el-form-item label="车辆类型" :label-width="formLabelWidth" prop="type">
          <el-input v-model="form.type " autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="车辆容量" :label-width="formLabelWidth" prop="capacity">
          <el-input v-model="form.capacity " autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="车辆耗油量" :label-width="formLabelWidth" prop="oil">
          <el-input v-model="form.oil " autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="车辆价格" :label-width="formLabelWidth" prop="price">
          <el-input v-model="form.price " autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="车辆生产日期" :label-width="formLabelWidth" prop="capacity">
          <el-date-picker v-model="form.date" type="date" placeholder="选择日期"></el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="handsubmitTest('ruleForm2')">确 定</el-button>
      </div>
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
        :action="uploadV"
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

    <el-table :data="tableData" style="width:750px;margin:0px auto;">
      <el-table-column label="车辆ID" width="100">
        <template slot-scope="scope">
          <span>{{ scope.row.vehicleId }}</span>
        </template>
      </el-table-column>
      <el-table-column label="车辆类型" width="150">
        <template slot-scope="scope">
          <p>{{ scope.row.type }}</p>
        </template>
      </el-table-column>
      <el-table-column label="载货量" width="100">
        <template slot-scope="scope">
          <p>{{ scope.row.capacity }}</p>
        </template>
      </el-table-column>
      <el-table-column label="排量" width="100">
        <template slot-scope="scope">
          <p>{{ scope.row.oil }}</p>
        </template>
      </el-table-column>
      <el-table-column label="运费" width="100">
        <template slot-scope="scope">
          <p>{{ scope.row.price }}</p>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <!-- <el-button size="mini" @click="handleEdit(scope.$index, scope.row)">编辑</el-button> -->
          <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <footer-footer />
  </div>
</template>

<script>
import FooterFooter from "./Footer.vue";
export default {
  name: "VehicleList",
  data() {
      //车辆类型
    var testType= (rule, value, callback) => {
      if (!value) {
        return callback(new Error("车辆类型不能为空"));
      }
      setTimeout(() => {
            callback();
      }, 500);
    };
    //车辆容量
    var testCapacity = (rule, value, callback) => {
      if (!value) {
        return callback(new Error("车辆容量不能为空"));
      }
      setTimeout(() => {  
        let reg=/^[0-9]*$/;//汉字   
        // let reg=/^[0-9]*$/;  
        if (!reg.test(value)) {
          callback(new Error("请输入数字"));
        } else {
            callback();
        }
      }, 500);
    };
        //耗油量
    var testOil = (rule, value, callback) => {
      if (!value) {
        return callback(new Error("用户名不能为空"));
      }
      setTimeout(() => {  
        let reg=/^[0-9]*$/;//汉字   
        // let reg=/^[0-9]*$/;  
        if (!reg.test(value)) {
          callback(new Error("请输入数字"));
        } else {
            callback();
        }
      }, 500);
    };
        //车辆价格
    var testPrice = (rule, value, callback) => {
      if (!value) {
        return callback(new Error("用户名不能为空"));
      }
      setTimeout(() => {  
        let reg=/^[0-9]*$/;//汉字   
        // let reg=/^[0-9]*$/;  
        if (!reg.test(value)) {
          callback(new Error("请输入数字"));
        } else {
            callback();
        }
      }, 500);
    };
        //日期
    var testDate = (rule, value, callback) => {
      if (!value) {
        return callback(new Error("用户名不能为空"));
      }
      setTimeout(() => {  
            callback();
      }, 500);
    };

    return {
      tableData: [
        {
          id: 0,
          date: "2016-05-02",
          name: "拉拉",
          address: "上海市普陀区金沙江路 1518 弄"
        }
      ],
      centerDialogVisible: false,
      fileList: [],
      uploadV: "", //车辆excel上传地址
      dialogFormVisible: false,
      form: {
        type: "",
        capacity: "",
        oil: "",
        price: "",
        date: ""
      },
      rules2: {
        type: [{ validator: testType, trigger: "blur" }],
        capacity: [{ validator: testCapacity, trigger: "blur" }],
        oil: [{ validator: testOil, trigger: "blur" }],
        price: [{ validator: testPrice, trigger: "blur" }],
        date: [{ validator: testDate, trigger: "blur" }]
      },
      formLabelWidth: "120px"
    };
  },
  components: {
    FooterFooter,
  },
  methods: {
    handleEdit(index, row) {
      console.log(index, row);
    },
    handleDelete(index, row) {
      let that = this;
      console.log(index, row);
      //this.tableData.splice(index, 1);
      console.log(that.tableData[index]);
      this.$axios
        .delete(
          that.$url + "/vehicleSystem/user/vehicle",
          {
            params: {
              vehicleId: that.tableData[index].vehicleId,
              questionId: that.$route.query.id
            }
          },
          {
            headers: {
              "content-type": "application/json;charset=UTF-8"
            }
          }
        )
        .then(function(response) {
          console.log(response);
          if (response.data.status == 0) {
            that.tableData.splice(index, 1);
          }  else {
            that.$notify({
              title: "警告",
              message: response.data.msg,
              type: "warning"
            });
          }
          // that.$notify({
          //   title: "警告",
          //   message: "点" + that.tableData[index].nodeName + "删除成功",
          //   type: "warning"
          // });
          //
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    //文件上传函数
    submitUpload() {
      this.$refs.upload.submit();
    },
    handleChange(file, fileList) {
      let that = this;
      this.fileList = fileList.slice(-3);
      console.log(that.fileList);
      if (that.fileList[0].status == "success") {
        if (that.fileList[0].response.status == 0) {
          that.getAllV();
        }
      }
    },
    handleRemove(file, fileList) {
      console.log(file, fileList);
    },
    handlePreview(file) {
      console.log(file);
    },
    //获取车辆信息
    getAllV() {
      let that = this;
      that.$axios
        .get(that.$url + "/vehicleSystem/user/vehicle", {
          params: {
            questionId: this.$route.query.id
          }
        })
        .then(function(response) {
          if (response.data.status == 0) {
            console.log(response);
            that.tableData = response.data.object;
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
    //打开增加车辆
    setCenter() {
      this.dialogFormVisible = true;
    },
    //提交车辆
    //提交车辆验证
    handsubmit() {
      console.log(this.form);
      console.log(
        this.form.date.getFullYear() +
          "-" +
          (this.form.date.getMonth() < 10
            ? "0" + this.form.date.getMonth()
            : this.form.date.getMonth()) +
          "-" +
          (this.form.date.getDate() < 10
            ? "0" + this.form.date.getDate()
            : this.form.date.getDate())
      );
      let that = this;
      if (
        this.form.type != "" &&
        this.form.capacity != "" &&
        this.form.oil != "" &&
        this.form.price != "" &&
        this.form.date != ""
      ) {
        this.$axios
          .post(
            this.$url + "/vehicleSystem/user/vehicle",
            that.$qs.stringify({
              type: that.form.type,
              capacity: that.form.capacity,
              oil: that.form.oil,
              price: that.form.price,
              date:
                this.form.date.getFullYear() +
                "-" +
                (this.form.date.getMonth() + 1 < 10
                  ? "0" + (this.form.date.getMonth() + 1)
                  : this.form.date.getMonth() + 1) +
                "-" +
                (this.form.date.getDate() < 10
                  ? "0" + this.form.date.getDate()
                  : this.form.date.getDate()),
              ownerId: parseInt(sessionStorage.getItem("userId")),
              questionId: that.$route.query.id
            })
          )
          .then(res => {
            if (res.data.status == 0) {
              this.$notify({
                title: "成功",
                message: "车辆添加成功",
                type: "success"
              });
              this.dialogFormVisible = false;
              that.form = {
                type: "",
                capacity: "",
                oil: "",
                price: "",
                date: ""
              };
            console.log(res);
            that.getAllV();
            }  else {
            that.$notify({
              title: "警告",
              message: response.data.msg,
              type: "warning"
            });
          }

          });
      }
    },
    //提交车辆验证
    handsubmitTest(formName) {
      let that = this;
      this.$refs[formName].validate(valid => {
        if (valid) {
          //alert("submit!");
          that.handsubmit()
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    handleClose(done) {
      this.$confirm("确认关闭？")
        .then(_ => {
          done();
        })
        .catch(_ => {});
    }
  },
  mounted() {
    console.log(this.$route.query.id);
    let that = this;
    that.uploadV =
      this.$url + "/vehicleSystem/excelVehicleInfo/" + this.$route.query.id;
    that.getAllV();
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
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
  margin-top: 20px;
}
.editor {
  cursor: pointer;
}
.from {
  margin-top: 30px;
  margin-bottom: 100px;
}
</style>
