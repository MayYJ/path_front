<template>
  <div>
      <header-header />
      <div class="main">
      <el-steps :active="nowPage" finish-status="success" simple style="margin-top: 50px">
        <el-step title="选择中心点" ></el-step>
        <el-step title="选择路径点" ></el-step>
        <!-- <el-step title="选择车辆" ></el-step> -->
        <el-step title="路径展示" ></el-step>
      </el-steps>
      <select-center @toRoutePoint="toPage(1)" v-if="nowPage == 0"></select-center>
      <select-route-point @toCenterPoint="toPage(0)" @toSelectVehicle="toPage(2)" v-else-if="nowPage == 1"></select-route-point>
      <!-- <select-vehicle  v-else-if="nowPage == 2" @addCarCanvas="addCarToHome" @toShowRoute="toPage(3)"  ></select-vehicle> -->
      <show-route v-else></show-route>
      </div>
  </div>
</template>
<script>
import HeaderHeader from './Modular/Header'
import SelectCenter from './canvas/SelectCenter'
import SelectRoutePoint from './canvas/SelectRoutePoint'
// import SelectVehicle from './canvas/SelectVehicle'
import ShowRoute from './canvas/showRoute'
export default {
  data: () => {
    return {
      nowPage:0,
    }
  },
  methods: {
    toPage(index) {
      this.nowPage = index;
    },
    addCarToHome() {
      this.$emit('addCar');
    }
  },
  components: {
    SelectCenter,
    SelectRoutePoint,
    ShowRoute,
    HeaderHeader
  },
  mounted () {
    this.$emit('loaded');
  }
}
</script>

<style  scoped>
  .main{
    width:95%;
    margin: 0 2.5% 20px;

  }
</style>

