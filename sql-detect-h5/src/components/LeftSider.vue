<template>
    <a-menu @click="handleClick" :defaultSelectedKeys="sider" :openKeys.sync="openkey" :forceSubMenuRender="false" theme="dark" mode="inline" style="height: 100%">
        <template v-for="item in menuList">
            <a-menu-item v-if="!item.children" :key="item.key">
                <a-icon :type="item.icon" />
                <span>{{ item.title }}</span>
            </a-menu-item>
            <sub-menu v-else :menu-info="item" :key="item.key" />
        </template>
    </a-menu>
</template>

<script>
import SubMenu from './SubMenu'
import { mapState, mapActions } from 'vuex'

export default {
    name: 'LeftSider',
    components: {
        'sub-menu': SubMenu,
    },
    data() {
        return {
            sider: [''],
            openkey: [''],
        }
    },
    beforeMount() {
        this.sider[0] = this.activeSider
        this.openkey[0] = this.openKey
    },
    computed: {
        ...mapState({
            activeHeader: state => state.activeHeader,
            activeSider: state => state.activeSider,
            openKey: state => state.openKey,
        }),
    },
    props: {
        menuList: { required: true, type: Array },
    },
    methods: {
        ...mapActions(['setActiveHeader', 'setActiveSider', 'setOpenKey']),

        handleClick(val) {
            //
            this.$router.push({ path: val.key })
        },
    },
}
</script>
