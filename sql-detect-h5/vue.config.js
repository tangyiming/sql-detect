module.exports = {
    configureWebpack: config => {
        config.module.rules.push({
            test: /\.md$/,
            use: [
                {
                    loader: 'html-loader',
                },
                {
                    loader: 'markdown-loader',
                    options: {},
                },
            ],
        })
    },
    css: {
        loaderOptions: {
            less: {
                modifyVars: {
                    'searchform-bgcolor': '#fbfbfb',
                    'primary-color': '#16817a',
                },
                javascriptEnabled: true,
            },
        },
    },
    devServer: {
        hot: true,
        // host: 'web.xxx.com',
        port: 8088,
        open: true,
        https: true,
        proxy: {
            '/detect': {
                //本地
                target: 'http://web.xxx.com:8080/detect/',
                //线上
                //target: 'http://tangram.xxx.com/detect/',
                ws: false,
                changOrigin: true,
                secure: true,
                pathRewrite: {
                    '^/detect/': '',
                },
            },
        },
    },
}
