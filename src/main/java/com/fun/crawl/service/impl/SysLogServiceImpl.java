package com.fun.crawl.service.impl;

import com.fun.crawl.model.SysLog;
import com.fun.crawl.mapper.SysLogMapper;
import com.fun.crawl.model.query.SysLogQuery;
import com.fun.crawl.service.SysLogService;
import com.fun.crawl.base.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 日志表 服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-06-18
 */
@Service
public class SysLogServiceImpl extends BaseServiceImpl<SysLogMapper, SysLog> implements SysLogService {


    @Autowired
    private SysLogMapper sysLogMapper;

    @Override
    public SysLogQuery pageByQuery(SysLogQuery query) {
        query.setDesc("id");
        sysLogMapper.pageByQuery(query);
        return query;
    }
}
