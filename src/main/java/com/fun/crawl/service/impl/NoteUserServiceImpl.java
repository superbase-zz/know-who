package com.fun.crawl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fun.crawl.mapper.NoteBookMapper;
import com.fun.crawl.model.NoteBook;
import com.fun.crawl.model.NoteUser;
import com.fun.crawl.mapper.NoteUserMapper;
import com.fun.crawl.model.query.NoteBookVoQuery;
import com.fun.crawl.model.query.NoteUserVoQuery;
import com.fun.crawl.service.NoteUserService;
import com.fun.crawl.base.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-06-11
 */
@Service
public class NoteUserServiceImpl extends BaseServiceImpl<NoteUserMapper, NoteUser> implements NoteUserService {
    @Autowired
    private NoteUserMapper noteUserMapper;




    @Override
    public NoteUserVoQuery pageNoteUserVoByQuery(NoteUserVoQuery query) {
        noteUserMapper.pageByQuery(query);
        return query;
    }
}
