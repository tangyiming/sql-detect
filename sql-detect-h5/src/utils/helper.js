/**
 * 防抖函数
 * @param {*} func
 * @param {*} delay
 * @param {*} immediate 是否立即执行
 */
export function debounce(func, delay, immediate) {
    // 定时任务的id
    let timeout
    return function () {
        let that = this
        let args = arguments
        if (timeout) clearTimeout(timeout)
        if (immediate) {
            var callNow = !timeout
            timeout = setTimeout(() => {
                timeout = null
            }, delay)
            if (callNow) func.apply(that, args)
        } else {
            timeout = setTimeout(function () {
                func.apply(that, args)
            }, delay)
        }
    }
}

