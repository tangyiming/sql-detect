<template>
    <a-config-provider :locale="locale">
        <div style="font-family: 'Microsoft Yahei'">
            <a-layout id="components-layout-demo-top-side">
                <a-layout-header class="header">
                    <span class="logo">SQL</span>
                    <span class="top-menu">
                        <a-menu theme="light" mode="horizontal" :defaultSelectedKeys="defaultSelectedKeys" @click="handleMenuClick" :key="headerKey">
                        <!-- 默认key值一定要与url路径xxx/key/xxxx相同 -->
                            <a-menu-item key="home">首页</a-menu-item>
                            <a-menu-item key="sqlananysis">慢SQL分析</a-menu-item>
                            <a-menu-item key="tool">小工具</a-menu-item>
                        </a-menu>
                    </span>
                    <span class="top-right">
                        {{ loginUser.departmentName + ' | ' + loginUser.name + ' | ' + loginUser.id }}
                        <span class="top-login">
                            <a-dropdown :trigger="['click']">
                                <a class="ant-dropdown-link" href>
                                    <a-icon type="caret-down" />
                                </a>
                                <a-menu slot="overlay">
                                    <a-menu-item key="0">
                                        <a @click="logOut">登出</a>
                                    </a-menu-item>
                                </a-menu>
                            </a-dropdown>
                        </span>
                    </span>
                </a-layout-header>
            </a-layout>
            <div>
                <router-view v-if="isRouterAlive" />
            </div>
        </div>
    </a-config-provider>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'
import http from './utils/http'

// axios实例调用sso
// import axios from 'axios'
// const instance = axios.create({
//     baseURL: '/sso/', //两边不加斜杠是坑，大坑，最后生成的地址莫名其妙
// })

export default {
    name: 'app',
    provide() {
        return {
            reload: this.reload,
        }
    },
    data() {
        return {
            locale: zhCN,
            defaultSelectedKeys: ['home'],
            activeHeader: '',
            activeSider: '',
            openKey: '',
            headerKey: 1,
            isRouterAlive: true,
            env: 'dev',
        }
    },

    computed: {
        ...mapState({
            menuList: state => state.menuList,
            siderKey: state => state.siderKey,
            loginUser: state => state.loginUser,
        }),
    },

    watch: {
        menuList: function (newval) {
            this.changeOpenkey(newval)
        },
        $route: function () {
            this.initMenu()
            this.headerKey += 1
        },
    },
    beforeMount() {
        this.initMenu()
    },
    mounted() {
        localStorage.setItem('loginUser', { departmentName: 'QA', name: '唐一鸣', id: '1020215', jobNumber: 'A1020215' })
        localStorage.setItem('userName', '唐一鸣')
        this.setLoginUser({ departmentName: 'QA', name: '唐一鸣', id: 'A1020215', jobNumber: 'A1020215' })
    },

    methods: {
        ...mapActions(['setActiveHeader', 'setActiveSider', 'setOpenKey', 'setSiderKey', 'setLoginUser']),

        logOut() {},

        changeOpenkey: function (val) {
            let routerMode = this.$router.mode
            let path
            if (routerMode === 'history') {
                this.openKey = ''
                path = window.location.pathname.substring(1)
                val.forEach(item => {
                    if (item.hasOwnProperty('children')) {
                        item.children.forEach(it => {
                            if (it.path.substring(1) === path) {
                                this.openKey = item.key
                            }
                        })
                    }
                })
            } else {
                this.openKey = ''
                path = window.location.hash.substring(1)
                val.forEach(item => {
                    if (item.hasOwnProperty('children')) {
                        item.children.forEach(it => {
                            if (it.path === path) {
                                this.openKey = item.key
                            }
                        })
                    }
                })
            }
            this.setOpenKey(this.openKey)
        },

        //根据路由设置选中头部菜单,侧边菜单
        initMenu() {
            let routerMode = this.$router.mode
            let paths
            if (routerMode === 'history') {
                if (window.location.pathname !== '/') {
                    paths = window.location.pathname.substring(1).split('/')
                    this.defaultSelectedKeys[0] = paths[0]
                    this.activeHeader = paths[0]
                    // 解决路由使用query方式传参时，无法正确高亮侧边菜单
                    this.activeSider = paths[1].split('?')[0]
                } else {
                    this.defaultSelectedKeys[0] = 'home'
                }
            } else {
                if (window.location.hash !== '#/') {
                    paths = window.location.hash.substring(1).split('/')
                    this.defaultSelectedKeys[0] = paths[1]
                    this.activeHeader = paths[1]
                    // 解决路由使用query方式传参时，无法正确高亮侧边菜单
                    this.activeSider = paths[2].split('?')[0]
                } else {
                    this.defaultSelectedKeys[0] = 'home'
                }
            }
            this.setActiveHeader(this.activeHeader)
            this.setActiveSider(this.activeSider)
            this.changeOpenkey(this.menuList)
            this.setSiderKey(this.siderKey + 1)
        },

        handleMenuClick(val) {
            switch (val.key) {
                case 'home':
                    this.$router.push({ path: '/' })
                    break
                case 'sqlananysis':
                    this.$router.push({ path: '/sqlananysis/sqls' })
                    break
                case 'tool':
                    this.$router.push({ path: '/tool/tasks' })
                    break
                default:
                    this.$router.push({ path: '/404' })
            }
        },
        reload() {
            this.isRouterAlive = false
            this.$nextTick(function () {
                this.isRouterAlive = true
            })
        },
    },
}
</script>

<style lang="less" scoped>
#components-layout-demo-top-side {
    .logo {
        line-height: 50px;
        margin: auto;
        float: left;
        font-size: 25px;
    }
    .top-menu {
        line-height: 50px;
        position: absolute;
        left: 250px;
    }
    .top-right {
        line-height: 50px;
        position: absolute;
        right: 30px;
        .top-user {
            font-size: 20px;
            vertical-align: middle;
        }
        .top-login {
            font-size: 14px;
        }
    }
}
</style>
