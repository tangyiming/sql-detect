<template>
    <a-layout>
        <a-layout-content class="layout-style-with-height">
            <div class="content-style">
                <br />
                <h1>如果你喜欢此项目的话，请到Github项目页给个Star～</h1>
            </div>
        </a-layout-content>
        <my-footer />
    </a-layout>
</template>

<script>
// @ is an alias to /src
import Footer from '@/components/Footer'
import 'echarts/theme/macarons'
export default {
    components: {
        'my-footer': Footer,
    },
    name: 'home'
}
</script>
