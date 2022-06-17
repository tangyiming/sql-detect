package com.tangym.sql.server.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangym.sql.server.dto.request.SqlDetailListPageRequest;
import com.tangym.sql.server.dto.response.ApiResponse;
import com.tangym.sql.server.entity.SqlAppConfigs;
import com.tangym.sql.server.entity.SqlExplainInfo;
import com.tangym.sql.server.entity.SqlExplainStatistics;
import com.tangym.sql.server.mapper.SqlAppConfigsMapper;
import com.tangym.sql.server.mapper.SqlExplainInfoMapper;
import com.tangym.sql.server.mapper.SqlExplainStarsMapper;
import com.tangym.sql.server.mapper.SqlExplainStatisticsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/sql/statistics")
public class SqlStatisticsController {
    @Resource
    private SqlAppConfigsMapper sqlAppConfigsMapper;
    @Resource
    private SqlExplainInfoMapper sqlExplainInfoMapper;
    @Resource
    private SqlExplainStarsMapper sqlExplainStarsMapper;
    @Resource
    private SqlExplainStatisticsMapper sqlExplainStatisticsMapper;


    @GetMapping("/overall")
    public ApiResponse<?> overall(@RequestParam String jobNumber, String tabKey) {
        if (StringUtils.isEmpty(jobNumber)) {
            return ApiResponse.failResponse("工号不可为空");
        }
        Map<String, Object> res = new HashMap<>();
        if (tabKey.equals("star")) {
            int total = sqlAppConfigsMapper.selectCount();
            res.put("count", total);
            List<String> apps = sqlExplainStarsMapper.selectByJobNum(jobNumber);
            res.put("starCount", apps.size());
            List<SqlExplainStatistics> sqlExplainStatistics = null;
            if (apps.size() != 0) {
                sqlExplainStatistics = sqlExplainStatisticsMapper.selectByServiceNames(apps);
            }
            res.put("list", sqlExplainStatistics);
        }

        if (tabKey.equals("all")) {
            int count = sqlAppConfigsMapper.selectCount();
            res.put("count", count);
            int starTotal = sqlExplainStarsMapper.selectCountByJobNum(jobNumber);
            res.put("starCount", starTotal);
            List<SqlExplainStatistics> sqlExplainStatistics = sqlExplainStatisticsMapper.selectAll();
            res.put("list", sqlExplainStatistics);
        }
        return ApiResponse.succResponse(res);
    }

    @GetMapping("/detail/overall")
    public ApiResponse<?> serviceDetailOverall(@RequestParam String app) {
        Map<String, Object> resMap = new HashMap<>();
        try {
            SqlExplainStatistics sqlExplainStatistics = sqlExplainStatisticsMapper.selectByServiceName(app);
            SqlAppConfigs appConfigs = sqlAppConfigsMapper.selectByService(app);
            resMap.put("users", appConfigs.getUsers());
            resMap.put("explainSwitch", appConfigs.getExplainSwitch());
            resMap.put("dingToken", appConfigs.getDingToken());
            resMap.put("serviceName", app);
            resMap.put("health", sqlExplainStatistics.getHealth());
            resMap.put("slowPercent", sqlExplainStatistics.getSlowPercent());
            resMap.put("slowTotal", sqlExplainStatistics.getSlowTotal());
            resMap.put("explainTotal", sqlExplainStatistics.getExplainTotal());
            resMap.put("latestSlowInSeven", sqlExplainStatistics.getLatestSlowInSeven());
        } catch (Exception e) {
            return ApiResponse.failResponse(e.getMessage());
        }
        return ApiResponse.succResponse(resMap);
    }

    @PostMapping("/detail/query")
    public ApiResponse<?> serviceDetailList(@RequestBody SqlDetailListPageRequest request) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<SqlExplainInfo> infos = sqlExplainInfoMapper.queryByConditions(request);
        PageInfo<SqlExplainInfo> SqlExplainPageInfo = new PageInfo<>(infos);
        return ApiResponse.succResponse(SqlExplainPageInfo);
    }
}
