package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.constant.Constants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.DicValueService;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.domain.TranRemark;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TranHistoryService;
import com.bjpowernode.crm.workbench.service.TranRemarkService;
import com.bjpowernode.crm.workbench.service.TranService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @Author: W
 * @Date: @Date: 2022-04-24 17:31
 * @Description:
 */
@Controller
public class TranController {

    @Resource
    private DicValueService dicValueService;

    @Resource
    private TranService tranService;

    @Resource
    private UserService userService;

    @Resource
    private CustomerService customerService;

    @Resource
    private TranRemarkService tranRemarkService;

    @Resource
    private TranHistoryService tranHistoryService;

    @RequestMapping("/workbench/transaction/index.do")
    public String index(HttpServletRequest request) {
        // 调用service层方法，查询动态数据-交易类型和交易来源
        List<DicValue> transactionTypeList = dicValueService.queryDicValueByTypeCode("transactionType");
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        // 把数据保存到request作用域
        request.setAttribute("transactionTypeList", transactionTypeList);
        request.setAttribute("sourceList", sourceList);
        request.setAttribute("stageList", stageList);
        // 请求转发
        return "workbench/transaction/index";
    }

    @RequestMapping("/workbench/transaction/queryTranByConditionForPage.do")
    @ResponseBody
    public Object queryTranByConditionForPage(String name, String customerName, String stage, String type, String owner,
                                              String source, String fullname, int pageNo, int pageSize) {
        // 封装参数到map中
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("customerName", customerName);
        map.put("stage", stage);
        map.put("type", type);
        map.put("owner", owner);
        map.put("source", source);
        map.put("fullname", fullname);
        map.put("beginNo", (pageNo - 1) * pageSize);
        map.put("pageSize", pageSize);
        // 调用service层方法查询数据
        List<Tran> tranList = tranService.queryTranByConditionForPage(map);
        for (Tran tran : tranList) {
            System.out.println(tran);
        }
        int totalRows = tranService.queryCountOfTranByCondition(map);
        // 查询到数据封装到map中，响应到页面
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("tranList", tranList);
        resultMap.put("totalRows", totalRows);
        return resultMap;
    }

    @RequestMapping("/workbench/transaction/toSave.do")
    public String toSave(HttpServletRequest request) {
        // 调用service层方法，查询动态数据
        List<User> userList = userService.queryAllUsers();
        List<DicValue> transactionTypeList = dicValueService.queryDicValueByTypeCode("transactionType");
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        // 把数据保存到作用域中
        request.setAttribute("transactionTypeList", transactionTypeList);
        request.setAttribute("sourceList", sourceList);
        request.setAttribute("stageList", stageList);
        request.setAttribute("userList", userList);
        // 请求转发
        return "workbench/transaction/save";
    }

    @RequestMapping("/workbench/transaction/getPossibilityByStage.do")
    @ResponseBody
    public Object getPossibilityByStage(String stageValue) {
        // 解析properties文件，获取可能性值
        ResourceBundle bundle = ResourceBundle.getBundle("possibility");
        // 返回响应信息
        return bundle.getString(stageValue);
    }

    @RequestMapping("/workbench/transaction/queryCustomerNameByName.do")
    public @ResponseBody
    Object queryCustomerNameByName(String customerName) {
        //调用service层方法，查询所有客户名称
        List<String> customerNameList = customerService.queryCustomerNameByName(customerName);
        //根据查询结果，返回响应信息
        return customerNameList;//['xxxx','xxxxx',......]
    }


    @RequestMapping("/workbench/transaction/saveCreateTran.do")
    @ResponseBody
    public Object saveCreateTran(@RequestParam Map<String, Object> map, HttpSession session) {
        //封装参数
        map.put(Constants.SESSION_USER, session.getAttribute(Constants.SESSION_USER));

        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，保存创建的交易
            tranService.saveCreateTran(map);

            returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试....");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/transaction/detailTran.do")
    public String detailTran(String id, HttpServletRequest request) {
        //调用service层方法，查询数据
        Tran tran = tranService.queryTranForDetailById(id);
        List<TranRemark> remarkList = tranRemarkService.queryTranRemarkForDetailByTranId(id);
        List<TranHistory> historyList = tranHistoryService.queryTranHistoryForDetailByTranId(id);

        //根据tran所处阶段名称查询可能性
        ResourceBundle bundle = ResourceBundle.getBundle("possibility");
        String possibility = bundle.getString(tran.getStage());
        tran.setPossibility(possibility);

        //把数据保存到request中
        request.setAttribute("tran", tran);
        request.setAttribute("remarkList", remarkList);
        request.setAttribute("historyList", historyList);
        //request.setAttribute("possibility",possibility);

        //调用service方法，查询交易所有的阶段
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");

        request.setAttribute("stageList", stageList);

        //请求转发
        return "workbench/transaction/detail";
    }


}
