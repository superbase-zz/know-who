package com.fun.crawl.service;

import com.fun.crawl.base.service.BaseService;
import com.fun.crawl.model.SysLog;
import com.fun.crawl.model.query.SysLogQuery;

/**
 * <p>
 * 日志表 服务类
 * </p>
 *
 * @author jobob
 * @since 2019-06-18
 */

/**
 * <p>
 * 日志表 服务类
 * </p>
 */
public interface SysLogService extends BaseService<SysLog> {

    /**
     * 分页条件查询
     *
     * @param query
     * @return
     */
    SysLogQuery pageByQuery(SysLogQuery query);
}

