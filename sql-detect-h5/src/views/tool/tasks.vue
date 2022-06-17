<template>
    <div class="view-box">
        <a-breadcrumb>
            <a-breadcrumb-item>
                <router-link :to="`/`">
                    <a-icon type="home" />
                </router-link>
            </a-breadcrumb-item>
            <a-breadcrumb-item href>
                <router-link :to="`tasks`">
                    <span>小工具</span>
                </router-link>
            </a-breadcrumb-item>
            <a-breadcrumb-item>
                <span>定时任务</span>
            </a-breadcrumb-item>
        </a-breadcrumb>
        <div class="content-style">
            <a-button type="primary" @click="addTask">添加定时任务</a-button>
            <a-table :columns="columns" :data-source="taskList" style="margin-top: 5px">
                <template slot="runAfterStart" slot-scope="text, record">
                    <a-tag color="green" v-if="record.runAfterStart">是</a-tag>
                    <a-tag color="red" v-else>否</a-tag>
                </template>
                <template slot="status" slot-scope="text, record">
                    <a-tag v-if="record.status === 'NONE'">NONE</a-tag>
                    <a-tag color="#87d068" v-else-if="record.status === 'NORMAL'">NORMAL</a-tag>
                    <a-tag color="#f50" v-else-if="record.status === 'PAUSED'">PAUSED</a-tag>
                    <a-tag color="108ee9" v-else-if="record.status === 'COMPLETE'">COMPLETE</a-tag>
                    <a-tag color="black" v-else-if="record.status === 'BLOCKED'">BLOCKED</a-tag>
                    <a-tag color="red" v-else>ERROR</a-tag>
                </template>
                <template slot="action" slot-scope="text, record">
                    <div>
                        <a-button @click="startTask(record)" type="primary" size="small">开启</a-button>
                        <a-button @click="runOnlyOnce(record)" type="primary" style="margin-left: 2px" size="small">执行一次</a-button>
                        <a-button @click="pauseTheTask(record)" type="primary" style="margin-left: 2px" size="small">暂挂</a-button>
                        <a-button @click="resumeTheTask(record)" type="primary" style="margin-left: 2px" size="small">恢复</a-button>
                    </div>
                    <div style="margin-top: 3px">
                        <a-button @click="editTask(record)" size="small">编辑</a-button>
                        <a-popconfirm title="你确定要删除吗？" @confirm="() => deleteTheTask(record)">
                            <a-button type="danger" style="margin-left: 2px" size="small">删除</a-button>
                        </a-popconfirm>
                    </div>
                </template>
            </a-table>
        </div>
        <a-modal :title="title" :visible="visible" @ok="handleOk" @cancel="handleCancel">
            <a-form-model :model="modelForm" ref="form">
                <a-form-model-item label="定时任务名称" ref="jobName" prop="jobName">
                    <a-input v-model="modelForm.jobName" placeholder="请输入定时任务名称" :disabled="noEdit" />
                </a-form-model-item>
                <a-form-model-item label="定时任务类" ref="jobClass" prop="jobClass">
                    <a-input v-model="modelForm.jobClass" placeholder="请输入定时任务类" />
                </a-form-model-item>
                <a-form-model-item label="Crontab" ref="cronExpression" prop="cronExpression">
                    <a-input v-model="modelForm.cronExpression" placeholder="请输入Crontab命令表达式" />
                </a-form-model-item>
                <a-form-model-item label="应用启动立即执行">
                    <a-switch v-model="modelForm.runAfterStart" />
                </a-form-model-item>
            </a-form-model>
        </a-modal>
    </div>
</template>
<script>
import { listTasks, startTask, pauseTask, runOnceTask, resumeTask, updateTask, deleteTask } from '@/requests/quartz'
import { mapState } from 'vuex'

const columns = [
    {
        title: '定时任务名称',
        key: 'jobName',
        dataIndex: 'jobName',
    },
    {
        title: '任务类',
        key: 'jobClass',
        dataIndex: 'jobClass',
    },
    {
        title: 'Crontab',
        key: 'cronExpression',
        dataIndex: 'cronExpression',
        width: '210px',
    },
    {
        title: '应用启动立即执行',
        key: 'runAfterStart',
        scopedSlots: { customRender: 'runAfterStart' },
        width: '100px',
    },
    {
        title: '执行状态',
        key: 'status',
        scopedSlots: { customRender: 'status' },
    },
    {
        title: '操作',
        key: 'action',
        scopedSlots: { customRender: 'action' },
    },
]

export default {
    name: 'tasks',
    data() {
        return {
            columns,
            taskList: [],
            title: '编辑更新',
            visible: false,
            noEdit: true,
            modelForm: {
                jobName: undefined,
                jobClass: undefined,
                cronExpression: undefined,
                runAfterStart: false,
            },
        }
    },
    mounted() {
        this.getAllTasks()
    },
    computed: {
        ...mapState({
            loginUser: state => state.loginUser,
        }),
    },
    methods: {
        getAllTasks() {
            listTasks().then(res => {
                res.result === 1 ? (this.taskList = res.data) : null
            })
        },
        startTask(val) {
            let p = {
                jobName: val.jobName,
                jobClass: val.jobClass,
                cronExpression: val.cronExpression,
            }
            startTask(p).then(res => {
                if (res.result === 1) {
                    this.$message.success('已成功执行')
                    this.getAllTasks()
                } else {
                    this.$message.error(res.data)
                }
            })
        },
        runOnlyOnce(val) {
            let p = {
                jobName: val.jobName,
            }
            runOnceTask(p).then(res => {
                if (res.result === 1) {
                    this.$message.success(res.data)
                    this.getAllTasks()
                } else {
                    this.$message.error(res.data)
                }
            })
        },
        pauseTheTask(val) {
            let p = {
                jobName: val.jobName,
            }
            pauseTask(p).then(res => {
                if (res.result === 1) {
                    this.$message.success(res.data)
                    this.getAllTasks()
                } else {
                    this.$message.error(res.data)
                }
            })
        },
        resumeTheTask(val) {
            let p = {
                jobName: val.jobName,
            }
            resumeTask(p).then(res => {
                if (res.result === 1) {
                    this.$message.success(res.data)
                    this.getAllTasks()
                } else {
                    this.$message.error(res.data)
                }
            })
        },
        addTask() {
            this.title = '添加定时任务'
            this.noEdit = false
            this.visible = true
            this.modelForm = {
                jobName: undefined,
                jobClass: undefined,
                cronExpression: undefined,
                runAfterStart: false,
            }
        },
        editTask(val) {
            this.title = '编辑更新'
            this.noEdit = true
            this.visible = true
            this.modelForm = val
        },
        deleteTheTask(val) {
            deleteTask(val).then(res => {
                if (res.result === 1) {
                    this.$message.success(res.data)
                    this.getAllTasks()
                } else {
                    this.$message.error(res.data)
                }
            })
        },
        handleOk() {
            if (this.noEdit === true) {
                updateTask(this.modelForm).then(res => {
                    if (res.result === 1) {
                        this.$message.success(res.data)
                        this.getAllTasks()
                        this.visible = false
                    } else {
                        this.$message.error(res.data)
                    }
                })
            } else {
                startTask(this.modelForm).then(res => {
                    if (res.result === 1) {
                        this.$message.success(res.data)
                        this.getAllTasks()
                        this.visible = false
                    } else {
                        this.$message.error(res.data)
                    }
                })
            }
        },
        handleCancel() {
            this.visible = false
            this.modelForm = {
                jobName: undefined,
                jobClass: undefined,
                cronExpression: undefined,
                runAfterStart: false,
            }
            this.getAllTasks()
        }
    },
}
</script>

<style scoped></style>
