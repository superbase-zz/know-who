package com.fun.crawl.service;


import com.fun.crawl.base.service.BaseService;
import com.fun.crawl.model.NoteBook;
import com.fun.crawl.model.query.NoteBookVoQuery;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jobob
 * @since 2019-06-11
 */
public interface NoteBookService extends BaseService<NoteBook> {

    NoteBook slectByNameLikeAndUserId(Long id, String bookName);


    NoteBookVoQuery pageNoteBookVoByQuery(NoteBookVoQuery query);
}


