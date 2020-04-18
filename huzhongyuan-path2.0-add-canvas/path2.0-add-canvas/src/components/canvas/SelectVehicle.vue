<template>
  <div class="main">
    <el-button type="danger" class="deleteButton" @click="deleteCarCanvas">删除车辆 <i class="el-icon-remove"></i></el-button>
        <el-button type="success" class="addButton" @click="addCarCanvas">录入车辆 <i class="el-icon-circle-plus"></i></el-button>
        <br>
        <br>

        <car v-for="car in cars" :key="car.vehicleId"
            :vehicle-id="car.vehicleId"
            :type="car.type"
            :capacity="car.capacity"
            :oil="car.oil"
            :price="car.price"
            :del-flag="car.delFlag"
            ></car>

  </div>
</template>

<script>
import Car from '../VehicleManagement/car.vue'

export default {
  data() {
    return {
      cars: [

      ]
    }
  },
  methods: {
    addCarCanvas() {
        this.$emit('addCarCanvas');
        this.loadCarInfo();
      },
      deleteCarCanvas() {
        this.$confirm("你确定要删除所选择的车辆","提示",{
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then( () => {
          //批量删除
          this.$axios.delete('/vehicleSystem/user/vehicle',{
            data:this.$store.getters.getDeleteArray
          })
            .then( res => {
              console.log(res);
              switch(res.data.status){
                case 0:this.$message({
                  type:'info',
                  message:'功能存在异常'
                });break;
                case 1:this.$message({
                  type:'success',
                  message:'删除成功'
                });
                this.$store.commit('deleteArrayClear'); //清空Vuex中的数组
                this.loadCarInfo();
                break;
                case 2:this.$message({
                  type:'info',
                  message:'请登录后进行操作'
                });break;
                case 4:this.$message({
                  type:'info',
                  message:'不存在该车辆，请刷新页面后重试'
                });break;
                case 5:this.$message({
                  type:'danger',
                  message:'删除失败'
                });break;
              }
            })
        }).catch(() => {
          this.$message({
            type:'info',
            message:'您已取消删除'
          })
        })
      },
      loadCarInfo(){
        this.$axios({
          methods:'get',
          url:'/vehicleSystem/user/vehicle',
        })
          .then( (res) => {
            console.log(res);
            this.cars = res.data.status;
          })
      }
  }
}
</script>

<style scoped>
  .addButton{
    float: right;
    margin-right: 50px;
  }
  .deleteButton{
    float: right;
    margin-right: 100px;
  }
</style>

