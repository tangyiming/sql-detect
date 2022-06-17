<template>
    <div class="exception">
        <div class="content">
            <div class="desc">抱歉，您访问的页面不存在</div>
            <div class="actions"></div>
        </div>
        <div class="imgBlock">
            <div class="imgEle" :style="{ backgroundImage: `url(https://gw.alipayobjects.com/zos/rmsportal/KpnpchXsobRgLElEozzI.svg)` }"></div>
        </div>
    </div>
</template>

<style lang="less" scoped>
@import '~ant-design-vue/lib/style/index';

.exception {
    display: flex;
    align-items: center;
    height: 100%;
    min-height: 666px;

    .imgBlock {
        flex: 0 0 62.5%;
        width: 62.5%;
        padding-right: 152px;
        zoom: 1;
        &::before,
        &::after {
            content: ' ';
            display: table;
        }
        &::after {
            clear: both;
            height: 0;
            font-size: 0;
            visibility: hidden;
        }
    }

    .imgEle {
        float: right;
        width: 100%;
        max-width: 430px;
        height: 360px;
        background-repeat: no-repeat;
        background-position: 50% 50%;
        background-size: contain;
    }

    .content {
        flex: auto;
        margin-left: 200px;

        .desc {
            margin-bottom: 16px;
            color: @text-color-secondary;
            font-size: 20px;
            line-height: 28px;
        }

        .actions {
            button:not(:last-child) {
                margin-right: 8px;
            }
        }
    }
}

@media screen and (max-width: @screen-xl) {
    .exception {
        .imgBlock {
            padding-right: 88px;
        }
    }
}

@media screen and (max-width: @screen-sm) {
    .exception {
        display: block;
        text-align: center;
        .imgBlock {
            margin: 0 auto 24px;
            padding-right: 0;
        }
    }
}

@media screen and (max-width: @screen-xs) {
    .exception {
        .imgBlock {
            margin-bottom: -24px;
            overflow: hidden;
        }
    }
}
</style>
