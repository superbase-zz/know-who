package com.fun.crawl.model.query;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fun.crawl.model.NoteBook;
import lombok.Data;

import java.util.Date;

@Data
public class NoteBookVoQuery extends Page<NoteBook> {

    /**
     * 所属用户
     */
    private Long noteUserId;

    /**
     * 笔记本编号
     */
    private String notebookGuid;

    /**
     * 笔记本名称
     */
    private String notebookName;

    /**
     * 笔记本（1，个人笔记本，2,链接笔记本）
     */
    private Long notebookType;

    /**
     * 是否删除1，已删除
     */
    private Long isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date modifyTime;

}
