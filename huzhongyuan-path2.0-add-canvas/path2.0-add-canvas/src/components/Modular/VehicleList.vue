<template>
  <div class="from">
    <div class="templateImportBox">
      <el-button type="primary" @click="centerDialogVisible = true">
        模板导入
        <i class="el-icon-upload el-icon--right"></i>
      </el-button>
    </div>
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
  </div>
</template>

<script>
export default {
  name: "VehicleList",
  data() {
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
      uploadV: "" //车辆excel上传地址
    };
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
        .delete(that.$url + "/vehicleSystem/user/vehicle", {params: [that.tableData[index]]},{
                headers: {
                'content-type': 'application/json;charset=UTF-8'
                }}
        
        )
        .then(function(response) {
          console.log(response);
          // that.$notify({
          //   title: "警告",
          //   message: "点" + that.tableData[index].nodeName + "删除成功",
          //   type: "warning"
          // });
          // that.tableData.splice(index, 1);
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    //文件上传函数
    submitUpload() {
      this.$refs.upload.submit();
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
          console.log(response)
          that.tableData = response.data.object;
        })
        .catch(function(error) {
          console.log(error);
        });
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
</style>
