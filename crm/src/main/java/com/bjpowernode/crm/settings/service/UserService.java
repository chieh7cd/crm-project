package com.bjpowernode.crm.settings.service;

import com.bjpowernode.crm.settings.domain.User;

import java.util.List;
import java.util.Map;

/**
 * @Author: W
 * @Date: @Date: 2022-04-18 16:39
 * @Description:
 */
public interface UserService {

    User queryUserByLoginActAndPwd(Map<String, Object> map);

    List<User> queryAllUsers();

}
