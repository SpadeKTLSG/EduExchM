package org.shop.admin.common.utils;

import cn.hutool.core.util.StrUtil;

/**
 * 正则工具类
 */
public class RegexUtil {

    /**
     * 手机号正则
     */
    final static String PHONE_REGEX = "^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$";

    /**
     * 邮箱正则
     */
    final static String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    /**
     * 密码正则。4~32位的字母、数字、下划线
     */
    final static String PASSWORD_REGEX = "^\\w{4,32}$";

    /**
     * 验证码正则, 6位数字或字母
     */
    final static String VERIFY_CODE_REGEX = "^[a-zA-Z\\d]{6}$";

    /**
     * 是否是无效手机格式
     */
    public static boolean isPhoneInvalid(String phone) {
        return mismatch(phone, PHONE_REGEX);
    }


    /**
     * 是否是无效邮箱格式
     */
    public static boolean isEmailInvalid(String email) {
        return mismatch(email, EMAIL_REGEX);
    }

    /**
     * 是否是无效验证码格式
     */
    public static boolean isCodeInvalid(String code) {
        return mismatch(code, VERIFY_CODE_REGEX);
    }

    /**
     * 校验是否不符合正则格式
     */
    private static boolean mismatch(String str, String regex) {
        if (StrUtil.isBlank(str)) {
            return true;
        }
        return !str.matches(regex);
    }
}
