<template>
  <div>
    <div id="allmap" ref="allmap" class="mapmap"></div>
  </div>
</template>
<script>
export default {
  data() {
    return {};
  },
  methods: {},
  mounted() {
    let that = this;
    let lat = that.$route.query.lat;
    let lng = that.$route.query.lng;
    let nodeAddress = that.$route.query.nodeAddress;
    let map = new BMap.Map(this.$refs.allmap, { enableMapClick: false }); // 创建Map实例
    map.centerAndZoom(new BMap.Point(lng, lat), 14); // 初始化地图,设置中心点坐标和地图级别
    
    var m1 = new BMap.Marker(
      new BMap.Point(lng, lat)
    ); //创建1个marker
    map.addOverlay(m1);
    var lab1 = new BMap.Label(nodeAddress, {
      position: new BMap.Point(lng, lat)
    }); //创建1个label
    map.addOverlay(lab1);
    
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
  }
};
</script>

<style>
.mapmap {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: red;
  z-index: 0;
}
</style>
