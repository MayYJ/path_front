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
      <el-input
        placeholder="请输入地点"
        suffix-icon="el-icon-search"
        :style="inputStyle"
        v-model="input"
        @change="searchPoint"
      ></el-input>

      <el-button type="primary" round class="modelChange" @click="changemodel">模式切换</el-button>
      <retrun-home class="returnHome"/>
      <el-button type="primary" class="next" @click="toNext">下一步</el-button>
    </div>
    <div :is="currentView" v-on:func="changeNowmodel" ref="mychild"></div>
    <!-- <router-view ref="dataperparechild"/> -->
  </div>

</template>
<script>
import RetrunHome from "./Modular/RetrunHome.vue";
import MapMap from "./Modular/Map.vue";
import PointList from "./Modular/PointList.vue";
import HeaderHeader from "./Modular/Header.vue";
import FooterFooter from "./Modular/Footer.vue";
export default {
  name: "Index",
  //inject: ['reload'],
  data() {
    return {
      nowModel: 0, //当前模式
      input: "",
      inputStyle: "width:180px;margin-left:200px;z-index:1",
      MapMap: "MapMap",
      PointList: "PointList",
      currentView: "MapMap", // 默认选中第一项
    };
  },
  components: {
    RetrunHome,
    MapMap,
    PointList,
    HeaderHeader,
    FooterFooter,
  },
  methods: {
    changeNowmodel() {
      console.log(7844455);
      let that = this;
      //重载组建
      //that.toReload();
      if (that.nowModel == 0) {
        that.nowModel = 1;
        that.currentView = "PointList";
      } else {
        that.nowModel = 0;
        that.currentView = "MapMap";
      }
    },
    changemodel() {
      this.changeNowmodel();
    },
    searchPoint() {
      let that = this;
      if (that.nowModel == 1) {
        that.nowModel = 0;
        that.currentView = "MapMap";
      }
      new Promise(() => {});
      setTimeout(() => {
        if (that.input != "") {
          this.$refs.mychild.changePoint(that.input);
        }
      }, 2000);
    },
    //获取改问题下面所有点信息
    getAllpoinit() {
      let that = this;
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
              return true;
            } else {
              return false;
            }
          } else {
            that.$notify({
              title: "警告",
              message: response.data.msg,
              type: "warning"
            });
          }
        })
        .then(data => {});
    },
    //转到车辆导入
    toNext() {
      let that = this;
      //that.toReload();
      this.$axios
        .get(this.$url + "/node/getQuestionNodes", {
          params: {
            questionId: that.$route.query.id
          }
        })
        .then(res => {
          console.log(res);
          if (res.data.status == 0) {
            if (res.data.object.length != 0) {
              this.$router.push({
                path: "/VehiclePerpare",
                query: { id: that.$route.query.id }
              });
            } else {
              this.$notify({
                title: "警告",
                message: "请先导入点",
                type: "warning"
              });
            }
          } else {
            that.$notify({
              title: "警告",
              message: response.data.msg,
              type: "warning"
            });
          }
        });
    },
    //重载界面
    toReload () {
      //this.reload();
    }
  },
  beforeCreate: function() {
    console.log(this.$route.query.id);
  },
  beforeMount () {
  },
  mounted() {
    //this.reload();
    //this.window.reload()
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
/* .all {
    margin: 0 100px;
} */
.header {
  margin-top: 30px;
}
.modelChange {
  margin-left: 15px;
  display: inline;
  z-index: 1;
  position: relative;
}
.next {
  float: right;
  margin-right: 200px;
  position: relative;
  z-index: 1;
}
</style>
