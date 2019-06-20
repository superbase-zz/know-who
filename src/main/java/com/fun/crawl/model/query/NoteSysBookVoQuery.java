package com.fun.crawl.model.query;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fun.crawl.model.NoteSysbook;
import lombok.Data;

import java.util.Date;

@Data
public class NoteSysBookVoQuery extends Page<NoteSysbook> {


    /**
     * 笔记本名称
     */
    private String notebookName;

    /**
     * 哪个账号同步
     */
    private Long uid;

}
