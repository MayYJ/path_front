package com.may.routeplansystem.constant;

/**
 * @author 10587
 */
public interface SessionMessage {
    /**
     * 设置登录状态的key
     */
    String LOGIN_KEY = "login";

    /**
     * 登录状态为已登录的值
     */
    String LOGIN_ATTRIBUTE = "isLogined";

    /**
     * 登录验证码key
     */
    String CODE_KEY = "verifyCode";

    /**
     * session存放userId 的key
     */
    String USER_ID_KEY = "user";

    /**
     * 注册存放邮箱验证码的key
     */
    String MAIL_CODE_KEY = "mailcode";
}
