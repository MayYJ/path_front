<template>
  <!-- <div>我是path界面</div> -->
  <div class="mapBox">
    <div id="allmap" ref="allmap" class="mapmap"></div>
    <!-- 点列表 -->
    <div class="pointList">
      <el-table :data="tableData" style="width: 100%" height="310">
        <el-table-column label="" width="10">
          <template >
            <!-- <span>{{ scope.row.id }}</span> -->
          </template>
        </el-table-column>
        <el-table-column label="点详细地址" width="250">
          <template slot-scope="scope">
            <!-- <el-popover trigger="hover" placement="top">
              <p>姓名: {{ scope.row.name }}</p>
              <p>住址: {{ scope.row.address }}</p>
              <div slot="reference" class="name-wrapper">
                <el-tag size="medium">{{ scope.row.name }}</el-tag>
              </div>
            </el-popover>-->
            <el-input v-model="scope.row.nodeAddress"></el-input>
            <!-- <span>{{ scope.row.nodeAddress }}</span> -->
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template slot-scope="scope">
            <!-- <el-button size="mini" @click="handleEdit(scope.$index, scope.row)">编辑</el-button> -->
            <el-button
              size="mini"
              type="danger"
              icon="el-icon-delete"
              circle
              @click="handleDelete(scope.$index, scope.row)"
            ></el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 点导入 -->
      <div class="updataPoint">
        <el-button type="warning" @click="updataPoint">上传所有点<i class="el-icon-upload el-icon--right"></i></el-button>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  //inject: ['reload'],
  data() {
    return {
      tableData: [

      ],
      questionId: '', //问题ID
      router: []
    };
  },
  components: {
    //组件
  },
  methods: {

    //初始化地图
    fristMap (res) {
      let that = this;
      let map = new BMap.Map(this.$refs.allmap, { enableMapClick: false }); // 创建Map实例
      //map.centerAndZoom(new BMap.Point(116.404, 39.915), 18); // 初始化地图,设置中心点坐标和地图级别
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

      var geoc = new BMap.Geocoder();
      map.addEventListener("click", function(e) {
        var pt = e.point;
        geoc.getLocation(pt, function(rs) {
          var addComp = rs.addressComponents;
          console.log(rs);
          console.log(rs.address);
          console.log(rs.point);
          // alert(
          //   addComp.province +
          //     ", " +
          //     addComp.city +
          //     ", " +
          //     addComp.district +
          //     ", " +
          //     addComp.street +
          //     ", " +
          //     addComp.streetNumber
          // );
          let value = {
            lat: rs.point.lat,
            lng: rs.point.lng,
            nodeAddress: rs.address,
            nodeName: rs.address,
            questionId: that.questionId,
          };
          that.tableData.unshift(value);
        });
      });
      if (res) {
        var local = new BMap.LocalSearch(map, {      
            renderOptions:{map: map}      
        });      
        local.search(res);
      }

          //获取改问题下面所有点信息
      this.$axios
        .get(this.$url + "/node/getQuestionNodes", {
          params: {
            questionId: that.$route.query.id
          }
        })
        .then(res => {
          if (res.data.status == 0) {
            console.log(res);
            if (res.data.object.length != 0) {
              console.log(res.data.object);
              that.router = res.data.object;
                map.centerAndZoom(new BMap.Point(res.data.object[0].lng, res.data.object[0].lat), 12); // 初始化地图,设置中心点坐标和地图级别
              for(let i in res.data.object) {
                var m1 = new BMap.Marker(new BMap.Point(res.data.object[i].lng, res.data.object[i].lat));         //创建3个marker
                map.addOverlay(m1);
                var lab1 = new BMap.Label(res.data.object[i].nodeAddress,{position:new BMap.Point(res.data.object[i].lng, res.data.object[i].lat)});        //创建3个label
                map.addOverlay(lab1);
              }
              return true;
            } else {
            map.centerAndZoom(new BMap.Point(116.404, 39.915), 18); // 初始化地图,设置中心点坐标和地图级别
              return false;
            }
          } else {
            map.centerAndZoom(new BMap.Point(116.404, 39.915), 18); // 初始化地图,设置中心点坐标和地图级别
            that.$notify({
              title: "警告",
              message: response.data.msg,
              type: "warning"
            });
          }
        })
        .then(res => {
            
        });


    },
    /*
    删除项目
*/
    handleDelete(index, row) {
      console.log(index, row);
      this.tableData.splice(index, 1);
    },
    /*
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

    //批量地图上传点
    updataPoint () {
      //that.toReload();
      console.log(111);
      let that = this;
      console.log(that.tableData);
      let questionId = that.questionId;
      console.log(questionId);
      console.log(that.tableData.length);
      let tableData = that.tableData;
      let nodes = that.tableData;
      let temp = {};
      temp.nodes = tableData;
      if (that.tableData.length != 0) {
        for (let i in  that.tableData) {
          console.log(that.tableData[i].nodeName)
          this.$axios
          .post(
             this.$url + "/node/newNode",
              that.$qs.stringify({
                questionId: that.questionId,
                nodeName: that.tableData[i].nodeName,
                nodeAddress: that.tableData[i].nodeAddress ,
                lat: that.tableData[i].lat,
                lng: that.tableData[i].lng,
              })
          )
          .then(res => {
            if (res.data.status == 0) {
              this.$notify({
                title: '成功',
                message: '点*' + that.tableData[i].nodeName + '*上传成功',
                type: 'success'
              });
              that.tableData = [];
              this.$emit('func');
            }  else {
            that.$notify({
              title: "警告",
              message: res.data.msg,
              type: "warning"
            });
          }
          });
      }

          // axios({
          //   method: 'post',
          //   url: this.$url + "/node/newNodeBatch",
          //   data: that.tableData,
          //   headers: {
          //       'content-type': 'application/json;charset=UTF-8'
          //    },
             
          // });
      }

    },
    //改变中心点
    changePoint (res) {
      this.fristMap(res);
    },
    //重载界面
    toReload () {
      //this.reload();
    }
  },
  mounted() {
    let that = this;
    this.questionId = this.$route.query.id;
    console.log("mounted");
    //加载完成触发已加载事件
    this.$emit("loaded");
    this.fristMap(); //调用地图
    //alert(this.$route.query.id);

    //加载完成触发已加载事件
    //this.$emit("loaded");
    //this.fristMap(); //调用地图
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
.pointList {
  position: absolute;
  left: 0;
  top: 180px;
  height: 400px;
  /* width: 300px; */
  background-color: white;
}
.updataPoint {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>

