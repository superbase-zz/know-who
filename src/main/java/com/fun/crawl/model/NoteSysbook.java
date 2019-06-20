package com.fun.crawl.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author jobob
 * @since 2019-06-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_note_sysbook")
public class NoteSysbook implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 印象同步笔记本
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 笔记本编号
     */
    @TableField("notebook_guid")
    private String notebookGuid;

    /**
     * 笔记本名称
     */
    @TableField("notebook_name")
    private String notebookName;

    /**
     * 哪个账号同步
     */
    @TableField("uid")
    private Long uid;

    /**
     * 是否删除1，已删除
     */
    @TableField("is_deleted")
    private Long isDeleted;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;


}
