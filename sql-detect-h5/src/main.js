import Vue from 'vue'
import Antd from 'ant-design-vue'
import App from './App.vue'
import 'ant-design-vue/dist/antd.less'
import './assets/styles/index.less'
import router from './router'
import store from './stores'
import LeftSider from './components/LeftSider'
import echarts from 'echarts'
import VJsoneditor from 'v-jsoneditor/src/index'
import '@/assets/styles/theme/index.css'
import EmaProxy from 'ema-proxy'
import JsonExcel from 'vue-json-excel'

window.EMA = new EmaProxy()

Vue.prototype.$echarts = echarts

Vue.config.productionTip = false

//全局注册插件
Vue.use(Antd)
Vue.use(VJsoneditor)

//全局注册组件
Vue.component('left-sider', LeftSider)
Vue.component('downloadExcel', JsonExcel)

//全局设置
Vue.prototype.$message.config({
    top: `80px`,
    duration: 2,
    maxCount: 3,
})

new Vue({
    router,
    store,
    render: h => h(App),
}).$mount('#app')
