package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.constant.Constants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.DicValueService;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.ClueActivityRelation;
import com.bjpowernode.crm.workbench.domain.ClueRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueActivityRelationService;
import com.bjpowernode.crm.workbench.service.ClueRemarkService;
import com.bjpowernode.crm.workbench.service.ClueService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @Author: W
 * @Date: @Date: 2022-04-23 10:33
 * @Description:
 */
@Controller
public class ClueController {

    @Resource
    private UserService userService;

    @Resource
    private DicValueService dicValueService;

    @Resource
    private ClueService clueService;

    @Resource
    private ClueRemarkService clueRemarkService;

    @Resource
    private ActivityService activityService;

    @Resource
    private ClueActivityRelationService clueActivityRelationService;


    @RequestMapping("/workbench/clue/index.do")
    public String index(HttpServletRequest request) {
        // 调用service层方法，查询动态数据
        List<User> userList = userService.queryAllUsers();
        List<DicValue> appellationList = dicValueService.queryDicValueByTypeCode("appellation");
        List<DicValue> clueStateList = dicValueService.queryDicValueByTypeCode("clueState");
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");
        // 把数据放到request作用域中
        request.setAttribute("userList", userList);
        request.setAttribute("appellationList", appellationList);
        request.setAttribute("clueStateList", clueStateList);
        request.setAttribute("sourceList", sourceList);
        return "workbench/clue/index";
    }

    @RequestMapping("/workbench/clue/saveCreateClue.do")
    @ResponseBody
    public Object saveCreateClue(Clue clue, HttpSession session) {
        // 封装参数
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        clue.setId(UUIDUtils.getUUID());
        clue.setCreateTime(DateUtils.formatDateTime(new Date()));
        clue.setCreateBy(user.getId());
        ReturnObject returnObject = new ReturnObject();
        try {
            // 调用service层方法保存创建的线索
            int count = clueService.saveCreateClue(clue);
            if (count > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setMessage(Constants.SAVE_CREATE_CLUE_SUCCESS);
            } else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage(Constants.SAVE_CREATE_CLUE_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage(Constants.SAVE_CREATE_CLUE_FAILED);
        }
        return returnObject;
    }

    @RequestMapping("/workbench/clue/queryClueByConditionForPage.do")
    @ResponseBody
    public Object queryClueByConditionForPage(String fullname, String company, String phone, String mphone,
                                              String source, String owner, String state, int pageNo, int pageSize) {
        // 封装参数到map中
        Map<String, Object> map = new HashMap<>();
        map.put("fullname", fullname);
        map.put("company", company);
        map.put("phone", phone);
        map.put("mphone", mphone);
        map.put("source", source);
        map.put("owner", owner);
        map.put("state", state);
        map.put("beginNo", (pageNo - 1) * pageSize);
        map.put("pageSize", pageSize);
        // 调用service层方法查询数据
        List<Clue> clueList = clueService.queryClueByConditionForPage(map);
        int totalRows = clueService.queryCountOfClueByCondition(map);
        // 查询到数据封装到map中，响应到页面
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("clueList", clueList);
        resultMap.put("totalRows", totalRows);
        return resultMap;
    }

    @RequestMapping("/workbench/clue/detailClue.do")
    public String detailClue(String id, HttpServletRequest request) {
        // 调用service层方法，查询数据
        Clue clue = clueService.queryClueForDetailById(id);
        List<ClueRemark> clueRemarkList = clueRemarkService.queryClueRemarkForDetailByClueId(id);
        List<Activity> activityList = activityService.queryActivityForDetailByClueId(id);
        // 把数据保存到request作用域中
        request.setAttribute("clue", clue);
        request.setAttribute("clueRemarkList", clueRemarkList);
        request.setAttribute("activityList", activityList);
        // 跳转到detail页面,请求转发
        return "workbench/clue/detail";
    }

    @RequestMapping("/workbench/clue/queryActivityForDetailByNameClueId.do")
    @ResponseBody
    public Object queryActivityForDetailByNameClueId(String activityName, String clueId) {
        // 封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("activityName", activityName);
        map.put("clueId", clueId);
        // 调用service层方法，查询市场活动
        // 根据查询结果，返回响应信息
        return activityService.queryActivityForDetailByNameClueId(map);
    }

    @RequestMapping("/workbench/clue/saveBund.do")
    @ResponseBody
    public Object saveBund(String[] activityId, String clueId) {
        // 封装参数
        ClueActivityRelation car = null;
        List<ClueActivityRelation> relationList = new ArrayList<>();
        for (String aid : activityId) {
            car = new ClueActivityRelation();
            car.setActivityId(aid);
            car.setClueId(clueId);
            car.setId(UUIDUtils.getUUID());
            relationList.add(car);
        }
        ReturnObject returnObject = new ReturnObject();
        try {
            // 调用service层方法，批量保存线索和市场活动的关联关系
            int count = clueActivityRelationService.saveCreateClueActivityRelationByList(relationList);
            if (count > 0) {
                List<Activity> activityList = activityService.queryActivityForDetailByIds(activityId);
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setMessage(Constants.SAVE_CREATE_CLUE_ACTIVITY_RELATION_SUCCESS);
                returnObject.setRetData(activityList);
            } else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage(Constants.SAVE_CREATE_CLUE_ACTIVITY_RELATION_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage(Constants.SAVE_CREATE_CLUE_ACTIVITY_RELATION_FAILED);
        }
        return returnObject;
    }

    @RequestMapping("/workbench/clue/saveUnbund.do")
    @ResponseBody
    public Object saveUnbund(ClueActivityRelation clueActivityRelation) {
        ReturnObject returnObject = new ReturnObject();
        try {
            // 调用service层方法删除数据
            int count = clueActivityRelationService.deleteClueActivityRelationByClueIdActivityId(clueActivityRelation);
            if (count > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setMessage(Constants.DELETE_CLUE_ACTIVITY_RELATION_SUCCESS);
            } else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage(Constants.DELETE_CLUE_ACTIVITY_RELATION_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage(Constants.DELETE_CLUE_ACTIVITY_RELATION_FAILED);
        }
        return returnObject;
    }

    @RequestMapping("/workbench/clue/toConvert.do")
    public String toConvert(String id, HttpServletRequest request) {
        // 调用service层方法，查询线索的明细信息
        Clue clue = clueService.queryClueForDetailById(id);
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        // 把数据保存到request作用域
        request.setAttribute("clue", clue);
        request.setAttribute("stageList", stageList);

        return "workbench/clue/convert";
    }

    @RequestMapping("/workbench/clue/queryActivityForConvertByNameClueId.do")
    @ResponseBody
    public Object queryActivityForConvertByNameClueId(String activityName, String clueId) {
        // 封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("activityName", activityName);
        map.put("clueId", clueId);
        // 调用service层方法，查询市场活动
        // 根据查询结果返回响应信息
        return activityService.queryActivityForConvertByNameClueId(map);
    }

    @RequestMapping("/workbench/clue/convertClue.do")
    @ResponseBody
    public Object convertClue(String clueId, String money, String name, String expectedDate, String stage,
                              String activityId, String isCreateTran, HttpSession session) {
        // 封装参数
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        Map<String, Object> map = new HashMap<>();
        map.put("clueId", clueId);
        map.put("money", money);
        map.put("name", name);
        map.put("expectedDate", expectedDate);
        map.put("stage", stage);
        map.put("activityId", activityId);
        map.put("isCreateTran", isCreateTran);
        map.put(Constants.SESSION_USER, user);
        ReturnObject returnObject = new ReturnObject();
        try {
            // 调用service层方法, 保存线索转换
            clueService.saveConvertClue(map);
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage(Constants.SAVE_CONVERT_CLUE_FAILED);
        }
        return returnObject;
    }

}
