<template>
  <div class="allBox">
    <header-header class="headerheader" />
    <div class="all">
      <div class="add">
        <el-button @click="indexAddPrograme" type="primary" icon="el-icon-circle-plus">添加项目</el-button>
        <el-dialog
          title="提示"
          :visible.sync="dialogVisible"
          width="30%"
          >
          <span>项目名称：</span><el-input v-model="input" placeholder="请输入内容"></el-input>
          <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
          </span>
        </el-dialog>
      </div>
      <div>
        <router-view ref="child1"/>
      </div>
    </div>
    <footer-footer />
  </div>
</template>

<script>
import HeaderHeader from "./Modular/Header.vue";
import FooterFooter from "./Modular/Footer.vue";
export default {
  name: 'Index',
  data () {
    return {
      msg: 'Welcome to Your Vue.js App',
      dialogVisible: false,
      input: ''
    }
  },
  components: {
    HeaderHeader,
    FooterFooter,
  },
  methods: {
    indexAddPrograme () {
      let that = this;
      this.$prompt('请输入项目名称', '添加项目', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\S/,
        inputErrorMessage: '名称格式不正确'
      }).then(({ value }) => {
        this.$message({
          type: 'success',
          message: '你的项目名称是: ' + value
        })
        let arr = {
          id: '1',
          date: '2016-05-03',
          name: value
        }
        this.$axios.post(that.$url + "/question/insertQuestion", that.$qs.stringify({
          questionName: value,
          userId: sessionStorage.getItem('userId')
        })
        )
        .then(function (response) {
          console.log(response);
          that.$refs.child1.addPrograme(response);
        })
        .catch(function (error) {
          console.log(error);
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消添加'
        })
      })
    },
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.all {
    margin: 0 100px;
    margin-top: 40px;
}
.add {
  display: flex;
  justify-content: flex-end;
}
</style>
