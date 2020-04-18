<template>
    <div class="header">
            <div class="line"></div>
            <el-menu
            :default-active="activeIndex2"
            class="el-menu-demo"
            mode="horizontal"
            @select="handleSelect"
            background-color="#545c64"
            text-color="#fff"
            active-text-color="#ffd04b">
            <el-menu-item index="0" style="width:100px;"></el-menu-item>
            <el-menu-item index="1" @click="home">Home</el-menu-item>
            <el-menu-item index="2" @click="canvas">二维演示</el-menu-item>
            <!-- <el-submenu index="2">
                <template slot="title">我的工作台</template>
                <el-menu-item index="2-1">选项1</el-menu-item>
                <el-menu-item index="2-2">选项2</el-menu-item>
                <el-menu-item index="2-3">选项3</el-menu-item>
                <el-submenu index="2-4">
                <template slot="title">选项4</template>
                <el-menu-item index="2-4-1">选项1</el-menu-item>
                <el-menu-item index="2-4-2">选项2</el-menu-item>
                <el-menu-item index="2-4-3">选项3</el-menu-item>
                </el-submenu>
            </el-submenu> -->
            <el-menu-item index="3" disabled>消息中心</el-menu-item>
            <el-menu-item index="4" disabled>个人中心</el-menu-item>
            </el-menu>
    </div>
</template>
<script>
  export default {
    data() {
      return {
        activeIndex: '1',
        activeIndex2: ''
      };
    },
    methods: {
      handleSelect(key, keyPath) {
        console.log(key, keyPath);
      },
      home() {
        this.$router.push({path: '/'})
      },
      canvas() {
        this.$router.push({path: '/canvas'})
      }
    }
  }
</script>
<style>
.header {
    position: relative;
    width: 100%;
    left: 0px;
    top: 0px;
    z-index: 1002;
}
</style>
