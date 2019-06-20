package com.fun.crawl.service;

import com.fun.crawl.model.NoteSysbook;
import com.fun.crawl.base.service.BaseService;
import com.fun.crawl.model.query.NoteSysBookVoQuery;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2019-06-20
 */
public interface NoteSysbookService extends BaseService<NoteSysbook> {

    NoteSysBookVoQuery pageVoByQuery(NoteSysBookVoQuery query);

}
