<template>
  <!-- <div>我是path界面</div> -->
  <div class="mapBox">
    <header-header />
    <div id="allmap" ref="allmap" class="mapmap"></div>
    <!-- 鼠标滑倒右侧显示箭头 -->
    <!-- <div class="add" @mouseenter="enter()" @mouseleave="leave()" :style="add">
      <div class="show" :style="mchange">
        <i class="el-icon-plus"></i>
      </div>
    </div>-->
    <!-- 导航步骤 -->
    <!-- <div class="step" :style="navImport">
      <div class="search">
        <span class="searchTip tip">创建问题</span>
      </div>
      <div class="questionbox">
       <el-input v-model="input" placeholder="请输入问题名称" style="width:70%;" ></el-input>
       <el-button type="primary" @click="createQuestion" style="margin-left:10px;">确定</el-button>
    </div>-->
    <!-- 中心点导入 -->
    <!-- <div class="centerInput">
        <span class="centerTips">中心点导入</span>
        <el-upload
          class="upload-demo"
          :action="doUpload1"
          :on-change="handleChange1"
          :file-list="fileList1"
          :limit="1"
          :disabled="disabled"
          >
          <el-button size="small" type="primary" style="margin-top:10px;" :disabled="disabled">点击上传</el-button>
          <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
        </el-upload>
    </div>-->
    <!-- 服务点导入 -->
    <!-- <div class="centerInput">
        <span class="centerTips">服务点导入</span>
        <el-upload
          class="upload-demo"
          :action="doUpload2"
          :on-change="handleChange2"
          :file-list="fileList2"
          :limit="1"
          :disabled="disabled"
          >
          <el-button size="small" type="primary" style="margin-top:10px;" :disabled="disabled">点击上传</el-button>
          <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
        </el-upload>
    </div>-->
    <!-- 车辆信息导入 -->
    <!-- <div class="centerInput">
        <span class="centerTips">车辆信息导入</span>
        <el-upload
          class="upload-demo"
          :action="doUpload3"
          :on-change="handleChange3"
          :file-list="fileList3"
          :disabled="disabled"
          >
          <el-button size="small" type="primary" style="margin-top:10px;" :disabled="disabled">点击上传</el-button>
          <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
        </el-upload>
      </div> 

      <div class="workout">
            <el-button type="primary" size="small" v-on:click = "workOut"  :disabled="disabled">开始计算</el-button>
        </div>   
    </div>-->
    <!-- 路线展示 -->
    <div class="step" :style="navrun">
      <!-- <i class="el-icon-back" @click="to_Import"></i> -->
      <div class="mtitle">方案展示</div>
      <el-table :data="tableData2" style="width: 100%" :row-class-name="tableRowClassName">
        <el-table-column label width="30"></el-table-column>
        <el-table-column label="方案Id" width="80">
          <template slot-scope="scope">
            <span>{{ scope.row.finalSolutionId }}</span>
          </template>
        </el-table-column>
        <el-table-column label="方案距离" width="140">
          <template slot-scope="scope">
            <span>{{ scope.row.totalDis }}米</span>
          </template>
        </el-table-column>
        <el-table-column label="地址">
          <template slot-scope="scope">
            <span style="display:none">{{ scope.row.key }}</span>
            <span class="mouseIn" @click="getDetail(scope.$index, scope.row)">查看</span>
          </template>
        </el-table-column>
      </el-table>
      <!-- <div class="mshow" v-for="(item,key,index) in finalSolution" v-bind:key="item.id">
        <div class="mtips" @click="Solution(index)">{{item.name}} <span>总距离: {{item.totalDis}}</span> </div> 
      </div>-->
    </div>
  </div>
</template>
<script>
import Lushu from '../lib/lushu.js'
import HeaderHeader from "./Modular/Header.vue";
export default {
  data() {
    return {
      //data
      dialogVisible: false,
      // mchange: {
      //   marginRight: "0px"
      // },
      // add: {
      //   display: "none"
      // },
      //questionId: 35,
      // fileList1: [],
      // fileList2: [],
      // fileList3: [],
      // input: "",
      // items: [
      //   {
      //     title: "路线最短",
      //     content: "xxxxxxxx"
      //   }
      // ],
      // uploadBotton: "false", //上传按钮状态
      // object: "",
      // doUpload1:  this.$url + "/node/excelNodeInfo/", //中心点url
      // doUpload2:  this.$url + "/node/excelNodeInfo/", //服务点url
      // doUpload3:   this.$url + "/vehicleSystem/excelVehicleInfo/", //车辆url
      questionId: "", //问题ID
      disabled: true, //禁止上传'
      loaded: ["false", "false", "false"], //查看上传状态
      finalSolution: [], //得到一个版本的解决方案
      navImport: "display:block",
      navrun: "display:block",
      tableData2: [
        {
          date: "2016-05-02",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1518 弄"
        },
        {
          date: "2016-05-04",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1518 弄"
        },
        {
          date: "2016-05-01",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1518 弄"
        },
        {
          date: "2016-05-03",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1518 弄"
        }
      ]
    };
  },
  components: {
    //组件
    HeaderHeader,
  },
  methods: {
    //表格列表效果
    tableRowClassName({ row, rowIndex }) {
      if (rowIndex === 1) {
        return "warning-row";
      } else if (rowIndex === 3) {
        return "success-row";
      }
      return "";
    },
    fristMap() {
      let map = new BMap.Map(this.$refs.allmap, { enableMapClick: false }); // 创建Map实例
      map.centerAndZoom(new BMap.Point(116.404, 39.915), 18); // 初始化地图,设置中心点坐标和地图级别
      map.addControl(
        new BMap.MapTypeControl({
          //添加地图类型控件
          mapTypes: [BMAP_NORMAL_MAP, BMAP_HYBRID_MAP]
        })
      );
      map.enableScrollWheelZoom(true); //开启鼠标滚轮缩放
      function showInfo(e) {
        console.log(e);
      }
      map.addEventListener("click", showInfo); //点击事件
    },

    //鼠标进入事件
    enter(index) {
      console.log(1);
      let indexnumber = 0;
      let t = setInterval(() => {
        this.mchange = "marginRight:" + indexnumber++ + "px";
        console.log(indexnumber);
        if (indexnumber > 50) {
          clearTimeout(t);
        }
      }, 1);
    },
    //鼠标移出事件
    leave(index) {
      let indexnumber = 50;
      let t = setInterval(() => {
        this.mchange = "marginRight:" + indexnumber-- + "px";
        console.log(indexnumber);
        if (indexnumber < 0) {
          clearTimeout(t);
        }
      }, 1);
    },
    //创建问题
    createQuestion(e) {
      let that = this;
      console.log(e);
      //创建问题
      this.$axios
        .post(
          this.$url + "/question/insertQuestion",
          this.$qs.stringify({
            questionName: this.input,
            userId: sessionStorage.getItem("userId")
          })
        )
        .then(function(response) {
          //存入问题ID
          that.questionId = response.data.object;
          if (response.data.status != 200) {
            that.uploadBotton = "false";
          } else {
            that.uploadBotton = "true";
            that.disabled = false;
            that.doUpload1 = that.doUpload1 + that.questionId;
            that.doUpload2 = that.doUpload2 + that.questionId;
            that.doUpload3 = that.doUpload3 + that.questionId;
          }
        })
        .catch(function(response) {
          console.log(response);
          console.log(22);
        });
    },
    //中心点
    handleChange1(file, fileList) {
      let that = this;
      this.fileList1 = fileList.slice(-3);
      console.log(that.fileList1);
    },

    //服务点导入
    handleChange2(file, fileList) {
      this.fileList2 = fileList.slice(-3);
      console.log(fileList);
    },
    //车辆信息导入
    handleChange3(file, fileList) {
      let that = this;
      this.fileList3 = fileList.slice(-3);
      console.log(fileList);
    },
    //开始计算
    workOut(e) {
      let that = this;
      console.log(e);
      this.$axios
        .get(this.$url + "/question/executeAlgorithm", {
          params: { questionId: that.questionId } //that.questionId
        })
        .then(function(response) {
          console.log(response);
          console.log(1);
          (that.navImport = "display:none"), (that.navrun = "display:block");
          that.uploadSolution();
        })
        .catch(function(response) {
          console.log(response);
          console.log(22);
        });
    },

    //方案展示
    uploadSolution() {
      let that = this;
      this.$axios
        .get(this.$url + "/finalSolution/getAllFinalSolution", {
          params: { questionId: that.questionId } //that.questionId
        })
        .then(function(response) {
          console.log(response);
          for (let i in response.data.object) {
            response.data.object[i].name = "方案" + i;
          }
          that.finalSolution = response.data.object;
          console.log(that.finalSolution);
          console.log(1);
        })
        .catch(function(response) {
          console.log(response);
          console.log(22);
        });
    },
    //路径绘制
    lushu(ll, data, color) {
      console.log(data);
      let map = new BMap.Map(this.$refs.allmap, { enableMapClick: false }); // 创建Map实例
      map.centerAndZoom(new BMap.Point(data[0][0].lng, data[0][0].lat), 10); // 初始化地图,设置中心点坐标和地图级别
      map.addControl(
        new BMap.MapTypeControl({
          //添加地图类型控件
          mapTypes: [BMAP_NORMAL_MAP, BMAP_HYBRID_MAP]
        })
      );
      map.enableScrollWheelZoom(true); //开启鼠标滚轮缩放
      function showInfo(e) {
        console.log(e);
      }

      map.addEventListener("click", showInfo); //点击事件

      var p = new Promise(function(resolve, reject) {
        let lushu = [];
        console.log(data);
        //实例化一个驾车导航用来生成路线
        var drv = new BMap.DrivingRoute(map, {
          onSearchComplete: function(res) {
            if (drv.getStatus() == BMAP_STATUS_SUCCESS) {
              console.log(res);
              var plan = res.getPlan(0);
              var arrPois = [];
              for (var j = 0; j < plan.getNumRoutes(); j++) {
                var route = plan.getRoute(j);
                arrPois = arrPois.concat(route.getPath());
              }
      
              map.addOverlay(
                new BMap.Polyline(arrPois, { strokeColor: color[ll] })
              );
              ll = ll + 1;
              map.setViewport(arrPois);
              console.log(arrPois);
                new BMapLib.LuShu(map, arrPois, {
                  defaultContent: "", //"从天安门到百度大厦"
                  autoView: false, //是否开启自动视野调整，如果开启那么路书在运动过程中会根据视野自动调整
                  icon: new BMap.Icon(
                    "http://lbsyun.baidu.com/jsdemo/img/car.png",
                    new BMap.Size(52, 26),
                    { anchor: new BMap.Size(27, 13) }
                  ),
                  speed: 12000,
                  enableRotation: true, //是否设置marker随着道路的走向进行旋转,
                  length:1
                }).start();
            }
          }
        });
        for (let k in data) {
          let p1 = new BMap.Point(data[k][0].lng, data[k][0].lat);
          
          let p2 = p1;
          let arr_map = [];

          
          for (let i in data[k]) {
            console.log(data[k].length)
            //在地图上设置标签
            if (data[k][0].nodeAddr  == data[k][data[k].length-1].nodeAddr){
                var m1 = new BMap.Marker(new BMap.Point(data[k][i].lng, data[k][i].lat));         //创建3个marker
                map.addOverlay(m1);
                var lab1 = new BMap.Label('起始/终点:' + data[k][0].nodeAddr,{position:new BMap.Point(data[k][0].lng, data[k][0].lat)});        //创建3个label
                map.addOverlay(lab1);
            }

            if (i != 0 && (i != data[k].length - 1))
            {

                var m1 = new BMap.Marker(new BMap.Point(data[k][i].lng, data[k][i].lat));         //创建marker
                map.addOverlay(m1);
                var lab1 = new BMap.Label('途径点:' + data[k][i].nodeAddr,{position:new BMap.Point(data[k][i].lng, data[k][i].lat)});        //创建3个label
                map.addOverlay(lab1);
            }
            if (i == 0 && (data[k][0].nodeAddr != data[k][data[k].length-1].nodeAddr)) {
                var m1 = new BMap.Marker(new BMap.Point(data[k][i].lng, data[k][i].lat));         //创建marker
                map.addOverlay(m1);
                var lab1 = new BMap.Label('起点:' + data[k][i].nodeAddr,{position:new BMap.Point(data[k][i].lng, data[k][i].lat)});        //创建3个label
                map.addOverlay(lab1);
            }
            if (i == data[k].length - 1 && (data[k][0].nodeAddr != data[k][data[k].length-1].nodeAddr)) {
                var m1 = new BMap.Marker(new BMap.Point(data[k][i].lng, data[k][i].lat));         //创建marker
                map.addOverlay(m1);
                var lab1 = new BMap.Label('终点:' + data[k][i].nodeAddr,{position:new BMap.Point(data[k][i].lng, data[k][i].lat)});        //创建3个label
                map.addOverlay(lab1);
            }
            arr_map.push(new BMap.Point(data[k][i].lng, data[k][i].lat));
          }
            console.log(arr_map)
            map.setViewport(arr_map); //调整到最佳视野
            drv.search(p1, p2, { waypoints: arr_map });
        }





      });
      return p;
    },

    //得到某个算法的总方案
    getOneVersionFinalSolution() {
      console.log(this.$route.query.id);
      console.log(this.$route.query.key);
      this.tableData2 = [];
      let that = this;
      that.$axios
        .get(that.$url + "/finalSolution/getOneVersionFinalSolution", {
          params: {
            questionId: parseInt(that.$route.query.id),
            key: parseInt(that.$route.query.key)
          }
        })
        .then(function(response) {
          console.log(response);
          if (response.data.status == 0) {
            let arr = {};
            for (let i in response.data.object) {
              arr = response.data.object[i];
              that.tableData2.push(arr);
            }
          }
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    //得到某个方案的详细路径
    getDetail(index, row) {
      let that = this;
      console.log(that.tableData2[index].finalSolutionId);
      that.$axios
        .get(that.$url + "/finalSolution/getFinalSolution", {
          params: {
            finalSolutionId: that.tableData2[index].finalSolutionId
          }
        })
        .then(function(response) {
          console.log(response);
          if (response.data.status == 0) {
            //路径绘制
            var color = ["#111", "red", "green", "blue", "purple", "#EE7600"];
            var ll = 0;
            let data = response.data.object.routes;
            that.lushu(ll, data, color);
            console.log(data);
            console.log(11);
          }
        })
        .catch(function(error) {
          console.log(error);
        });
    }
  },
  mounted() {
    let that = this;
    console.log("mounted");
    //加载完成触发已加载事件
    this.$emit("loaded");
    this.fristMap(); //调用地图
    this.getOneVersionFinalSolution();
  }
};
</script>
<style scoped>
.mapmap {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: red;
  z-index: 0;
}

/* 导航步骤 */
.step {
  position: absolute;
  height: 100%;
  width: 350px;
  background-color: white;
  z-index: 1001;
  right: 0;
  top: 0;
}
.search {
  display: flex;
  margin-left: 20px;
  align-items: center;
  margin-top: 20px;
}

.searchTip {
  margin-right: 20px;
}
.centerInput {
  margin-top: 20px;
  margin-left: 20px;
}
.workout {
  margin-top: 50px;
  display: flex;
  justify-content: center;
}
.mshow {
  padding-left: 20px;
  height: 40px;
  display: flex;
  cursor: pointer;
  align-items: center;
}
.mshow:hover {
  background-color: #f0f9eb;
}
.mtitle {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  font-size: 20px;
  margin-bottom: 20px;
  font-family: "Microsoft YaHei";
  margin-top: 100px;
}
.questionbox {
  display: flex;
  margin-left: 20px;
  margin-top: 20px;
  align-items: center;
}
.questionbox i {
  margin-left: 20px;
}
.routes {
  padding: 5px 0px 5px 20px;
  cursor: pointer;
}
.el-icon-back {
  cursor: pointer;
  margin: 10px 0 0 10px;
}
.el-table .warning-row {
  background: oldlace;
}

.el-table .success-row {
  background: #f0f9eb;
}

.mouseIn {
  cursor: pointer;
}
</style>

