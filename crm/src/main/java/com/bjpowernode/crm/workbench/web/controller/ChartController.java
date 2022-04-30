package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.workbench.service.TranService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @Author: W
 * @Date: @Date: 2022-04-25 16:37
 * @Description:
 */
@Controller
public class ChartController {

    @Resource
    private TranService tranService;

    @RequestMapping("/workbench/chart/transaction/index.do")
    public String index() {
        return "workbench/chart/transaction/index";
    }

    @RequestMapping("/workbench/chart/transaction/queryCountOfTranGroupByStage.do")
    @ResponseBody
    public Object queryCountOfTranGroupByStage() {
        // 调用service层方法查询数据
        // 根据查询结果返回响应信息
        return tranService.queryCountOfTranGroupByStage();
    }

}
