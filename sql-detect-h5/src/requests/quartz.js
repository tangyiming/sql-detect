import http from '../utils/http'

export const listTasks = p => http.get('/quartz/list', p)
export const startTask = p => http.post('/quartz/create', p)
export const pauseTask = p => http.post('/quartz/pause', p)
export const runOnceTask = p => http.post('/quartz/runonce', p)
export const resumeTask = p => http.post('/quartz/resume', p)
export const updateTask = p => http.post('/quartz/update', p)
export const deleteTask = p => http.post('/quartz/delete', p)
