# test-platform-boss

## 技术栈

vue.js/vuex/ant-design-vue/axios/vue-test-utils/jest/prettier

## 特点

本骨架工程采用了顶部菜单加侧边菜单分别渲染的方式，为了可以直接通过复制网址打开对应页面且能正确高亮顶部与侧边菜单，本工程并未使用浏览器本地存储数据的方式，而是对 url 进行了解析，所以对于地址命名与项目文件命名有着一定的规范。请阅读骨架工程代码学习约定规约。

## 使用

-   请根据自己的需要修改登录认证与封装的 axios 请求拦截方法。

## 本地开发

devServer 下修改 host: 'web.amh-group.com'，本地 hosts 文件新增：机器 IP web.amh-group.com

## 部署

### 常规部署方式

拉取代码

```bash
[root@localhost conf.d]# npm install
[root@localhost conf.d]# npm run build
```

配置 nginx server 项

```bash
[root@localhost conf.d]# vi default.conf
```

> listen 8080;
>
> location / {
>
> root /opt/test-platform-h5/dist/;
>
> try_files $uri $uri/ /index.html = 404;
>
> }

```bash
[root@localhost conf.d]# nginx -c /etc/nginx/nginx.conf
[root@localhost conf.d]# nginx -s reload
```

关闭防火墙，以防止外面无法访问

```bash
[root@localhost conf.d]# systemctl stop firewalld.service
```

### Docker 化部署方式（暂不用这种方式部署）

在项目中添加 docker 与 nginx 配置
给服务器生成 sshkey 添加到 gitlab
在服务器上拉取前端代码
进行 image 构建（tag 随版本变更），和容器运行

```bash
[root@localhost opt]# docker build test-platform-h5/ -t test-platform-h5:1.0.0
[root@localhost opt]# docker run -d -p 8080:80 test-platform-h5:1.0.0
```

参考：https://cli.vuejs.org/guide/deployment.html#docker-nginx
