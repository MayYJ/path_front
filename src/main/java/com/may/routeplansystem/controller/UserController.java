package com.may.routeplansystem.controller;

import com.may.routeplansystem.constant.Response;
import com.may.routeplansystem.constant.ResponseStatu;
import com.may.routeplansystem.constant.SessionMessage;
import com.may.routeplansystem.dao.UserDao;
import com.may.routeplansystem.entity.dto.ResponseEntity;
import com.may.routeplansystem.entity.po.UserMessage;
import com.may.routeplansystem.service.UserService;
import com.may.routeplansystem.util.VerifyCodeImageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;


@RestController
@RequestMapping("userSystem")
@Api(tags = "用户模块")
@Validated
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserDao userDao;

    @GetMapping(value = "/verifyCode")
    @ApiOperation("生成二维码图片")
    public void verifyCode(HttpServletResponse response, HttpServletRequest request) throws IOException {
        userService.generateVerifyCode(request,response);
    }

    @PostMapping(value = "/session/user")
    @ApiOperation("用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "用户名", dataType = "string", required = true),
            @ApiImplicitParam(name = "password", value = "密码", dataType = "string", required = true),
            @ApiImplicitParam(name = "code", value = "验证码", dataType = "string", required = true)
    })
    public ResponseEntity<UserMessage> userLogin(String userName, String password, String code,
                                                 HttpSession session, HttpServletResponse response) {
        UserMessage userMessage =  userService
                .userLogin(userName, password, code, session, response);
        return new ResponseEntity<>(ResponseStatu.SUCCESS, Response.SUCCESSFUL, userMessage);
    }

    @DeleteMapping(value = "/session/user")
    @ApiOperation("用户退出登录")
    public Object userLogOut(HttpServletRequest request) {
        userService.loginOut(request);
        return new ResponseEntity<>(ResponseStatu.SUCCESS, Response.SUCCESSFUL, null);
    }

    @PostMapping(value = "/user")
    @ApiOperation("用户注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", dataType = "string", value = "用户名", required = true),
            @ApiImplicitParam(name = "password", dataType = "string", value = "密码", required = true),
            @ApiImplicitParam(name = "eMail", dataType = "string", value = "邮箱", required = true),
            @ApiImplicitParam(name = "mailCode", dataType = "string", value = "邮箱验证码", required = true),
            @ApiImplicitParam(name = "rePassword", dataType = "string", value = "重复验证密码", required = true)
    })
    public ResponseEntity<UserMessage> userRegister(UserMessage userMessage, String mailCode, String rePassword, HttpSession session) {
        UserMessage responseUser =  userService.userRegister(userMessage, mailCode, rePassword, session);
        return new ResponseEntity<>(ResponseStatu.SUCCESS, Response.SUCCESSFUL, responseUser);
    }

    @GetMapping(value = "/user/eMailCode")
    @ApiOperation("邮箱发送注册验证码")
    @ApiImplicitParam(name = "eMail", value = "邮箱", paramType = "query", required = true)
    public ResponseEntity verifyMail(String eMail, HttpSession session) {
        userService.sendVerifyMail(eMail, session);
        return new ResponseEntity<>(ResponseStatu.SUCCESS, Response.SUCCESSFUL, null);
    }

    @GetMapping("autologin")
    @ApiOperation("自动登录接口, 需要开启Cookie")
    public ResponseEntity autoLogin(HttpServletRequest request) {
        boolean flag = userService.autoLogin(request);
        if (flag) {
            return new ResponseEntity<>(ResponseStatu.SUCCESS, Response.SUCCESSFUL, null);
        }else {
            return new ResponseEntity<>(ResponseStatu.AUTHENTATION_FIAL, "自动登录失败", null);
        }
    }

    @GetMapping("verifyUserName")
    @ApiOperation("注册用户名唯一性验证")
    public ResponseEntity verifyUserName(String userName){
        UserMessage userMessage = userDao.selectUserByUserName(userName);
        if(userMessage != null){
            return new ResponseEntity<>(ResponseStatu.USER_OPERATION_ERROR, Response.SUCCESSFUL,"该用户名已存在");
        }else {
            return new ResponseEntity<>(ResponseStatu.SUCCESS, Response.SUCCESSFUL,null);
        }
    }

    @GetMapping("verifyEmail")
    @ApiOperation("注册邮箱唯一性验证")
    public ResponseEntity verifyEmail(String eMail){
        UserMessage userMessage = userDao.userEmail(eMail);
        if(userMessage != null){
            return new ResponseEntity<>(ResponseStatu.USER_OPERATION_ERROR, Response.SUCCESSFUL,"该邮箱已存在");
        }else {
            return new ResponseEntity<>(ResponseStatu.SUCCESS, Response.SUCCESSFUL,null);
        }
    }
}
