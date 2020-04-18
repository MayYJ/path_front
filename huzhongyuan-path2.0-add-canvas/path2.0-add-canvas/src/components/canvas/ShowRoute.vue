<template>
  <div class="main">
    <el-row class="content">
        <el-col :span="24" class="canvasMap">
          <canvas id="map" width="1200px" height="800px"  >
            当前浏览器不支持Canvas，请升级浏览器或更换浏览器访问。
          </canvas>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="4" :offset="18">
          <el-button type="success" class="nextStepBtn" >完成演示</el-button>
        </el-col>
      </el-row>
  </div>
</template>

<script>
import qs from 'qs'
export default {
  data () {
    return {
      canvas : null,
      ctx : null,
      routes: [],
    }
  },
  mounted(){
    this.getRoutes()
    this.initCanvas()
  },
  methods: {
    // 获取路径点
    getRoutes () {
      let qid = this.$store.getters.getQid
      // 获取路径
      this.$axios.post(this.$url + '/demoNode/demoSolution', qs.stringify({
        questionId: qid,
        userId: 3
      }))
        .then(res => {
          console.table(res.data.object)
          if (res.data.status === 0) {
            this.$notify({
                    title: '成功',
                    message: '获取路径成功',
                    type: 'success'
                    })
            this.routes = res.data.object
            this.drawRoutes()
          } else {
            this.$notify({
                    title: '失败',
                    message: res.data.msg,
                    type: 'warning'
                    })
          }
        })
    },
    // 初始化Canvas
    initCanvas() {
      this.canvas = document.getElementById('map');
      this.ctx = this.canvas.getContext('2d');
    },
    //画点，传递渲染上下文、x坐标、y坐标、半径
    drawPoint(ctx,x,y,radius,color) {
      ctx.fillStyle = color;
      ctx.beginPath();
      ctx.arc(x, y, radius, 0, Math.PI*2, true);
      ctx.fill();
      ctx.closePath()
    },
    //获取在canvas的坐标
    getMousePos(canvas, event) {
      let rect = canvas.getBoundingClientRect();
      //保证画布的像素值和显示在页面上的尺寸不一致的情况下，也能够正确获取到鼠标相对于canvas中的坐标
      let x = event.clientX - rect.left * (canvas.width / rect.width);
      let y = event.clientY - rect.top * (canvas.height / rect.height);
      return {x:x,y:y}
    },
    //画出一条路径
    drawRoute (arr) {
      this.ctx.beginPath()
      this.ctx.lineWidth = 3
      //生成随机颜色路径
      this.ctx.strokeStyle = this.randomColor()
      // 先画点再画线
      for(let x in arr) {
        if (x == 0) {
          // 画中心点
          // this.drawPoint(this.ctx, arr[x].posX, arr[x].posY,10,"#409EFF")
          this.ctx.moveTo(arr[x].posX, arr[x].posY)
        } else {
          // 画路径点
          // this.drawPoint(this.ctx, arr[x].posX, arr[x].posY,5,"white")
          this.ctx.lineTo(arr[x].posX, arr[x].posY)
        }
      }
      this.ctx.stroke()
      for(let x in arr) {
        if (x == 0) {
          // 中心点开始
          // this.ctx.moveTo(arr[x].posX, arr[x].posY)
          this.drawPoint(this.ctx, arr[x].posX, arr[x].posY,10,"#409EFF")
        } else {
          // 画路径点的线段
          // this.ctx.lineTo(arr[x].posX, arr[x].posY)
           this.drawPoint(this.ctx, arr[x].posX, arr[x].posY,8,"white")
        }
        this.ctx.stroke()
      }

    },
    // 画出所有路径
    drawRoutes () {
      for (let x in this.routes) {
        this.drawRoute(this.routes[x])
      }
    },
    // 随机生成一种颜色
    randomColor () {
      let r = Math.floor(Math.random()*256);
      let g = Math.floor(Math.random()*256);
      let b = Math.floor(Math.random()*256);
      return `rgb(${r},${g},${b})`
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
  .nextStepBtn{
    margin-top: 20px;
    width: 100%;
    height: 60px;
  }
</style>

