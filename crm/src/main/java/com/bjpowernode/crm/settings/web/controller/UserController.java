package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.commons.constant.Constants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: W
 * @Date: @Date: 2022-04-18 14:52
 * @Description:
 */
@Controller
public class UserController {

    @Resource
    private UserService userService;

    /**
     * url要和当前controller方法处理完请求之后，响应信息返回的页面的资源目录保持一致
     * settings/qx/user/login.jsp
     */
    @RequestMapping("/settings/qx/user/toLogin.do")
    public String toLogin() {
        // 请求转发到登录页面
        return "settings/qx/user/login";
    }

    /*
        RequestMapping("/xxx")  xxx要和响应到哪个页面的资源路径保持一致
           谁发的请求响应给谁
     */

    /**
     * @param loginAct 登录用户名
     * @param loginPwd 登录密码
     * @param isRemPwd 是否记住密码
     * @return 一个json字符串
     */
    @RequestMapping("/settings/qx/user/login.do")
    @ResponseBody
    public Object login(String loginAct, String loginPwd, String isRemPwd,
                        HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        // 封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("loginAct", loginAct);
        map.put("loginPwd", loginPwd);
        // 调用service方法查询用户
        User user = userService.queryUserByLoginActAndPwd(map);
        // 根据查询结果生成相应信息
        ReturnObject returnObject = new ReturnObject();
        if (user == null) {
            // 登录失败, 用户名或密码错误
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage(Constants.WRONG_USERNAME_OR_PASSWORD);
        } else {
            // 进一步判断账号是否合法
            if (DateUtils.formatDateTime(new Date()).compareTo(user.getExpireTime()) > 0) {
                // 过期了, 登录失败，账号已过期
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage(Constants.ACCOUNT_EXPIRED);
            } else if ("0".equals(user.getLockState())) {
                // 登录失败，状态被锁定
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage(Constants.ACCOUNT_STATUS_LOCKED);
            } else if (!user.getAllowIps().contains(request.getRemoteAddr())) {
                // 登录失败, 不在常用IP地址登录, ip受限
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage(Constants.IP_ADDRESS_NOT_ALLOWED);
            } else {
                // 登录成功
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);

                // 把user对象保存到session作用域中
                session.setAttribute(Constants.SESSION_USER, user);

                // 如果需要记住密码, 则往外写cookie
                if ("true".equals(isRemPwd)) {
                    Cookie cookie1 = new Cookie("loginAct", user.getLoginAct());
                    cookie1.setMaxAge(10 * 24 * 60 * 60);
                    response.addCookie(cookie1);
                    Cookie cookie2 = new Cookie("loginPwd", user.getLoginPwd());
                    cookie2.setMaxAge(10 * 24 * 60 * 60);
                    response.addCookie(cookie2);
                } else {
                    // 没记住密码
                    // 把没有过期的cookie都删掉
                    Cookie cookie1 = new Cookie("loginAct", "");
                    cookie1.setMaxAge(0);
                    response.addCookie(cookie1);
                    Cookie cookie2 = new Cookie("loginPwd", "");
                    cookie2.setMaxAge(0);
                    response.addCookie(cookie2);
                }
            }
        }
        return returnObject;
    }

    @RequestMapping("/settings/qx/user/logout.do")
    public String logout(HttpServletResponse response, HttpSession session) {
        // 清空cookie
        Cookie cookie1 = new Cookie("loginAct", "");
        cookie1.setMaxAge(0);
        response.addCookie(cookie1);
        Cookie cookie2 = new Cookie("loginPwd", "");
        cookie2.setMaxAge(0);
        response.addCookie(cookie2);
        // 销毁session
        session.invalidate();

        // 跳转到首页
        return "redirect:/";
    }

}
