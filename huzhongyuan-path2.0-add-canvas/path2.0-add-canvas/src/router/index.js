import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import App from '@/components/App'
import Login from '@/components/Login'
import Rejest from '@/components/Rejest'
import Index from '@/components/Index'
import Indexc from '@/components/Indexc'
import DataPerpare from '@/components/DataPerpare'
import BAIDUMap from '@/components/Modular/Map'
import TEST from '@/components/Modular/TEST'
import SinglePoint from '@/components/Modular/SinglePoint'
import FlashPage from '@/components/Modular/FlashPage'
import VehiclePerpare from '@/components/VehiclePerpare'
import Path from '@/components/Path'
import CanvasBody from '@/components/CanvasBody'
Vue.use(Router)

const router = new Router({
  routes: [
    {
      path: '/Login',
      name: 'Login',
      component: Login,
      meta: {
        login_require: false
      },
    },
    {
      path: '/Rejest',
      name: 'Rejest',
      component: Rejest,
      meta: {
        login_require: false
      },
    },
    {
      path: '/TEST',
      name: 'Rejest',
      component: TEST,
      meta: {
        login_require: true
      },
    },
    {
      path: '/',
      name: 'Index',
      component: Index,
      meta: {
        login_require: true
      },
      children: [
        {
          path: '/',
          name: 'Indexc',
          component: Indexc,
          meta: {
            login_require: true
          },
        }
      ]
    },
    {
      path: '/DataPerpare',
      name: 'DataPerpare',
      component: DataPerpare,
      meta: {
        login_require: true
      },
      children: [
        {
          path: '/',
          name: '',
          component: BAIDUMap,
          meta: {
            login_require: true
          },
        }
      ]
    },
    {
      path: '/VehiclePerpare',
      name: 'VehiclePerpare',
      component: VehiclePerpare,
      meta: {
        login_require: true
      },
      children: [
        {
          path: '/',
          name: '',
          component: BAIDUMap,
          meta: {
            login_require: true
          },
        }
      ]
    },
    {
      path: '/Path',
      name: 'Path',
      component: Path,
      meta: {
        login_require: true
      },
    },
    {
      path: '/FlashPage',
      name: 'FlashPage',
      component: FlashPage,
      meta: {
        login_require: true
      },
    },
    {
      path: '/SinglePoint',
      name: 'SinglePoint',
      component: SinglePoint,
      meta: {
        login_require: true
      },
    },
    {
      path: '/APP',
      name: 'App',
      component: App,
      meta: {
        login_require: true
      },
    },
    {
      path: '/HelloWorld',
      name: 'HelloWorld',
      component: HelloWorld,
      meta: {
        login_require: true
      },
    },
    {
      path: '/canvas',
      name: 'Canvas',
      component: CanvasBody,
      meta: {
        login_require: true
      }
    }
  ]
})

//路由守卫，检测是否已经登录
router.beforeEach((to, from, next) => {
  if (to.meta.login_require) { // 如果前往页面需要登录
    if (sessionStorage.getItem('userId') == null) { // 未登录
      next('/Login')// 跳转到登录界面
    } else { // 已登录
      next()
    }
  } else { // 前往页面不需要登录
    next()
  }
})

export default router;
