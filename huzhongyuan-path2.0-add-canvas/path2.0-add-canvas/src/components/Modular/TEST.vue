<template>
  <!-- <div>我是path界面</div> -->

  <div class="mapBox">
    <div id="allmap" ref="allmap" class="mapmap"></div>
    <!-- 鼠标滑倒右侧显示箭头 -->
    <div class="add" @mouseenter="enter()" @mouseleave="leave()" :style="add">
      <div class="show" :style="mchange">
        <i class="el-icon-plus"></i>
      </div>
    </div>

    <!-- 导航步骤 -->
    <div class="step" :style="navImport">
      <div class="search">
        <span class="searchTip tip">创建问题</span>
      </div>
      <div class="questionbox">
       <el-input v-model="input" placeholder="请输入问题名称" style="width:70%;" ></el-input>
       <el-button type="primary" @click="createQuestion" style="margin-left:10px;">确定</el-button>
      </div>

      <!-- 中心点导入 -->
      <div class="centerInput">
        <span class="centerTips">中心点导入</span>
        <el-upload
          class="upload-demo"
          :action="doUpload1"
          :on-change="handleChange1"
          :file-list="fileList1"
          :limit="1"
          >
          <el-button size="small" type="primary" style="margin-top:10px;" :disabled="disabled">点击上传</el-button>
          <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
        </el-upload>
      </div>

      <!-- 服务点导入 -->
      <div class="centerInput">
        <span class="centerTips">服务点导入</span>
        <el-upload
          class="upload-demo"
          :action="doUpload2"
          :on-change="handleChange2"
          :file-list="fileList2"
          :limit="1"
          >
          <el-button size="small" type="primary" style="margin-top:10px;" :disabled="disabled">点击上传</el-button>
          <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
        </el-upload>
      </div>  

      <!-- 车辆信息导入 -->
      <div class="centerInput">
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
    </div>

    <!-- 路线展示 -->
    <div class="step" :style="navrun">
      <i class="el-icon-back" @click="to_Import"></i>
      <div class="mtitle">方案展示</div>
      <div class="mshow" v-for="(item,index) in finalSolution">
        <div class="mtips" @click="Solution(index)">{{item.name}} <span>总距离: {{item.totalDis}}</span> </div> 
        <!-- <div class="routes"  v-for="items in item.routes">{{ items.name }}</div> -->
        <!-- <div class="mcontent">{{item.content}}</div> -->
      </div>
    </div>
  </div>


</template>
<script>
export default {
  data() {
    return {
      //data
      dialogVisible: false,
      mchange: {
        marginRight: "0px"
      },
      add: {
        display: "none"
      },
      //questionId: 35,
      fileList1: [],
      fileList2: [],
      fileList3: [],
      input: "",
      items: [
        {
          title: "路线最短",
          content: "xxxxxxxx"
        }
      ],
      uploadBotton: "false", //上传按钮状态
      object: "",
      doUpload1:  this.$url + "/node/excelNodeInfo/", //中心点url
      doUpload2:  this.$url + "/node/excelNodeInfo/", //服务点url
      doUpload3:
         this.$url + "/vehicleSystem/excelVehicleInfo/", //车辆url
      questionId: "", //问题ID
      disabled: true, //禁止上传'
      loaded: ["false", "false", "false"], //查看上传状态
      finalSolution: [], //得到一个版本的解决方案
      navImport: 'display:block',
      navrun: 'display:none'
    };
  },
  components: {
    //组件
  },
  methods: {
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

    // mapNew(centerPoint,serverPoint,waypoints) {
    //   let map = new BMap.Map(this.$refs.allmap, { enableMapClick: false }); // 创建Map实例
    //   map.centerAndZoom(new BMap.Point(116.404, 39.915), 18); // 初始化地图,设置中心点坐标和地图级别
    //   map.addControl(
    //     new BMap.MapTypeControl({
    //       //添加地图类型控件
    //       mapTypes: [BMAP_NORMAL_MAP, BMAP_HYBRID_MAP]
    //     })
    //   );
    //   var p1 = new BMap.Point(116.301934,39.977552);
    //   var p2 = new BMap.Point(116.508328,39.919141);
    //   var driving = new BMap.DrivingRoute(map, {renderOptions:{map: map, autoViewport: true}});
    //   driving.search(p1, p2,{waypoints:["北京科技大学","北京国际会议中心"]});//waypoints表示途经点
    //   map.setCurrentCity("重庆邮电大学"); // 设置地图显示的城市 此项是必须设置的
    //   map.enableScrollWheelZoom(true); //开启鼠标滚轮缩放
    //   function showInfo(e) {
    //     console.log(e);
    //   }
    //   map.addEventListener("click", showInfo); //点击事件
    // },

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
      // if (that.fileList1.response.status == '导入成功') {
      //   that.loaded[0] = 'true'
      // }
    },

    // //中心点上传到服务器
    //   submitUpload() {
    //     this.$refs.upload.submit();
    //   },
    //服务点导入
    handleChange2(file, fileList) {
      this.fileList2 = fileList.slice(-3);
      console.log(fileList);
      // if (that.fileList1.response.status == '导入成功') {
      //   that.loaded[1] = 'true'
      // }
      // console.log(that.loaded);
    },
    //车辆信息导入
    handleChange3(file, fileList) {
      let that = this;
      this.fileList3 = fileList.slice(-3);
      console.log(fileList);
      // if (that.fileList1.response.status == '导入成功') {
      //   that.loaded[1] = 'true'
      // }
    },
    //开始计算
    workOut(e) {
      let that = this;
      console.log(e);
      this.$axios
        .get(
           this.$url + "/question/executeAlgorithm",
          {
            params: { questionId: that.questionId } //that.questionId
          }
        )
        .then(function(response) {
          console.log(response);
          console.log(1);
          that.navImport = 'display:none',
          that.navrun = 'display:block'
          that.uploadSolution()
        })
        .catch(function(response) {
          console.log(response);
          console.log(22);
        });
    },


    //返回到模板导入界面
    to_Import() {
      this.navImport = 'display:block',
      this.navrun = 'display:none'
    },
    //方案展示
    uploadSolution() {
      let that = this;
 this.$axios
      .get(
         this.$url + "/finalSolution/getAllFinalSolution",
        {
          params: { questionId: that.questionId } //that.questionId
        }
      )
      .then(function(response) {
        console.log(response);
        for (let i in response.data.object) {
          response.data.object[i].name = "方案" + i;
          // for (let j in response.data.object[i].routes) {
          //   response.data.object[i].routes[j].name = '路线' + j;
          // }
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

        // 实例化一个驾车导航用来生成路线
        var drv = new BMap.DrivingRoute("重庆市", {
          onSearchComplete: function(res) {
            if (drv.getStatus() == BMAP_STATUS_SUCCESS) {
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
              //console.log(lushu);
              // lushu.push(
              //   new BMapLib.LuShu(map, arrPois, {
              //     defaultContent: "", //"从天安门到百度大厦"
              //     autoView: true, //是否开启自动视野调整，如果开启那么路书在运动过程中会根据视野自动调整
              //     icon: new BMap.Icon(
              //       "http://lbsyun.baidu.com/jsdemo/img/car.png",
              //       new BMap.Size(52, 26),
              //       { anchor: new BMap.Size(27, 13) }
              //     ),
              //     speed: 12000,
              //     enableRotation: true //是否设置marker随着道路的走向进行旋转
              //   })
              // );
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
        // for (var k = 0; k < data.data.length; k++) {
        //     var len = data.data[k].length;
        //     var arr = data.data[k].slice(1, len - 1);
        //     var p1 = new BMap.Point(data.data[k][0].longitude, data.data[k][0].latitude);
        //     var p2 = p1;
        //     var arr_map = [];
        //     for (var n = 0; n < arr.length; n++) {
        //         arr_map.push(new BMap.Point(arr[n].longitude, arr[n].latitude));
        //     }

        //     var p1 = new BMap.Point(106.495095,29.620681);
        //     var p2 = new BMap.Point(106.495095,29.620681);
        //     drv.search(p1, p2, {waypoints: arr_map});
        // }
        for (let k in data) {
          var p1 = new BMap.Point(data[k][0].lng, data[k][0].lat);
          var p2 = p1;
          var arr_map = [];
          for (let i in data[k]) {
            arr_map.push(new BMap.Point(data[k][i].lng, data[k][i].lat));
          }
          drv.search(p1, p2, { waypoints: arr_map });
        }
        // console.log(lushu);
        // console.log(Object.prototype.toString(lushu))
        //console.log(lushu.length);
        // for (let i in lushu) {
        //   console.log(lushu[i]);
        // }
        // for (let i = 0; i < lushu.length; i++) {
        //   lushu[i].start();
        //   console.log(0);
        // }
      });
      return p;
    },
    //路径绘制调用
    Solution(index) {
      console.log(index);
      let that = this;
      var color = ["#111", "red", "green", "blue", "purple", "#EE7600"];
      var ll = 0;
      let data = that.finalSolution[index].routes;
      that.lushu(ll, data, color);
      console.log(data);
    }
  },
  mounted() {
    let that = this;
    console.log("mounted");
    //加载完成触发已加载事件
    this.$emit("loaded");
    this.fristMap(); //调用地图
    //this.mapNew(); //调用地图
    //this.uploadSolution();
    //console.log(sessionStorage.getItem('userId'));
    //模拟登陆
    // this.$axios
    //   .get("http://192.168.43.115:8081/RoutePlanSystem/userSystem/verifyCode")
    //   .then(function(response) {
    //     console.log(response);
    //     console.log(1);
    //   })
    //   .catch(function(response) {
    //     console.log(response);
    //     console.log(22);
    //   });
    //模拟登陆
    // this.$axios
    //   .post(
    //     "http://192.168.43.115:8081/RoutePlanSystem/userSystem/session/user",
    //     this.$qs.stringify({
    //       userId: "3",
    //       password: "123456",
    //       code: "abcd"
    //     })
    //   )
    //   .then(function(response) {
    //     console.log(response);
    //     console.log(1);
    //   })
    //   .catch(function(response) {
    //     console.log(response);
    //     console.log(22);
    //   });
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



.el-main {
  margin: 0px;
  padding: 0px;
}
.addnew {
  position: fixed;
  right: 0px;
  bottom: 0px;
}

/* 导航步骤 */
.step {
  position: absolute;
  height: 100%;
  width: 300px;
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
</style>

