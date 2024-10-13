package org.shop.guest.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户详细信息完全DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailAllDTO {

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
