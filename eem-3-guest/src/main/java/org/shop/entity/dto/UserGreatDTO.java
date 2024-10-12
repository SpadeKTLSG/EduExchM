package org.shop.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户超级DTO (包含用户和用户功能和用户详细信息)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGreatDTO {

    // 以下为用户核心信息

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String name;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机
     */
    private String phone;

    // 以下为用户详细信息

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

    // 以下为用户功能信息

    /**
     * 状态 (0正常, 1封禁)
     */
    private Integer status;

    /**
     * 收款码pic
     */
    private String billcard;

    /**
     * 积分数量
     */
    private Long credit;

    /**
     * 红牌警告数量
     */
    private Integer redhit;

    /**
     * 嘉奖数量
     */
    private Integer godhit;

    /**
     * 已售出数量
     */
    private Integer solds;

    /**
     * 已获得数量
     */
    private Integer gains;

    /**
     * 粉丝数量
     */
    private Integer fans;

    /**
     * 关注人数量
     */
    private Integer followee;

    /**
     * 收藏集合, 存放商品id, ','分割
     */
    private String collections;


}
