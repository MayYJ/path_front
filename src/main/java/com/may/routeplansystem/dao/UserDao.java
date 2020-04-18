package com.may.routeplansystem.dao;

import com.may.routeplansystem.entity.po.UserMessage;
import org.springframework.stereotype.Repository;

/**
 * @author:dengsiyuan
 * @Date:2018/9/23 20:31
 */
@Repository("userDao")
public interface UserDao {

    /**
     * 判断用户登录凭证是否存在
     *
     * @param userMessage
     * @return true:凭证正确 false:凭证错误
     */
    UserMessage isLogin(UserMessage userMessage);

    /**
     * 查询用户信息
     *
     * @param userId
     * @return UserMessage
     */
    UserMessage userMessage(String userId);

    /**
     * 用户注册
     *
     * @param userMessage
     * @return -1:注册失败
     */
    boolean insertUser(UserMessage userMessage);

    /**
     * 注册邮箱唯一性验证
     *
     * @param eMail
     * @return UserMessage
     */
    UserMessage userEmail(String eMail);

    /**
     * 通过userName查找用户信息
     * @param userName
     * @return
     */
    UserMessage selectUserByUserName(String userName);

}
