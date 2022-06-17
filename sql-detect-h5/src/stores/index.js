import Vue from 'vue'
import Vuex from 'vuex'
import perftest from './modules/perftest'
import * as actions from './actions'
import * as getters from './getters'
import mutations from './mutations'
import createLogger from 'vuex/dist/logger'

const packageInfo = require('../../package.json')

Vue.use(Vuex)

const state = {
    menuList: [],
    activeHeader: '',
    activeSider: '',
    openKey: '',
    //用来进行重新渲染sider
    siderKey: 0,
    loginUser: {},
    clientIp: '',
    appList: [],
    app: {
        packageInfo,
    },
    userInfo: {
        id: 0,
        name: '',
        gender: 1,
        avatarUrl: '',
        jobNumber: '',
    },
}

const debug = process.env.NODE_ENV !== 'production'

export default new Vuex.Store({
    state,
    getters,
    mutations,
    actions,
    modules: {
        perftest,
    },
    //在严格模式下，无论何时发生了状态变更且不是由 mutation 函数引起的，将会抛出错误
    strict: debug,
    //如果正在使用 vue-devtools，你可能不需要此插件
    plugins: debug ? [createLogger()] : [],
})
