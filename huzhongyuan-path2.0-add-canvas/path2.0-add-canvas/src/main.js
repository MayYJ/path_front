// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import VueRouter from 'vue-router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios'
import qs from 'qs';
import vuex from 'vuex'
import createStore from './store/store'
Vue.use(ElementUI)

axios.defaults.withCredentials = true;
Vue.config.productionTip = false;

Vue.prototype.$axios= axios
//Vue.prototype.$url = 'http://geniusdsy.cn/RoutePlanSystem';
Vue.prototype.$url = 'http://localhost:8081/RoutePlanSystem';
Vue.prototype.$qs = qs;
Vue.use(vuex);
Vue.use(VueRouter);
// const store = createStore();
const store = createStore();

new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>'
})
