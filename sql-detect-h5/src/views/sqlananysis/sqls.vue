<template>
    <div class="view-box">
        <a-breadcrumb>
            <a-breadcrumb-item>
                <router-link :to="`/`">
                    <a-icon type="home" />
                </router-link>
            </a-breadcrumb-item>
            <a-breadcrumb-item href>
                <router-link :to="`sqls`">
                    <span>慢SQL分析</span>
                </router-link>
            </a-breadcrumb-item>
            <a-breadcrumb-item>
                <span>SQL分析</span>
            </a-breadcrumb-item>
        </a-breadcrumb>
        <div class="content-style">
            <div v-if="name === ''">
                <a-input-search v-model="searchName" placeholder="搜索应用服务，按回车键快捷查询" style="width: 300px; margin-left: 8px" @search="onSearch" @change="onSearchChange" />
                <a-tabs style="margin-top: 20px" :default-active-key="tabKey" @change="tabChange">
                    <a-tab-pane key="star">
                        <span slot="tab">
                            <a-icon type="star" />
                            我的应用（{{ this.starAppTotal }}）
                        </span>
                    </a-tab-pane>
                    <a-tab-pane key="all">
                        <span slot="tab">
                            <a-icon type="more" />
                            全部应用（{{ this.appTotal }}）
                        </span>
                    </a-tab-pane>
                </a-tabs>
                <div style="display: flex; flex-flow: wrap; gap: 20px; margin-top: 18px">
                    <div v-for="(appInfo, index) in appList" :key="index">
                        <a-card style="width: 400px; background-color: rgba(248, 248, 248, 0.6)">
                            <a slot="extra" @click="goToDetail(appInfo.serviceName)">详情</a>
                            <span slot="title">
                                <span v-if="tabKey === 'star'">
                                    <a-tooltip placement="top" title="取消关注"><a-icon @click="handleUnStar(appInfo.serviceName)" type="star" theme="filled" style="font-size: 17px" /></a-tooltip>
                                </span>
                                <span v-else>
                                    <a-tooltip placement="top" title="新增关注"><a-icon @click="handleStar(appInfo.serviceName)" type="star" theme="outlined" style="font-size: 17px" /></a-tooltip>
                                </span>
                                <b style="margin-left: 10px">{{ appInfo.serviceName.toUpperCase() }}</b>
                            </span>
                            <div style="float: left; width: 50%; height: 100%">
                                <a-statistic title="健康度" value="差" :value-style="{ color: '#ed1941' }" style="margin-right: 40px" v-if="'poor' === appInfo.health">
                                    <template #prefix>
                                        <a-icon type="frown" />
                                    </template>
                                </a-statistic>
                                <a-statistic title="健康度" value="中" :value-style="{ color: '#f58220' }" style="margin-right: 40px" v-else-if="'average' === appInfo.health">
                                    <template #prefix>
                                        <a-icon type="frown" />
                                    </template>
                                </a-statistic>
                                <a-statistic title="健康度" value="良" :value-style="{ color: '#00ae9d' }" style="margin-right: 40px" v-else-if="'good' === appInfo.health">
                                    <template #prefix>
                                        <a-icon type="meh" />
                                    </template>
                                </a-statistic>
                                <a-statistic title="健康度" value="秀" :value-style="{ color: '#7fb80e' }" style="margin-right: 40px" v-else-if="'excellent' === appInfo.health">
                                    <template #prefix>
                                        <a-icon type="smile" />
                                    </template>
                                </a-statistic>
                                <a-statistic title="健康度" value="秀" :value-style="{ color: '#7fb80e' }" style="margin-right: 40px" v-else>
                                    <template #prefix>
                                        <a-icon type="smile" />
                                    </template>
                                </a-statistic>
                                <a-statistic title="慢查占比" :value="appInfo.slowPercent" style="margin-top: 20px">
                                    <template #suffix>
                                        <span> / 100</span>
                                    </template>
                                </a-statistic>
                            </div>
                            <div style="float: right; width: 50%; height: 100%">
                                <a-statistic title="近7日新增慢查SQL数" :value="appInfo.latestSlowInSeven"></a-statistic>
                                <div style="display: flex; margin-top: 20px">
                                    <a-statistic title="慢查总数" :value="appInfo.slowTotal"></a-statistic>
                                    <a-statistic title="分析总数" :value="appInfo.explainTotal" style="margin-left: 20px"></a-statistic>
                                </div>
                            </div>
                        </a-card>
                    </div>
                </div>
            </div>
            <div v-else>
                <sqls-detail />
            </div>
        </div>
    </div>
</template>

<script>
import { overall, star, unstar } from '@/requests/sqlexplain'
import sqlsDetail from '@/views/sqlananysis/sqlsDetail'
import { mapState } from 'vuex'

export default {
    name: 'sqls',
    inject: ['reload'],
    components: {
        'sqls-detail': sqlsDetail,
    },
    data() {
        return {
            searchName: undefined,
            appTotal: 0,
            starAppTotal: 0,
            appListBackup: [],
            appList: [],
            name: '',
            tabKey: 'star',
        }
    },
    watch: {
        $route(to, from) {
            this.reload()
        },

        loginUser: {
            handler(newVal, oldVal) {
                console.log(newVal)
            },
            deep: true,
        },
    },

    mounted() {
        this.name = this.$route.query.name || ''
        if (this.name === '') {
            this.getOverAllStatistics()
        }
    },
    computed: {
        ...mapState({
            loginUser: state => state.loginUser,
        }),
    },
    methods: {
        handleStar(val) {
            star({ serviceName: val, jobNumber: this.loginUser.jobNumber }).then(res => {
                if (res.result === 1) {
                    this.$message.success(res.data)
                    this.getOverAllStatistics()
                } else {
                    this.$message.error(res.data)
                }
            })
        },
        handleUnStar(val) {
            unstar({ serviceName: val, jobNumber: this.loginUser.jobNumber }).then(res => {
                if (res.result === 1) {
                    this.$message.success(res.data)
                    this.getOverAllStatistics()
                } else {
                    this.$message.error(res.data)
                }
            })
        },
        tabChange(val) {
            this.tabKey = val
            this.getOverAllStatistics()
        },
        getOverAllStatistics() {
            overall({ jobNumber: this.loginUser.jobNumber, tabKey: this.tabKey }).then(res => {
                if (res.result === 1) {
                    this.appTotal = res.data.count
                    this.starAppTotal = res.data.starCount
                    this.appList = res.data.list
                }
            })
        },
        onSearch() {
            let searchRes = this.appList.filter(ele => ele.name.toLowerCase().includes(this.searchName.toLowerCase()))
            this.appListBackup = this.appList
            this.appList = searchRes
        },
        onSearchChange() {
            this.searchName === '' ? (this.appList = this.appListBackup) : null
        },
        goToDetail(val) {
            this.$router.push(`/sqlananysis/sqls?name=${val}`)
        },
    },
}
</script>

<style scoped></style>
