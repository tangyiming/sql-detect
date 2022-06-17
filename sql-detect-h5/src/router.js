import Vue from 'vue'
import Router from 'vue-router'
import Home from './views/home.vue'

Vue.use(Router)
// 避免重复同导航跳转报错
const originalPush = Router.prototype.push
Router.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}

export default new Router({
    mode: 'hash',
    routes: [
        {
            path: '/',
            name: 'home',
            component: Home,
        },
        {
            path: '/404',
            name: '404',
            // route level code-splitting
            // this generates a separate chunk (about.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import(/* webpackChunkName: "customUpload" */ './views/404'),
        },
        {
            path: '/sqlananysis',
            name: 'sqlananysis',
            component: () => import('./views/sqlananysis'),
            children: [
                {
                    path: 'dashboard',
                    // eslint-disable-next-line prettier/prettier
                    component: require('./views/sqlananysis/dashboard').default,
                },
                {
                    path: 'services',
                    // eslint-disable-next-line prettier/prettier
                    component: require('./views/sqlananysis/services').default,
                },
                {
                    path: 'tables',
                    // eslint-disable-next-line prettier/prettier
                    component: require('./views/sqlananysis/tables').default,
                },
                {
                    path: 'sqls',
                    // eslint-disable-next-line prettier/prettier
                    component: require('./views/sqlananysis/sqls').default,
                },
                {
                    path: 'rules',
                    // eslint-disable-next-line prettier/prettier
                    component: require('./views/sqlananysis/rules').default,
                },
            ],
        },
        {
            path: '/tool',
            name: 'tool',
            component: () => import('./views/tool'),
            children: [
                {
                    path: 'tasks',
                    // eslint-disable-next-line prettier/prettier
                    component: require('./views/tool/tasks').default,
                }
            ],
        },
        {
            path: '/*',
            name: '404',
            component: () => import('./views/404'),
        },
    ],
})
