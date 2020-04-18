<template>
  <el-button type="primary" round class="returnHome" @click="returnHome">回到首页</el-button>
</template>

<script>
export default {
  name: 'RetrunHome',
  data () {
    return {
      input: ''
    }
  },
  methods: {
    returnHome () {
      this.$router.push({path: '/'})
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.returnHome {
  margin-left: 15px;
  position: relative;
  z-index: 1;
}
</style>
