<template>
    <div>
        <!-- 展示应用基础信息 -->
        <div style="padding: 20px">
            <span style="font-size: 20px">
                <b>{{ this.$route.query.name.toUpperCase() }}</b>
            </span>
            <a-tooltip placement="bottom" title="慢查占比">
                <a-tag color="#108ee9" style="font-size: 17px; margin-left: 5px"> {{ this.overall.slowPercent }}%</a-tag>
            </a-tooltip>
            <a-tooltip placement="bottom" title="健康度">
                <a-tag color="#ed1941" style="font-size: 17px" v-if="'poor' === this.overall.health"> 差</a-tag>
                <a-tag color="#f58220" style="font-size: 17px" v-else-if="'average' === this.overall.health"> 中</a-tag>
                <a-tag color="#00ae9d" style="font-size: 17px" v-else-if="'good' === this.overall.health"> 良</a-tag>
                <a-tag color="#7fb80e" style="font-size: 17px" v-else-if="'excellent' === this.overall.health"> 秀 </a-tag>
                <a-tag color="#7fb80e" style="font-size: 17px" v-else> 秀</a-tag>
            </a-tooltip>
        </div>
        <div style="margin-left: 20px; font-size: 15px">
            <a-row :gutter="[8, 16]">
                <a-col :span="5">
                    <div><b>近7日新增慢查数：</b>{{ this.overall.latestSlowInSeven }}</div>
                    <div style="margin-top: 5px"><b>慢查总数：</b>{{ this.overall.slowTotal }}</div>
                </a-col>
                <a-col :span="5">
                    <div><b>分析总数：</b>{{ this.overall.explainTotal }}</div>
                    <div style="margin-top: 5px"><b>慢查启用状态：</b>{{ this.overall.explainSwitch ? '开启' : '关闭' }}</div>
                </a-col>
                <a-col :span="5">
                    <div>
                        <b>负责人：</b>
                        <a v-for="(usr, idx) in JSON.parse(this.overall.users || '[]')" :key="idx">
                            <a-tooltip placement="bottom" :title="usr.tel">{{ usr.name }}&nbsp;</a-tooltip>
                        </a>
                    </div>
                    <div style="margin-top: 5px">
                        <b>通知Token：</b>
                        <a-tooltip placement="bottom" :title="this.overall.dingToken"><a>查看</a></a-tooltip>
                    </div>
                </a-col>
            </a-row>
        </div>
        <a-divider />
        <a-form-model layout="inline" style="float: right">
            <a-form-model-item>
                <a-select v-model="querySlowType" style="width: 120px">
                    <a-select-option value="all"> 所有 </a-select-option>
                    <a-select-option value="isSlow"> 慢查 </a-select-option>
                </a-select>
            </a-form-model-item>
            <a-form-model-item>
                <a-range-picker :show-time="{ format: 'HH:mm' }" format="YYYY-MM-DD HH:mm" :placeholder="['起始时间', '终止时间']" v-model="selectTime" />
            </a-form-model-item>
            <a-form-model-item>
                <a-button type="primary" @click="queryList"> 查询 </a-button>
            </a-form-model-item>
        </a-form-model>
        <br /><br />
        <a-table :columns="columns" :data-source="tableData" :rowKey="data => data.id" :loading="loading" :pagination="pagination" @change="handlePageChange" size="middle">
            <template slot="id" slot-scope="text, record, index">
                {{ (pagination.current - 1) * pagination.pageSize + parseInt(index) + 1 }}
            </template>
            <template slot="sqlId" slot-scope="text, record">
                {{ !!record.sqlId?record.sqlId:"值为空，请开发联系唐一鸣" }}
            </template>
            <template slot="isSlow" slot-scope="text, record">
                <a-tag color="red" v-if="record.isSlow === true"> 慢查</a-tag>
                <a-tag color="green" v-else-if="record.isSlow === false"> 非慢查</a-tag>
                <a-tag color="grey" v-else> 待分析</a-tag>
            </template>
            <template slot="isProdFault" slot-scope="text, record">
                <a-tag color="red" v-if="record.isProdFault === true"> 是</a-tag>
                <a-tag v-else> -</a-tag>
            </template>
            <template slot="createTime" slot-scope="text, record">
                {{ formatTime(record.createTime) }}
            </template>
            <template slot="updateTime" slot-scope="text, record">
                {{ formatTime(record.updateTime) }}
            </template>
            <template slot="action" slot-scope="text, record">
                <a-button type="primary" size="small" @click="showProcess(record)">处理</a-button>
                <br />
                <a-button size="small" @click="showProcessLog(record)" style="margin-top: 8px">记录</a-button>
            </template>
            <template slot="expandedRowRender" slot-scope="record" class="ant-table">
                <a-descriptions bordered>
                    <a-descriptions-item label="ID">
                        {{ record.id }}
                    </a-descriptions-item>
                    <a-descriptions-item label="服务名">
                        {{ record.serviceName }}
                    </a-descriptions-item>
                    <!--                    <a-descriptions-item label="DB地址">-->
                    <!--                        {{ record.dbHost }}-->
                    <!--                    </a-descriptions-item>-->
                    <!--                    <a-descriptions-item label="DB端口">-->
                    <!--                        {{ record.dbPort }}-->
                    <!--                    </a-descriptions-item>-->
                    <a-descriptions-item label="DB名">
                        {{ record.dbName }}
                    </a-descriptions-item>
                    <!--                    <a-descriptions-item label="DB账号">-->
                    <!--                        {{ record.dbUser }}-->
                    <!--                    </a-descriptions-item>-->
                    <!--                    <a-descriptions-item label="DB密码">-->
                    <!--                        {{ record.dbPwd }}-->
                    <!--                    </a-descriptions-item>-->
                    <!--                    <a-descriptions-item label="DB版本">-->
                    <!--                        {{ record.serverVersion }}-->
                    <!--                    </a-descriptions-item>-->
                    <!--                    <a-descriptions-item label="关联表指纹ID">-->
                    <!--                        {{ record.tbFingerprints }}-->
                    <!--                    </a-descriptions-item>-->
                    <a-descriptions-item label="方法" :span="3">
                        {{ record.sqlId }}
                    </a-descriptions-item>
                    <a-descriptions-item label="原始SQL" :span="3">
                        {{ record.originalSql }}
                    </a-descriptions-item>
                    <a-descriptions-item label="参数化SQL" :span="3">
                        {{ record.parameterizedSql }}
                    </a-descriptions-item>
                    <a-descriptions-item label="Explain结果" :span="3">
                        {{ record.explainRes }}
                    </a-descriptions-item>
                    <a-descriptions-item label="是否慢查">
                        <span v-if="record.isSlow === true">是</span>
                        <span v-else-if="record.isSlow === false">否</span>
                        <span v-else>待分析</span>
                    </a-descriptions-item>

                    <!--                    <a-descriptions-item label="分析是否失败">-->
                    <!--                        {{ record.isFailed === 0 ? '-' : '是' }}-->
                    <!--                    </a-descriptions-item>-->
                    <a-descriptions-item label="是否已预警">
                        {{ record.isAlert ? '是' : '否' }}
                    </a-descriptions-item>
                    <a-descriptions-item label="线上慢查">
                        {{ record.isProdFault ? '是' : '-' }}
                    </a-descriptions-item>
                    <a-descriptions-item label="上报时间">
                        {{ record.createTime }}
                    </a-descriptions-item>
                    <a-descriptions-item label="更新时间">
                        {{ record.updateTime }}
                    </a-descriptions-item>
                </a-descriptions>
            </template>
        </a-table>
        <a-modal title="慢查SQL预警处理" :visible="visible" @ok="handleOk" @cancel="handleCancel" width="900px">
            <a-form-model :model="modelForm" ref="form" :rules="rules" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
                <a-form-model-item label="处理类型" required>
                    <a-radio-group v-model="modelForm.type">
                        <a-radio value="ignore"> 确认非慢查-忽略</a-radio>
                        <a-radio value="slow-optimized"> 确认慢查-已优化</a-radio>
                        <a-radio value="slow-todo"> 确认慢查-待处理</a-radio>
                    </a-radio-group>
                </a-form-model-item>
                <a-form-model-item label="处理说明" prop="remark" ref="remark" required>
                    <a-textarea v-model="modelForm.remark" placeholder="请输入你的处理说明" :auto-size="{ minRows: 3, maxRows: 5 }" />
                </a-form-model-item>
            </a-form-model>
            <span style="margin-left: 35px; color: #8d0002">注：处理提交后，将变更慢查标记结果</span>
        </a-modal>
        <a-modal title="处理历史记录" :visible="logVisible" @ok="logHandleOk" @cancel="logHandleCancel" width="900px">
            <a-table :columns="logColumns" :data-source="logList" :rowKey="data => data.id" :pagination="false">
                <template slot="id" slot-scope="text, record, index">
                    {{ (pagination.current - 1) * pagination.pageSize + parseInt(index) + 1 }}
                </template>
                <template slot="type" slot-scope="text, record">
                    <span v-if="record.type === 'ignore'">确认非慢查-忽略</span>
                    <span v-else-if="record.type === 'slow-optimized'">确认慢查-已优化</span>
                    <span v-else>确认慢查-待处理</span>
                </template>
                <template slot="usr" slot-scope="text, record">
                    <a-tooltip placement="bottom" :title="record.userId">{{ record.userName }}</a-tooltip>
                </template>
                <template slot="createTime" slot-scope="text, record">
                    {{ formatTime(record.createTime) }}
                </template>
            </a-table>
        </a-modal>
        <a-back-top :visibilityHeight="visibilityHeight" />
    </div>
</template>

<script>
import moment from 'moment'
import { mapState } from 'vuex'
import { addProcessLog, queryProcessLog, serviceDetailList, serviceDetailOverall } from '@/requests/sqlexplain'

const columns = [
    {
        scopedSlots: { customRender: 'id' },
    },
    {
        title: '方法',
        key: 'sqlId',
        scopedSlots: { customRender: 'sqlId' },
        width: '15%',
    },
    {
        title: '原始 SQL',
        key: 'originalSql',
        dataIndex: 'originalSql',
        width: '20%',
    },
    {
        title: 'Explain结果',
        key: 'explainRes',
        dataIndex: 'explainRes',
        width: '20%',
    },
    {
        title: '是否慢查',
        key: 'isSlow',
        scopedSlots: { customRender: 'isSlow' },
    },
    {
        title: '处理说明',
        key: 'remark',
        dataIndex: 'remark',
    },
    {
        title: '线上慢查',
        key: 'isProdFault',
        scopedSlots: { customRender: 'isProdFault' },
    },
    {
        title: '捕获时间',
        key: 'createTime',
        scopedSlots: { customRender: 'createTime' },
    },
    {
        title: '更新时间',
        key: 'updateTime',
        scopedSlots: { customRender: 'updateTime' },
    },
    {
        title: '操作',
        key: 'action',
        scopedSlots: { customRender: 'action' },
    },
]

const logColumns = [
    {
        scopedSlots: { customRender: 'id' },
    },
    {
        title: '处理类型',
        key: 'type',
        scopedSlots: { customRender: 'type' },
    },
    {
        title: '处理说明',
        key: 'remark',
        dataIndex: 'remark',
    },
    {
        title: '处理人',
        key: 'usr',
        scopedSlots: { customRender: 'usr' },
    },
    {
        title: '处理时间',
        key: 'createTime',
        scopedSlots: { customRender: 'createTime' },
    },
]

export default {
    name: 'sqlsDetail',
    data() {
        return {
            columns,
            logColumns,
            name: '',
            querySlowType: 'all',
            selectTime: [moment().subtract(7, 'days'), moment()],
            overall: {},
            loading: false,
            tableData: [],
            logList: [],
            pagination: {
                current: 1,
                pageSize: 10,
                total: 0,
                showSizeChanger: true,
                pageSizeOptions: ['10', '20', '50', '100'],
                showTotal: total => `共有 ${total} 条数据`,
            },
            visible: false,
            logVisible: false,
            modelForm: {
                explainInfoId: undefined,
                userId: undefined,
                userName: undefined,
                type: undefined,
                remark: undefined,
            },
            rules: {
                remark: [
                    { required: true, message: '必填项', trigger: 'blur' },
                    { min: 1, max: 100, message: '长度小于100', trigger: 'blur' },
                ],
            },
            visibilityHeight: 300,
        }
    },
    computed: {
        ...mapState({
            loginUser: state => state.loginUser,
        }),
    },
    mounted() {
        this.getServiceOverallInfo()
        this.name = this.$route.query.name
        let p = {
            pageNum: this.pagination.current,
            pageSize: this.pagination.pageSize,
            sqlExplainInfo: {
                serviceName: this.$route.query.name,
            },
            startTime: moment().subtract(7, 'days').format('YYYY-MM-DD HH:mm:ss'),
            endTime: moment().format('YYYY-MM-DD HH:mm:ss'),
        }
        this.querySqlInfoList(p)
    },
    methods: {
        queryList() {
            let slow = this.querySlowType === 'all' ? undefined : true
            let p = {
                pageNum: this.pagination.current,
                pageSize: this.pagination.pageSize,
                sqlExplainInfo: {
                    serviceName: this.name,
                    isSlow: slow,
                },
                startTime: moment(this.selectTime[0]).format('YYYY-MM-DD HH:mm:ss'),
                endTime: moment(this.selectTime[1]).format('YYYY-MM-DD HH:mm:ss'),
            }
            this.querySqlInfoList(p)
        },
        handlePageChange(val) {
            this.pagination = { ...val }
            let slow = this.querySlowType === 'all' ? undefined : true
            let p = {
                pageNum: this.pagination.current,
                pageSize: this.pagination.pageSize,
                sqlExplainInfo: {
                    serviceName: this.name,
                    isSlow: slow,
                },
                startTime: moment(this.selectTime[0]).format('YYYY-MM-DD HH:mm:ss'),
                endTime: moment(this.selectTime[1]).format('YYYY-MM-DD HH:mm:ss'),
            }
            this.querySqlInfoList(p)
        },
        logHandleOk() {
            this.logVisible = false
        },
        logHandleCancel() {
            this.logVisible = false
        },
        showProcessLog(val) {
            this.logVisible = true
            queryProcessLog({ infoId: val.id }).then(res => {
                if (res.result === 1) {
                    this.logList = res.data
                }
            })
        },
        showProcess(val) {
            this.visible = true
            this.modelForm = {
                explainInfoId: val.id,
                userId: this.loginUser.jobNumber,
                userName: this.loginUser.name,
                type: 'ignore',
                remark: undefined,
            }
        },
        handleOk() {
            this.$refs['form'].validate(valid => {
                if (valid) {
                    addProcessLog(this.modelForm).then(res => {
                        if (res.result === 1) {
                            this.$message.success('处理请求成功')
                            let slow = this.querySlowType === 'all' ? undefined : true
                            let p = {
                                pageNum: this.pagination.current,
                                pageSize: this.pagination.pageSize,
                                sqlExplainInfo: {
                                    serviceName: this.name,
                                    isSlow: slow,
                                },
                                startTime: moment(this.selectTime[0]).format('YYYY-MM-DD HH:mm:ss'),
                                endTime: moment(this.selectTime[1]).format('YYYY-MM-DD HH:mm:ss'),
                            }
                            this.querySqlInfoList(p)
                            this.visible = false
                        } else {
                            this.$message.error(res.data)
                        }
                    })
                } else {
                    return false
                }
            })
        },
        handleCancel() {
            this.visible = false
            this.$refs.form.resetFields()
            let slow = this.querySlowType === 'all' ? undefined : true
            let p = {
                pageNum: this.pagination.current,
                pageSize: this.pagination.pageSize,
                sqlExplainInfo: {
                    serviceName: this.name,
                    isSlow: slow,
                },
                startTime: moment(this.selectTime[0]).format('YYYY-MM-DD HH:mm:ss'),
                endTime: moment(this.selectTime[1]).format('YYYY-MM-DD HH:mm:ss'),
            }
            this.querySqlInfoList(p)
        },
        formatTime(val) {
            return moment(val, 'YYYY-MM-DDTHH:mm').format('YYYY-MM-DD HH:mm')
        },
        getServiceOverallInfo() {
            serviceDetailOverall({ app: this.$route.query.name }).then(res => {
                if (res.result === 1) {
                    this.overall = res.data
                } else {
                    this.$message.error(res.data)
                }
            })
        },
        querySqlInfoList(val) {
            this.loading = true
            serviceDetailList(val)
                .then(res => {
                    if (res.result === 1) {
                        this.tableData = res.data.list
                        this.pagination.total = res.data.total
                    }
                })
                .catch(err => {
                    this.$message.error(err)
                })
                .finally(() => {
                    this.loading = false
                })
        },
    },
}
</script>
<style lang="less" scoped>
/deep/ .ant-table {
    font-size: 12px;
}
</style>
