import http from '../utils/http'

export const query = p => http.post('/sql/tables/query', p)

export const queryConfig = p => http.post('/sql/config/service/query', p)
export const addConfig = p => http.post('/sql/config/service/add', p)
export const updateConfig = p => http.post('/sql/config/service/update', p)
export const deleteConfig = p => http.get('/sql/config/service/delete', p)

export const getAllRules = p => http.get('/sql/config/rules/list', p)
export const updateRule = p => http.post('/sql/config/rules/update', p)
export const createRule = p => http.post('/sql/config/rules/create', p)
export const deleteRule = p => http.get('/sql/config/rules/delete', p)

export const getAllScriptRules = p => http.get('/sql/config/rules/script/list', p)
export const updateScriptRule = p => http.post('/sql/config/rules/script/update', p)
export const createScriptRule = p => http.post('/sql/config/rules/script/create', p)
export const deleteScriptRule = p => http.get('/sql/config/rules/script/delete', p)

export const overall = p => http.get('sql/statistics/overall', p)
export const serviceDetailOverall = p => http.get('sql/statistics/detail/overall', p)
export const serviceDetailList = p => http.post('sql/statistics/detail/query', p)

export const addProcessLog = p => http.post('sql/process/log/add', p)
export const queryProcessLog = p => http.get('sql/process/log/query', p)

export const star = p => http.post('/sql/stars/add', p)
export const unstar = p => http.post('/sql/stars/delete', p)
