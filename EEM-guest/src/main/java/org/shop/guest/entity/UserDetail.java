package org.shop.guest.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户详情
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_detail")
public class UserDetail {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    /**
     * 性别 (男1 女0)
     */
    private Integer gender;

    /**
     * 邮箱
     */
    private String email;

    /**
     * QQ号
     */
    private String qq;

    /**
     * 微信号
     */
    private String wechat;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 地区
     */
    private String area;

    /**
     * 学校
     */
    private String school;

    /**
     * 学院
     */
    private String institution;

    /**
     * 年级
     */
    private String grade;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;

    /**
     * 个人介绍
     */
    private String introduce;

}
