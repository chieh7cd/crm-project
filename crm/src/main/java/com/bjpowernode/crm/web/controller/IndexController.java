package com.bjpowernode.crm.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: W
 * @Date: @Date: 2022-04-18 14:34
 * @Description:
 */
@Controller
public class IndexController {

    /*
        "/" 代表应用根目录下的/
     */
    @RequestMapping("/")
    public String index() {
        // 请求转发
        return "index";
    }

}
