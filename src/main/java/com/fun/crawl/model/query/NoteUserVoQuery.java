package com.fun.crawl.model.query;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fun.crawl.model.NoteUser;
import lombok.Data;

import java.util.Date;

@Data
public class NoteUserVoQuery extends Page<NoteUser> {

    /**
     * 印像token表
     */
    private Long id;

    /**
     * 映像笔记用户编号
     */
    private String userId;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * token
     */
    private String token;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date modifyTime;

    /**
     * 更新时间
     */
    private Integer mainAccount;

}
