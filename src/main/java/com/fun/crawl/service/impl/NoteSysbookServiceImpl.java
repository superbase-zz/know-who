package com.fun.crawl.service.impl;

import com.fun.crawl.base.service.impl.BaseServiceImpl;
import com.fun.crawl.mapper.NoteSysbookMapper;
import com.fun.crawl.model.NoteSysbook;
import com.fun.crawl.model.query.NoteSysBookVoQuery;
import com.fun.crawl.service.NoteSysbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-06-20
 */
@Service
public class NoteSysbookServiceImpl extends BaseServiceImpl<NoteSysbookMapper, NoteSysbook> implements NoteSysbookService {

    @Autowired
    private NoteSysbookMapper noteSysbookMapper;


    @Override
    public NoteSysBookVoQuery pageVoByQuery(NoteSysBookVoQuery query) {
        noteSysbookMapper.pageByQuery(query);
        return query;
    }



}
