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
 * @since 2019-06-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_note_book")
public class NoteBook implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 印像token表
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户
     */
    @TableField("note_user_id")
    private Long noteUserId;

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
     * 笔记本（1，个人笔记本，2,链接笔记本）
     */
    @TableField("notebook_type")
    private Long notebookType;

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

    /**
     * 更新时间
     */
    @TableField("modify_time")
    private Date modifyTime;


}
