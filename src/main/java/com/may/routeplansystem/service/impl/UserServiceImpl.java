package com.may.routeplansystem.service.impl;

import com.may.routeplansystem.constant.Constant;
import com.may.routeplansystem.constant.CookieKey;
import com.may.routeplansystem.constant.SessionMessage;
import com.may.routeplansystem.dao.UserDao;
import com.may.routeplansystem.entity.po.UserMessage;
import com.may.routeplansystem.exception.AuthentationException;
import com.may.routeplansystem.exception.ParameterException;
import com.may.routeplansystem.exception.ProcessException;
import com.may.routeplansystem.exception.ThreeServiceException;
import com.may.routeplansystem.service.UserService;
import com.may.routeplansystem.service.util.ServiceUtil;
import com.may.routeplansystem.util.EncryptionUtil;
import com.may.routeplansystem.util.VerifyCodeImageUtil;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


/**
 * @author 10587
 */
@Service
@Configuration
@Import(value = MailSenderAutoConfiguration.class)
public class UserServiceImpl implements UserService {

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private UserDao userDao;

    private static final int MAIL_CODE_LENGTH = 5;


    @Override
    public UserMessage userLogin(String userName, String password,
                                 String code, HttpSession session,
                                 HttpServletResponse response) {
        checkVerifyCode(code, session);
        UserMessage userMessage = userDao.selectUserByUserName(userName);
        checkUser(password, userMessage);
        session.setAttribute(SessionMessage.USER_ID_KEY, userMessage.getUserId());
        session.setAttribute(SessionMessage.LOGIN_KEY, SessionMessage.LOGIN_ATTRIBUTE);
        addCookie(response, userName, password);
        return userMessage;
    }

    private void checkUser(String password, UserMessage userMessage) {
        if (userMessage == null || !userMessage.getPassword().equals(EncryptionUtil.md5(password))) {
            throw new AuthentationException("请检查用户名或者密码是否有误");
        }
    }

    private void checkVerifyCode(String code, HttpSession session) {
        String verifyCode = (String) session.getAttribute(SessionMessage.CODE_KEY);
        if (verifyCode == null) {
            throw new ProcessException("请先获取验证码图片");
        }
        if (!code.equalsIgnoreCase(verifyCode)) {
            throw new ProcessException("验证码有误");
        }
    }

    private void addCookie(HttpServletResponse response, String userName, String password) {
        Cookie userNameCookie = new Cookie(CookieKey.USER_NAME, userName);
        Cookie passwordCookie = new Cookie(CookieKey.PASSWORD, password);
        response.addCookie(userNameCookie);
        response.addCookie(passwordCookie);
    }

    @Override
    public UserMessage userRegister(UserMessage userMessage, String mailCode, String rePassword, HttpSession session) {
        checkRePassword(userMessage.getPassword(), rePassword);
        checkMailCode(mailCode, session);
        checkDuplicate(userMessage);
        userMessage.setPassword(EncryptionUtil.md5(userMessage.getPassword()));
        boolean flag = userDao.insertUser(userMessage);
        ServiceUtil.checkSqlExecuted(flag, session,
                (HttpSession httpSession) ->
                        httpSession.removeAttribute(SessionMessage.MAIL_CODE_KEY));
        return userMessage;
    }

    public void checkDuplicate(UserMessage userMessage) {
        UserMessage userMessage1 = userDao.selectUserByUserName(userMessage.getUserName());
        if (userMessage1 != null) {
            throw new ParameterException("用户名已经被注册过了");
        }
        UserMessage userMessage2 = userDao.userEmail(userMessage.getEMail());
        if (userMessage2 != null) {
            throw new ParameterException("邮箱已经被注册过了");
        }

    }

    private void checkMailCode(String code, HttpSession session) {
        String mailVerifyCode = (String) session.getAttribute(SessionMessage.MAIL_CODE_KEY);
        if (!mailVerifyCode.equals(code)) {
            throw new ProcessException("邮箱验证码错误");
        }
    }

    private void checkRePassword(String beforePassword, String rePassword) {
        if (!beforePassword.equals(rePassword)) {
            throw new ProcessException("重复密码不正确");
        }
    }

    @Override
    public void sendVerifyMail(String eMail, HttpSession session) {
        String mailCode = generateRandomMailCode();
        session.setAttribute(SessionMessage.MAIL_CODE_KEY, mailCode);
        sendMailCodeMail(eMail, mailCode);
    }

    @Override
    public void loginOut(HttpServletRequest request) {
        deleteCookies(request);
        HttpSession session = request.getSession();
        session.removeAttribute(SessionMessage.LOGIN_KEY);
        session.invalidate();
    }

    private void deleteCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(CookieKey.USER_NAME)
            || cookie.getName().equals(CookieKey.PASSWORD)){
                cookie.setMaxAge(0);
            }
        }
    }

    @Override
    public boolean autoLogin(HttpServletRequest request) {
        return autoLoginBySession(request.getSession()) || autoLoginByCookie(request);
    }

    @Override
    public void generateVerifyCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Object[] objs = VerifyCodeImageUtil.createImage();
        request.getSession().setAttribute(SessionMessage.CODE_KEY, objs[0]);
        BufferedImage image = (BufferedImage) objs[1];
        response.setContentType("image/png");
        OutputStream os = response.getOutputStream();
        ImageIO.write(image, "png", os);
    }

    private boolean autoLoginBySession(HttpSession session) {
        return session.getAttribute(SessionMessage.LOGIN_KEY) != null;

    }

    private boolean autoLoginByCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String userName = null;
        String password = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(CookieKey.USER_NAME)) {
                userName = cookie.getValue();
            }
            if (cookie.getName().equals(CookieKey.PASSWORD)) {
                password = cookie.getValue();
            }
        }

        if (userName == null || password == null) {
            return false;
        }
        UserMessage userMessage = userDao.selectUserByUserName(userName);
        if (userMessage.getPassword().equals(EncryptionUtil.md5(password))) {
            request.getSession().setAttribute(SessionMessage.LOGIN_KEY, SessionMessage.LOGIN_ATTRIBUTE);
            return true;
        }
        return false;
    }

    public String generateRandomMailCode() {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < MAIL_CODE_LENGTH; i++) {
            int number = random.nextInt(base.length());
            code.append(base.charAt(number));
        }

        return code.toString();
    }

    public void sendMailCodeMail(String eMail, String mailCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(Constant.EMAIL_FROM);
            helper.setTo(eMail);
            helper.setSubject("Path Security");
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String sb = "<div style='position: relative; margin: 0 auto; width: 450px;height: 350px;'>" +
                    "<h1 align='center'><strong>Path</strong></h1>" +
                    "<h5>您好：</h5>" +
                    "<h5>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;欢迎您通过邮箱验证码来验证您的身份！</h5>" +
                    "<h5>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;快！使用<div style='position: relative; " +
                    "margin: 0 auto; width: 100px;height: 45px; text-align: center; background-color: #EEE'>" +
                    "<font size='5' style='position: relative; top: 6px;'>"
                    + mailCode
                    + "</font></div>去验证您的身份吧！切记不要告诉他人哦！</h5>" +
                    "<h5 style='position:relative; float: right; margin-bottom: 0'>Path</h5><br><br>" +
                    "<h5 style='position: relative; float: right; margin-top: 0'>" + simpleDateFormat.format(date) + "</h5></div>";
            helper.setText(sb, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new ThreeServiceException("发送邮件失败");
        }
    }


}
