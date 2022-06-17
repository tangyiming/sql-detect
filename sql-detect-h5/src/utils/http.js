import axios from 'axios'

function checkCode(response = {}) {
    if (response.status === 200 || response.data.code === 200) {
        return response.data
    } else {
        if (response.message || response.data.message) {
            console.log('warning')
        }
        return {}
    }
}

axios.defaults.withCredentials = true

// 此处全局设置api 用于请求test-platform-app的服务器端接口转发，两边不加斜杠是坑，大坑，最后生成的地址莫名其妙
// axios.defaults.baseURL = '/api/'
axios.defaults.baseURL = process.env.VUE_APP_API_BASE_URL

axios.interceptors.request.use(
    config => {
        return config
    },
    error => Promise.reject(error)
)
axios.interceptors.response.use(
    response => checkCode(response),
    err => {
        if (err.response.status === 401) {
            //todo
            return
        }
        return Promise.reject(err)
    }
)

export default {
    post(url, data) {
        return axios({
            method: 'post',
            url,
            data: data,
        })
    },
    postFormData(url, data) {
        let ret = ''
        for (let it in data) {
            ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
        }
        return axios({
            method: 'post',
            headers: {
                'Content-type': 'application/x-www-form-urlencoded',
            },
            url,
            data: ret,
        })
    },
    postMultipartFormData(url, data) {
        return axios({
            method: 'post',
            headers: {
                'Content-type': 'multipart/form-data',
            },
            url,
            data: data,
        })
    },
    postMultipartFormDataAndDownloadNew(url, data) {
        return axios({
            method: 'post',
            headers: {
                'Content-type': 'multipart/form-data',
            },
            url,
            data: data,
            responseType: 'blob',
        })
    },
    put(url, data) {
        return axios({
            method: 'put',
            url,
            data: data,
        })
    },
    get(url, params) {
        return axios({
            method: 'get',
            url,
            params,
        })
    },
    delete(url, params) {
        return axios({
            method: 'delete',
            url,
            data: params,
        })
    },
}
