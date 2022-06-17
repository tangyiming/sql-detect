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
                <span>应用配置</span>
            </a-breadcrumb-item>
        </a-breadcrumb>
        <div class="content-style">
            <a-form class="ant-advanced-search-form">
                <a-button type="primary" @click="addService"  >添加配置</a-button>

                <a-input-search v-model="queryServiceName" placeholder="查询应用服务，按回车键快捷查询" style="width: 300px; margin-left: 8px" @search="onSearch" />
            </a-form>
            <a-table :columns="columns" :data-source="serviceList" style="margin-top: 5px" :rowKey="tableData => tableData.id" :pagination="pagination" @change="handlePageChange">
                <template slot="explainSwitch" slot-scope="text, record">
                    <a-switch v-model="record.explainSwitch" @change="val => handleSwitchChange(record, val)" />
                </template>
                <template slot="action" slot-scope="text, record">
                    <a-button @click="editServiceConfig(record)" type="primary" size="small"> 编辑 </a-button>
                    <a-popconfirm title="你确定要删除吗？" @confirm="() => deleteServiceConfig(record)">
                        <a-button type="danger" style="margin-left: 2px" size="small"  >删除 </a-button>
                    </a-popconfirm>
                </template>
            </a-table>
        </div>
        <a-modal :title="title" :visible="visible" @ok="handleOk" @cancel="handleCancel" width="800px">
            <a-form-model :model="modelForm" ref="form">
                <a-form-model-item label="应用名" ref="service" prop="service">
                    <a-input v-model="modelForm.service" placeholder="请输入应用名" :disabled="noEdit" />
                </a-form-model-item>
                <a-form-model-item label="分析开关" ref="explainSwitch" prop="explainSwitch">
                    <a-switch v-model="modelForm.explainSwitch" />
                </a-form-model-item>
                <a-form-model-item label="负责人" ref="users" prop="users">
                    <a-input v-model="modelForm.users" placeholder="请输入负责人手机号,格式：[{'name':'','tel':''}]" />
                </a-form-model-item>
                <a-form-model-item label="钉钉Token" ref="dingToken" prop="dingToken">
                    <a-tooltip placement="top" title="留空则发送到默认预警群，否则发送到填写Token的机器人所属指定群"
                        ><a-input v-model="modelForm.dingToken" placeholder="请输入通知群机器人Token"
                    /></a-tooltip>
                </a-form-model-item>
            </a-form-model>
        </a-modal>
    </div>
</template>

<script>
import { addConfig, deleteConfig, queryConfig, updateConfig } from '@/requests/sqlexplain'
import { mapState } from 'vuex'

const columns = [
    {
        title: '应用',
        key: 'service',
        dataIndex: 'service',
    },
    {
        title: 'SQL上报开关',
        key: 'explainSwitch',
        scopedSlots: { customRender: 'explainSwitch' },
    },
    {
        title: '负责人',
        key: 'users',
        dataIndex: 'users',
    },
    {
        title: '自定义钉群通知Token',
        key: 'dingToken',
        dataIndex: 'dingToken',
    },
    {
        title: '操作',
        key: 'action',
        scopedSlots: { customRender: 'action' },
    },
]

export default {
    name: 'services',
    data() {
        return {
            queryServiceName: undefined,
            pagination: {
                current: 1,
                pageSize: 10,
                total: 0,
                showSizeChanger: true,
                pageSizeOptions: ['10', '20', '50', '100'],
                showTotal: total => `共有 ${total} 条数据`,
            },
            serviceList: [],
            columns,
            title: '添加配置',
            visible: false,
            noEdit: true,
            modelForm: { service: undefined, explainSwitch: false, users: undefined, dingToken: undefined },
        }
    },
    mounted() {
        this.getServicesByPage({ pageNum: this.pagination.current, pageSize: this.pagination.pageSize })
    },
    computed: {
        ...mapState({
            loginUser: state => state.loginUser,
        }),
    },
    methods: {
        isEnabled() {
            return this.loginUser.id !== 1020215
        },
        handleSwitchChange(record, val) {
            record.explainSwitch = val
            updateConfig(record).then(res => {
                if (res.result === 1) {
                    this.$message.success(res.data)
                    this.getServicesByPage({ pageNum: this.pagination.current, pageSize: this.pagination.pageSize })
                    this.visible = false
                } else {
                    this.$message.error('更新失败')
                }
            })
        },
        handleOk() {
            if (this.noEdit === false) {
                addConfig(this.modelForm).then(res => {
                    if (res.result === 1) {
                        this.$message.success(res.data)
                        this.pagination.current = 1
                        this.pagination.pageSize = 10
                        this.getServicesByPage({ pageNum: 1, pageSize: 10 })
                        this.visible = false
                    } else {
                        this.$message.error('创建失败')
                    }
                })
            } else {
                updateConfig(this.modelForm).then(res => {
                    if (res.result === 1) {
                        this.$message.success(res.data)
                        this.getServicesByPage({ pageNum: this.pagination.current, pageSize: this.pagination.pageSize })
                        this.visible = false
                    } else {
                        this.$message.error('更新失败')
                    }
                })
            }
        },
        handleCancel() {
            this.visible = false
        },
        getServicesByPage(val) {
            queryConfig(val).then(res => {
                if (res.result === 1) {
                    this.serviceList = res.data.list
                    this.pagination.total = res.data.total
                } else {
                    this.$message.error(res.errorMsg)
                }
            })
        },
        addService() {
            this.title = '添加应用配置'
            this.noEdit = false
            this.visible = true
            this.modelForm = { service: undefined, explainSwitch: false, users: undefined, dingToken: undefined }
        },
        editServiceConfig(val) {
            this.title = '编辑更新应用配置'
            this.noEdit = true
            this.visible = true
            this.modelForm = val
        },
        deleteServiceConfig(val) {
            deleteConfig({ id: val.id }).then(res => {
                this.$message.success(res.data)
                this.getServicesByPage({ pageNum: 1, pageSize: 10 })
            })
        },
        handlePageChange(val) {
            this.pagination = { ...val }
            queryConfig({ pageNum: this.pagination.current, pageSize: this.pagination.pageSize }).then(res => {
                if (1 === res.result) {
                    this.serviceList = res.data.list
                    this.pagination.total = res.data.total
                } else {
                    this.$message.error(res.errorMsg)
                }
            })
        },
        onSearch() {
            queryConfig({ pageNum: 1, pageSize: 10, appConfigs: { service: this.queryServiceName } }).then(res => {
                if (1 === res.result) {
                    this.serviceList = res.data.list
                    this.pagination.total = res.data.total
                } else {
                    this.$message.error(res.errorMsg)
                }
            })
        },
    },
}
</script>
