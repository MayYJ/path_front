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
        <el-col :span="4" :offset="18">
          <el-button type="success" class="nextStepBtn" @click="storeCenterPoint">选择路径点按钮</el-button>
        </el-col>
      </el-row>
  </div>
</template>

<script>
//计数器
export default {
  data () {
    return {
      canvas : null,
      ctx : null,
      points:[]
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
  },
  methods: {
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
    },
    //点击实现画点
    clickToDrawPoint(e){
      //获取坐标
      let coordinate = this.getMousePos(this.canvas, e);
      //画点
      this.drawPoint(this.ctx, coordinate.x, coordinate.y, 10,"#409EFF");
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
    //点击实现跳转，并把当前中心点传入状态管理
    storeCenterPoint() {
      this.$store.commit('setCenterPoints',this.points)
      this.$emit('toRoutePoint');
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

