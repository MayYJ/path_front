<template>
  <div class="main">
    <el-row class="content">
        <el-col :span="18" class="canvasMap">
          <canvas id="map" width="1200px" height="800px"  @click="clickToDrawPoint">
            当前浏览器不支持Canvas，请升级浏览器或更换浏览器访问。
          </canvas>
        </el-col>
        <el-col :span="4" class="side">
          <el-row class="pointInfo" v-for="point in points" :key="point.index" type="flex" justify="space-between">
            <el-col :span="4" class="index">
              <el-tag >{{point.index}}</el-tag>
            </el-col>
            <el-col :span="12" class="xy">
              X:{{point.x}}&nbsp;&nbsp;Y:{{point.y}}
            </el-col>
            <el-col :span="4">
              <el-button type="danger" icon="el-icon-delete" circle @click="deletePoint(--point.index)"></el-button>
            </el-col>
          </el-row>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="4" :offset="13">
          <el-button type="info" class="lastStepBtn" @click="toLastStep">返回上一步</el-button>
        </el-col>
        <el-col :span="4" :offset="1">
          <el-button type="success" class="nextStepBtn" @click="storeCenterPoint">生成路径</el-button>
        </el-col>
      </el-row>
  </div>
</template>

<script>
import qs from 'qs'
//计数器
export default {
  data () {
    return {
      canvas : null,
      ctx : null,
      points:[],
      qid:0
    }
  },
  watch: {
    //给点数组添加索引
    points(){
      for(let i =0;i < this.points.length;i++){
        this.points[i].index = i+1;
      }
    }
  },
  mounted(){
    this.initCanvas();
    this.drawCenterPoints();
    this.getMostNum()
  },
  methods: {
    initCanvas() {
      this.canvas = document.getElementById('map');
      this.ctx = this.canvas.getContext('2d');
    },
    //将中心点画在画布上
    drawCenterPoints() {
      let centerPoints = this.$store.getters.getCenterPoints
      for(var i=0;i < centerPoints.length;i++) {
        this.drawPoint(this.ctx, centerPoints[i].x, centerPoints[i].y, 10, "#409EFF")
      }
    },
    //画点，传递渲染上下文、x坐标、y坐标、半径
    drawPoint(ctx,x,y,radius,color) {
      ctx.fillStyle = color;
      ctx.beginPath();
      ctx.arc(x, y, radius, 0, Math.PI*2, true);
      ctx.fill();
    },
    //点击实现画点
    clickToDrawPoint(e){
      //获取坐标
      let coordinate = this.getMousePos(this.canvas, e);
      //画点
      this.drawPoint(this.ctx, coordinate.x, coordinate.y, 8,"white");
      //把点存入数据集
      this.points.push(coordinate);
    },
    //获取在canvas的坐标
    getMousePos(canvas, event) {
      let rect = canvas.getBoundingClientRect();
      //保证画布的像素值和显示在页面上的尺寸不一致的情况下，也能够正确获取到鼠标相对于canvas中的坐标
      let x = (event.clientX - rect.left * (canvas.width / rect.width)).toFixed(0);
      let y = (event.clientY - rect.top * (canvas.height / rect.height)).toFixed(0);
      return {x:x,y:y}
    },
    //删除数组指定位置的元素,并在原来的点区域画一个与底部颜色相同的点来
    deletePoint(index){
      if (isNaN(index) || index > this.points.length ) {return false}
      let point = this.points[index];
      this.drawPoint(this.ctx,point.x,point.y,10,"#888");
      for (var i=0,n=0; i < this.points.length; i++ ) {
        if (this.points[i] != this.points[index]){
          this.points[n++] = this.points[i]
        }
      }
      this.points.length -= 1;
      for(let i =0;i < this.points.length;i++){
        this.points[i].index = i+1;
      }
      //数据层过多，手动触发重新渲染
      this.$forceUpdate();
    },
    //点击返回上一步
    toLastStep() {
      this.$emit('toCenterPoint');
    },
    // 上传中心点与路径点
    uploadPoints (uid,qid) {
      let centerPoints = this.$store.getters.getCenterPoints
      let routePoints = this.$store.getters.getRoutePoints
      let toSendCenterPoints = []
      let toSendRoutePoints = []
      let flag = [false, false]
      // 修改属性名
      for (let x in centerPoints) {
        toSendCenterPoints[x] = {userId:uid, questionId:qid, posId: parseInt(x)+1, posX: centerPoints[x].x, posY: centerPoints[x].y, isCenter:1}
      }
      let num = toSendCenterPoints.length
      for (let x in routePoints) {
        toSendRoutePoints[x] = {userId:uid, questionId:qid, posId: parseInt(num+x)+1, posX: routePoints[x].x, posY: routePoints[x].y, isCenter:0}
      }
      // 上传中心点
      this.$axios.post(this.$url + '/demoNode/demoNewNodes', {
          jsonArray:toSendCenterPoints
      },{
        ContentType: 'application/json'
      })
        .then(res => {
          console.log(res)
          if (res.data.status === 0) {
            this.$notify({
                  title: '成功',
                  message: '录入中心点成功',
                  type: 'success'
                  })
            flag[0] = true
            if (flag[0] && flag[1]) {
              this.$emit('toSelectVehicle')
            }
          } else {
            this.$notify({
                  title: '失败',
                  message: '录入中心点失败',
                  type: 'warning'
                  })
          }
        })
    // 上传路径点
    this.$axios.post(this.$url + '/demoNode/demoNewNodes', {
          jsonArray:toSendRoutePoints
      },{
        ContentType: 'application/json'
      })
        .then(res => {
          console.log(res)
          if (res.data.status === 0) {
            this.$notify({
                  title: '成功',
                  message: '录入路径点成功',
                  type: 'success'
                  })
            flag[1] = true
            if (flag[0] && flag[1]) {
              this.$emit('toSelectVehicle')
            }
          } else {
            this.$notify({
                  title: '失败',
                  message: '录入路径点点失败',
                  type: 'warning'
                  })
          }
        })
    },
    // 获得当前用户的最大问题号
    getMostNum () {
      this.$axios.get(this.$url + '/demoNode/demoNodeMaxQuestionId', {
        params: {
          userId: 3
        }
      })
        .then(res => {
          if(res.data.status === 0) {
            this.qid = res.data.object + 1
            this.$store.commit('setQid', res.data.object + 1)
          } else {
            this.$notify({
                  title: '失败',
                  message: res.data.msg,
                  type: 'warning'
                  })
          }
        })
    },
    //点击实现跳转，并把当前中心点传入状态管理
    storeCenterPoint() {
      this.$store.commit('setRoutePoints',this.points)
      // 上传点
      let qid = this.qid
      console.log(qid)
      this.uploadPoints(3,qid)
    }
  }
}
</script>

<style scoped>
  .content{
    position: relative;
    margin-top: 20px;
    height: 800px;
  }
  .canvasMap{
    position: relative;
    height: 100%;
    overflow-x:auto;
    overflow-y: hidden;
  }
  .side{
    position: relative;
    border: 3px solid #409eff;
    height: 100%;
    overflow-y: auto;
    background: #888;
  }
  #map{
    position: absolute;
    cursor: pointer;
    background: #888;
  }
  .pointInfo{
    position: relative;
    box-sizing: border-box;
    width: 100%;
    height: 60px;
    margin-bottom: 10px;
    background: #fff;
    line-height: 60px;
  }
  .pointInfo:last-child{
    margin-bottom: 0px;
  }
  .pointInfo el-col{
    line-height: 60px;
  }
  .lastStepBtn{
    margin-top: 20px;
    width: 100%;
    height: 60px;
  }
  .nextStepBtn{
    margin-top: 20px;
    width: 100%;
    height: 60px;
  }
</style>

