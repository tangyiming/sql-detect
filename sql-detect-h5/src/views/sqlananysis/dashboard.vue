<template>
    <div class="view-box">
        <a-breadcrumb>
            <a-breadcrumb-item>
                <router-link :to="`/`">
                    <a-icon type="home" />
                </router-link>
            </a-breadcrumb-item>
            <a-breadcrumb-item href>
                <router-link :to="`dashboard`">
                    <span>慢SQL分析</span>
                </router-link>
            </a-breadcrumb-item>
            <a-breadcrumb-item>
                <span>Dashboard</span>
            </a-breadcrumb-item>
        </a-breadcrumb>
        <div class="content-style"></div>
    </div>
</template>

<script>
export default {
    name: 'dashboard',
}
</script>

<style scoped></style>
