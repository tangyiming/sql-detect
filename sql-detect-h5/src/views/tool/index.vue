<template>
    <a-layout>
        <a-layout-sider collapsible v-model="collapsed" class="sider">
            <left-sider :menuList="menuList" :key="siderKey" />
        </a-layout-sider>
        <a-layout>
            <a-layout-content class="layout-style">
                <router-view />
            </a-layout-content>
            <my-footer />
        </a-layout>
    </a-layout>
</template>
<script>
import { mapActions, mapState } from 'vuex'
import Footer from '@/components/Footer'

export default {
    components: {
        'my-footer': Footer,
    },
    data() {
        return {
            collapsed: false,
            menuList: [
                { key: 'tasks', path: '/tool/tasks', title: '定时任务', icon: 'sketch' }
            ],
        }
    },
    computed: {
        ...mapState({
            openKey: state => state.openKey,
            activeSider: state => state.activeSider,
            siderKey: state => state.siderKey,
        }),
    },
    watch: {
        openKey: function () {
            this.setSiderKey(this.siderKey + 1)
        },
    },
    beforeMount() {
        this.setMenuList(this.menuList)
    },
    methods: {
        ...mapActions(['setMenuList', 'setSiderKey']),
    },
}
</script>
