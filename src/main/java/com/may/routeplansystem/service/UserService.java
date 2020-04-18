package com.may.routeplansystem.service;

import com.may.routeplansystem.entity.po.UserMessage;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author:dengsiyuan
 * @Date:2018/9/24 18:11
 */
@Service
public interface UserService {
    /**
     * 验证登陆信息是否正确
     *
     * @param userId
     * @param password
     * @param code
     * @param session
     * @return true:验证通过 false:验证不通过
     */
    UserMessage userLogin(String userId, String password, String code,
                          HttpSession session, HttpServletResponse response);

    /**
     * 用户注册
     *
     * @param userMessage
     * @param mailCode
     * @param rePassword
     * @param session
     * @return -1:注册失败
     */
    UserMessage userRegister(UserMessage userMessage, String mailCode, String rePassword, HttpSession session);

    /**
     * 用户邮箱验证码发送
     *
     * @param eMail
     * @param session
     * @return 邮件
     */
    void sendVerifyMail(String eMail, HttpSession session);

    /**
     * 退出登录
     * @param request
     */
    void loginOut(HttpServletRequest request);

    /**
     * 自动登录
     * @param request
     */
    boolean autoLogin(HttpServletRequest request) ;

    void generateVerifyCode(HttpServletRequest request, HttpServletResponse response) throws IOException, IOException;


}
