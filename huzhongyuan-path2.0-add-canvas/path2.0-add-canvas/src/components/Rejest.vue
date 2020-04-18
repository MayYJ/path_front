<template>
  <div class="login">
<el-card class="box-card">
  <div slot="header" class="clearfix">
    <span>注册</span>
    <el-button style="float: right; padding: 3px 0" type="text" @click="toLogin">去登陆</el-button>
  </div>
    <el-form
      :model="ruleForm2"
      status-icon
      :rules="rules2"
      ref="ruleForm2"
      label-width="100px"
      class="demo-ruleForm"
    >
      <el-form-item label="用户名" prop="userName">
        <el-input type="user" v-model="ruleForm2.userName" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="pass">
        <el-input type="password" v-model="ruleForm2.pass" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="checkPass">
        <el-input type="password" v-model="ruleForm2.checkPass" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item class="verifyCodeBox" label="邮箱" prop="email">
        <el-input type="user" v-model="ruleForm2.email" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item class="verifyCodeBox" label="邮箱验证" prop="emailCode">
        <el-input v-model="ruleForm2.emailCode">
          <el-button  class="getCode" slot="append" @click="getCode">获取验证码</el-button>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm('ruleForm2')">注册并登陆</el-button>
        <el-button @click="resetForm('ruleForm2')">重置</el-button>
      </el-form-item>
    </el-form>
</el-card>

  </div>
</template>

<script>
export default {
  name: "Login",

  data() {
    //邮箱
    var checkEmail= (rule, value, callback) => {
      if (!value) {
        return callback(new Error("邮箱不能为空"));
      }
      setTimeout(() => {
        let reg=/^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/;    
        if (!reg.test(value)) {
          callback(new Error("邮箱格式错误"));
        } else {
            callback();
        }
      }, 500);
    };
    //用户名
    var user = (rule, value, callback) => {
      if (!value) {
        return callback(new Error("用户名不能为空"));
      }
      setTimeout(() => {  
        let reg=/^[\u0391-\uFFE5]+$/;//汉字   
        // let reg=/^[0-9]*$/;  
        if (!reg.test(value)) {
          callback(new Error("请输入中文名"));
        } else {
            callback();
        }
      }, 500);
    };
    //密码
    var validatePass = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请输入密码"));
      } else {
        if (this.ruleForm2.checkPass !== "") {
          this.$refs.ruleForm2.validateField("checkPass");
        }
        callback();
      }
    };
    //确认密码
    var validatePass2 = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请再次输入密码'));
        } else if (value !== this.ruleForm2.pass) {
          callback(new Error('两次输入密码不一致!'));
        } else {
          callback();
        }
   };
    //验证码
    var checkEmailCode = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请输入验证码"));
      } else {
        callback();
      }
    };
    return {
      ruleForm2: {
        userName: '',  //用户名
        pass: "",      //密码
        checkPass: "", //确认密码
        email: '',     //邮箱
        emailCode: '', //邮箱验证码
      },
      rules2: {
        userName: [{ validator: user, trigger: "blur" }],
        pass: [{ validator: validatePass, trigger: "blur" }],
        checkPass: [
            { validator: validatePass2, trigger: 'blur' }
          ],
        email: [{ validator: checkEmail, trigger: "blur" }],
        emailCode: [{ validator: checkEmailCode, trigger: "blur" }]
      },
      verifyCodeURL: '',//二维码链接
    };
  },
  methods: {
    //重置表单
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    //转到登陆界面
    toLogin () {
        this.$router.push({path: '/Login'})
    },
    //提交表单
    submitForm(formName) {
      let that = this;
      this.$refs[formName].validate(valid => {
        if (valid) {
          //alert("submit!");
          that.rejest();
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    //获取验证码
    getCode () {
      let that = this;
      if ( that.ruleForm2.email == '') {
        that.$notify({
          title: '警告',
          message: '请先填写邮箱地址',
          type: 'warning'
        });
      } else {
        //alert(that.ruleForm2.email);
        that.$axios.get(that.$url + "/userSystem/user/eMailCode", {
          params: {
            eMail: that.ruleForm2.email
          }
        })
        .then(function (response) {
          console.log(response);
        })
        .catch(function (error) {
          console.log(error);
        });
      }
    },
    //用户注册
    rejest () {
      let that = this;
        this.$axios
          .post(
             this.$url + "/userSystem/user",
              that.$qs.stringify({
                userName: that.ruleForm2.userName,
                password: that.ruleForm2.pass,
                rePassword: that.ruleForm2.checkPass,
                eMail : that.ruleForm2.email,
                mailCode : that.ruleForm2.emailCode,
              })
          )
          .then(res => {
            console.log(res);
            if (res.data.status == 0) {
              //把登录信息存入state
              this.$store.commit("loginState", {
                userId: res.data.object.userId,
                userName: res.data.object.userName
              });
              this.$router.push({path: '/'})
            } else if (res.data.status == 1) {
              this.$alert("用户名、密码或验证码错误!", "通知", {
                confirmButtonText: "我知道了",
                type: "error"
              });
            } else if (res.data.status == 3) {
              this.$alert("验证码错误或为空！", "通知", {
                confirmButtonText: "我知道了",
                type: "error"
              });
              this.verifyCode(); //再次请求验证码
            } else {
              this.$alert("登录失败！", "通知", {
                confirmButtonText: "我知道了",
                type: "error"
              });
            }
          });
    },
    //用户登陆
    Login () {
      let that = this;
        console.log(that.ruleForm2.userName);
        this.$axios
          .post(
             this.$url + "/userSystem/session/user",
              that.$qs.stringify({
                userName: that.ruleForm2.userName,
                password: that.ruleForm2.pass,
                code: that.ruleForm2.verifyCode
              })
          )
          .then(res => {
            console.log(res);
            if (res.data.status == 0) {
              //把登录信息存入state
              // this.$store.commit("loginState", {
              //   userId: this.userName,
              //   userName: res.data.userMessage.userName
              // });
              this.$router.push({path: '/'})
            } else if (res.data.status == 1) {
              this.$alert("用户名、密码或验证码错误!", "通知", {
                confirmButtonText: "我知道了",
                type: "error"
              });
            } else if (res.data.status == 3) {
              this.$alert("验证码错误或为空！", "通知", {
                confirmButtonText: "我知道了",
                type: "error"
              });
              this.verifyCode(); //再次请求验证码
            } else {
              this.$alert("登录失败！", "通知", {
                confirmButtonText: "我知道了",
                type: "error"
              });
            }
          });
    },
  },
  mounted () {

  },

};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.login {
    margin-top: 100px;
}
  .text {
    font-size: 14px;
  }

  .item {
    margin-bottom: 18px;
  }

  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }
  .clearfix:after {
    clear: both
  }

  .box-card {
    width: 480px;
    margin: 0px auto;
  }

  .verifyCode {
    margin-left: 100px;
    cursor: pointer;
  }

  .getCode {
    cursor: pointer;
  }
</style>
