package com.fun.crawl.service;

import com.fun.crawl.model.NoteUser;
import com.fun.crawl.base.service.BaseService;
import com.fun.crawl.model.query.NoteBookVoQuery;
import com.fun.crawl.model.query.NoteUserVoQuery;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2019-06-11
 */
public interface NoteUserService extends BaseService<NoteUser> {

    NoteUserVoQuery pageNoteUserVoByQuery(NoteUserVoQuery query);

}
