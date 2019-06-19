package com.fun.crawl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fun.crawl.base.enums.DataStatusEnum;
import com.fun.crawl.model.NoteBook;
import com.fun.crawl.mapper.NoteBookMapper;
import com.fun.crawl.model.query.NoteBookVoQuery;
import com.fun.crawl.service.NoteBookService;
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
public class NoteBookServiceImpl extends BaseServiceImpl<NoteBookMapper, NoteBook> implements NoteBookService {


    @Autowired
   private NoteBookMapper noteBookMapper;


    @Override
    public NoteBook slectByNameLikeAndUserId(Long id, String bookName) {
        QueryWrapper<NoteBook> query = new QueryWrapper();
        query.lambda().like(NoteBook::getNotebookName,bookName);
        query.lambda().eq(NoteBook::getNoteUserId,id);
        NoteBook noteBook = noteBookMapper.selectOne(query);
        return noteBook;
    }

    @Override
    public NoteBookVoQuery pageNoteBookVoByQuery(NoteBookVoQuery query) {
        noteBookMapper.pageByQuery(query);
        return query;
    }
}
