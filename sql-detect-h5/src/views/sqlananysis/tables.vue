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
                <span>表结构信息</span>
            </a-breadcrumb-item>
        </a-breadcrumb>
        <div class="content-style">
            <div class="ant-advanced-search-form">
                <a-form @submit="handleSearch">
                    <a-row :gutter="24">
                        <a-col :span="6">
                            <a-form-item label="数据库名">
                                <a-input v-model="queryDBName" placeholder="输入数据库名查询" allowClear />
                            </a-form-item>
                        </a-col>
                        <a-col :span="6">
                            <a-form-item label="表名">
                                <a-input v-model="queryTableName" placeholder="输入表名查询" allowClear />
                            </a-form-item>
                        </a-col>
                    </a-row>
                    <a-row>
                        <a-col>
                            <a-button :style="{ marginLeft: '8px' }" @click="handleSearch" type="primary"> 查询</a-button>
                            <a-button :style="{ marginLeft: '8px' }" @click="handleReset"> 清空</a-button>
                        </a-col>
                    </a-row>
                </a-form>
            </div>
            <a-table :columns="columns" :data-source="tableList" style="margin-top: 5px" :rowKey="tableData => tableData.id" :pagination="pagination" @change="handlePageChange">
                <template slot="createTime" slot-scope="text, record">
                    {{ formatTime(record.createTime) }}
                </template>
                <template slot="updateTime" slot-scope="text, record">
                    {{ formatTime(record.updateTime) }}
                </template>
            </a-table>
        </div>
    </div>
</template>

<script>
import { query } from '@/requests/sqlexplain'
import moment from 'moment'

const columns = [
    {
        title: '数据库名',
        key: 'dbName',
        dataIndex: 'dbName',
    },
    {
        title: '表名',
        key: 'tbName',
        dataIndex: 'tbName',
    },
    {
        title: '表结构指纹',
        key: 'fingerprint',
        dataIndex: 'fingerprint',
    },
    {
        title: '创建时间',
        key: 'createTime',
        scopedSlots: { customRender: 'createTime' },
    },
    {
        title: '更新时间',
        key: 'updateTime',
        scopedSlots: { customRender: 'updateTime' },
    },
]

export default {
    name: 'tables',
    data() {
        return {
            pagination: {
                current: 1,
                pageSize: 10,
                total: 0,
                showSizeChanger: true,
                pageSizeOptions: ['10', '20', '50', '100'],
                showTotal: total => `共有 ${total} 条数据`,
            },
            tableList: [],
            columns,
            queryDBName: undefined,
            queryTableName: undefined,
        }
    },
    mounted() {
        this.getTablesByPage({ pageNum: this.pagination.current, pageSize: this.pagination.pageSize })
    },
    methods: {
        getTablesByPage(val) {
            query(val).then(res => {
                if (res.result === 1) {
                    this.tableList = res.data.list
                    this.pagination.total = res.data.total
                } else {
                    this.$message.error(res.errorMsg)
                }
            })
        },
        formatTime(val) {
            return moment(val, 'YYYY-MM-DDTHH:mm').format('YYYY-MM-DD HH:mm')
        },
        handlePageChange(val) {
            this.pagination = { ...val }
            query({ pageNum: this.pagination.current, pageSize: this.pagination.pageSize }).then(res => {
                if (1 === res.result) {
                    this.tableList = res.data.list
                    this.pagination.total = res.data.total
                } else {
                    this.$message.error(res.errorMsg)
                }
            })
        },
        handleSearch() {
            query({
                pageNum: 1,
                pageSize: 10,
                sqlExplainTableFingerprint: { dbName: this.queryDBName, tbName: this.queryTableName },
            }).then(res => {
                if (1 === res.result) {
                    this.tableList = res.data.list
                    this.pagination.total = res.data.total
                } else {
                    this.$message.error(res.errorMsg)
                }
            })
        },
        handleReset() {
            this.queryDBName = undefined
            this.queryTableName = undefined
        },
    },
}
</script>

<style scoped></style>
