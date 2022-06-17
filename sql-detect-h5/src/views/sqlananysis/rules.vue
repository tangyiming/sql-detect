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
                <span>规则配置</span>
            </a-breadcrumb-item>
        </a-breadcrumb>
        <div class="content-style">
            <a-button type="primary" @click="addRule"  >添加全局配置规则</a-button>
            <a-table :columns="columns" :data-source="rules" style="margin-top: 5px">
                <template slot="action" slot-scope="text, record">
                    <a-button @click="editRule(record)" type="primary" size="small"  >编辑 </a-button>
                    <a-popconfirm title="你确定要删除吗？" @confirm="() => deleteTheRule(record)">
                        <a-button type="danger" style="margin-left: 2px" size="small"  >删除 </a-button>
                    </a-popconfirm>
                </template>
            </a-table>

            <a-button type="primary" @click="addScriptRule"  >添加忽略慢查规则</a-button>
            <a-table :columns="scriptColumns" :data-source="scriptRules" style="margin-top: 5px">
                <template slot="action" slot-scope="text, record">
                    <a-button @click="editScriptRule(record)" type="primary" size="small"  >编辑 </a-button>
                    <a-popconfirm title="你确定要删除吗？" @confirm="() => deleteTheScriptRule(record)">
                        <a-button type="danger" style="margin-left: 2px" size="small"  >删除 </a-button>
                    </a-popconfirm>
                </template>
            </a-table>
        </div>
        <a-modal :title="title" :visible="visible" @ok="handleOk" @cancel="handleCancel">
            <div v-if="type === 'json'">
                <a-form-model :model="modelForm" ref="form">
                    <a-form-model-item label="规则Key" ref="ruleKey" prop="ruleKey">
                        <a-input v-model="modelForm.ruleKey" placeholder="请输入规则Key" :disabled="noEdit" />
                    </a-form-model-item>
                    <a-form-model-item label="规则名称" ref="ruleName" prop="ruleName">
                        <a-input v-model="modelForm.ruleName" placeholder="请输入规则名称" />
                    </a-form-model-item>
                    <a-form-model-item label="规则体" ref="ruleDetail" prop="ruleDetail">
                        <a-input v-model="modelForm.ruleDetail" placeholder="请输入详细规则" @blur="checkFormat" />
                    </a-form-model-item>
                </a-form-model>
                <span> Mysql版本（5.7.22）支持Explain语句类型：</span><br />
                <span><b>SELECT / UPDATE / INSERT / DELETE / REPLACE</b></span>
            </div>
            <div v-else>
                <a-form-model :model="modelFormScript" ref="formScript">
                    <a-form-model-item label="规则脚本" ref="script" prop="script">
                        <a-textarea v-model="modelFormScript.script" placeholder="规则脚本" />
                    </a-form-model-item>
                    <a-form-model-item label="规则备注" ref="remark" prop="remark">
                        <a-textarea v-model="modelFormScript.remark" placeholder="规则备注" />
                    </a-form-model-item>
                </a-form-model>
            </div>
        </a-modal>
    </div>
</template>
<script>
import { getAllRules, updateRule, createRule, deleteRule, getAllScriptRules, updateScriptRule, createScriptRule, deleteScriptRule } from '@/requests/sqlexplain'
import { mapState } from 'vuex'

const columns = [
    {
        title: '规则Key',
        key: 'ruleKey',
        dataIndex: 'ruleKey',
    },
    {
        title: '规则名',
        key: 'ruleName',
        dataIndex: 'ruleName',
    },
    {
        title: '规则体',
        key: 'ruleDetail',
        dataIndex: 'ruleDetail',
    },
    {
        title: '操作',
        key: 'action',
        scopedSlots: { customRender: 'action' },
    },
]

const scriptColumns = [
    {
        title: '规则脚本',
        key: 'script',
        dataIndex: 'script',
    },
    {
        title: '规则备注',
        key: 'remark',
        dataIndex: 'remark',
    },
    {
        title: '操作',
        key: 'action',
        width: '140px',
        scopedSlots: { customRender: 'action' },
    },
]
export default {
    name: 'rules',
    data() {
        return {
            columns,
            scriptColumns,
            rules: [],
            scriptRules: [],
            type: 'json', //json script
            title: '编辑更新',
            visible: false,
            noEdit: true,
            modelForm: {
                ruleKey: undefined,
                ruleName: undefined,
                ruleDetail: undefined,
            },
            modelFormScript: {
                script: undefined,
                remark: undefined,
            },
        }
    },
    mounted() {
        this.getRulesList()
        this.getScriptRulesList()
    },
    computed: {
        ...mapState({
            loginUser: state => state.loginUser,
        }),
    },
    methods: {
        getRulesList() {
            getAllRules().then(res => {
                if (res.result === 1) {
                    this.rules = res.data
                }
            })
        },
        getScriptRulesList() {
            getAllScriptRules().then(res => {
                if (res.result === 1) {
                    this.scriptRules = res.data
                }
            })
        },
        isEnabled() {
            return this.loginUser.id !== 1020215
        },
        addRule() {
            this.type = 'json'
            this.title = '添加规则'
            this.noEdit = false
            this.visible = true
            this.modelForm = {
                ruleKey: undefined,
                ruleName: undefined,
                ruleDetail: undefined,
            }
        },
        addScriptRule() {
            this.type = 'script'
            this.title = '添加规则'
            this.noEdit = false
            this.visible = true
            this.modelFormScript = {
                script: undefined,
                remark: undefined,
            }
        },
        editRule(val) {
            this.type = 'json'
            this.title = '编辑更新'
            this.noEdit = true
            this.visible = true
            this.modelForm = val
        },
        editScriptRule(val) {
            this.type = 'script'
            this.noEdit = true
            this.title = '编辑更新'
            this.visible = true
            this.modelFormScript = val
        },
        handleCancel() {
            this.visible = false
        },
        handleOk() {
            console.log(this.type)
            if (this.type === 'json') {
                if (this.noEdit === true) {
                    try {
                        JSON.parse(this.modelForm.ruleDetail)
                    } catch (e) {
                        this.$message.error('规则体JSON数据格式有误：' + e.message)
                        return
                    }

                    updateRule(this.modelForm).then(res => {
                        if (res.result === 1) {
                            this.$message.success(res.data)
                            this.getRulesList()
                            this.visible = false
                        }
                    })
                } else {
                    try {
                        JSON.parse(this.modelForm.ruleDetail)
                    } catch (e) {
                        this.$message.error('规则体JSON数据格式有误：' + e.message)
                        return
                    }

                    createRule(this.modelForm).then(res => {
                        if (res.result === 1) {
                            this.$message.success(res.data)
                            this.getRulesList()
                            this.visible = false
                        }
                    })
                }
            } else {
                if (this.noEdit === true) {
                    updateScriptRule(this.modelFormScript).then(res => {
                        if (res.result === 1) {
                            this.$message.success(res.data)
                            this.getScriptRulesList()
                            this.visible = false
                        }
                    })
                } else {
                    createScriptRule(this.modelFormScript).then(res => {
                        if (res.result === 1) {
                            this.$message.success(res.data)
                            this.getScriptRulesList()
                            this.visible = false
                        }
                    })
                }
            }
        },
        deleteTheRule(val) {
            deleteRule({ id: val.id }).then(res => {
                if (res.result === 1) {
                    this.$message.success(res.data)
                    this.getRulesList()
                }
            })
        },
        deleteTheScriptRule(val) {
            deleteScriptRule({ id: val.id }).then(res => {
                if (res.result === 1) {
                    this.$message.success(res.data)
                    this.getScriptRulesList()
                }
            })
        },
        checkFormat() {
            try {
                JSON.parse(this.modelForm.ruleDetail)
            } catch (e) {
                this.$message.error('规则体JSON数据格式有误：' + e.message)
            }
        },
    },
}
</script>

<style scoped></style>
