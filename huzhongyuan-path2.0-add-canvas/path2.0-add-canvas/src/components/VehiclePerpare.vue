<template>
  <div class="all">
    <div class="header">
      <!-- <el-input
        placeholder="请输入内容"
        prefix-icon="el-icon-search"
        v-model="input"
        clearable
        :style="inputStyle"
        >
      </el-input>-->
      <retrun-home class="returnHome" :style="inputStyle"/>
      <el-button type="primary" round class="modelChange" >模式切换</el-button>
      <div class="buttonBox">
        <el-button type="primary" style="margin-right:20px;" @click="toPrevious">上一步</el-button>
        <el-button type="primary" @click="finish">完成</el-button>
      </div>
    </div>

    <div :is="currentView" v-on:func="changeNowmodel" ref="mychild"></div>

    <!-- <router-view ref="dataperparechild"/> -->
  </div>
</template>
<script>
import RetrunHome from "./Modular/RetrunHome.vue";
import VehicleList from "./Modular/VehicleList.vue";
import VehicleEditor from "./Modular/VehicleEditor.vue";
export default {
  name: "vehiclePerpare",
  data() {
    return {
      VehicleList: 'VehicleList',
      VehicleEditor: 'VehicleEditor',
      nowModel: 0,
      currentView: 'VehicleEditor',
      inputStyle: "margin-left:200px;z-index:1",
    };
  },
  components: {
    RetrunHome,
    VehicleList,
    VehicleEditor,
  },
  methods: {
    //上一步
    toPrevious() {
      this.$router.push({ path: "/DataPerpare", query: { id:  this.$route.query.id} });
    },
    //完成准备
    finish() {
      let that = this;
      that.$axios
        .get(that.$url + "/vehicleSystem/user/vehicle", {
          params: {
            questionId: this.$route.query.id
          }
        })
        .then(function(response) {
          if (response.data.status == 0) {
          console.log(response)
          if (response.data.object.length != 0) {
                     that.$notify({
            title: '数据导入成功',
            message: '三秒钟后跳转到主页',
            type: 'success'
          });
          setTimeout(function() {
            that.$router.push({ path: "/" });
          },3000)
          } else {
                    that.$notify({
          title: '警告',
          message: '请先导入车辆',
          type: 'warning'
        });
          }
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

        //  that.$notify({
        //     title: '数据导入成功',
        //     message: '三秒钟后跳转到主页',
        //     type: 'success'
        //   });
        //   setTimeout(function() {
        //     that.$router.push({ path: "/" });
        //   },3000)
      // let that = this;
      //   that.$axios.get(that.$url + "/generateDistance", {
      //     params: {
      //       questionId: this.$route.query.id
      //     }
      //   })
      //   .then(function (response) {
      //     console.log(response);
      //     if (response.data.status == 0) {
      //     that.$notify({
      //       title: '数据导入成功',
      //       message: '三秒钟后跳转到主页',
      //       type: 'success'
      //     });
      //     setTimeout(function() {
      //       that.$router.push({ path: "/" });
      //     },3000)

      //     }
      //   })
      //   .catch(function (error) {
      //     console.log(error);
      //   });
      // 
    },
    //切换模块
    changeNowmodel () {
      console.log(7844455)
      let that = this
      if(that.nowModel == 0) {
        that.nowModel = 1
        that.currentView = 'VehicleEditor'
      } else {
        that.nowModel = 0
        that.currentView = 'VehicleList'
      }
    },
    changemodel () {
      this.changeNowmodel()
    },
  },
  beforeCreate: function() {
    console.log(this.$route.query.id);
  },
  mounted() {

  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
/* .all {
    margin: 0 100px;
} */
.header{
  margin-top: 30px;
}
.buttonBox {
  float: right;
  margin-right: 200px;
  position: relative;
  z-index: 1;
}

.modelChange {
  margin-left: 15px;
  display: inline;
  z-index: 1;
  position: relative;
}
</style>
