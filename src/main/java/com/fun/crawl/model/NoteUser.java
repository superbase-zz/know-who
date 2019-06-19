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
@TableName("t_note_user")
public class NoteUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 印像token表
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 映像笔记用户编号
     */
    @TableField("user_id")
    private String userId;

    /**
     * 用户昵称
     */
    @TableField("username")
    private String username;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * token
     */
    @TableField("token")
    private String token;

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

    /**
     * 更新时间
     */
    @TableField("main_account")
    private Integer mainAccount;


}
