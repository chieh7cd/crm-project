package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.constant.Constants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityRemarkService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @Author: W
 * @Date: @Date: 2022-04-22 18:08
 * @Description:
 */
@Controller
public class ActivityRemarkController {

    @Resource
    private ActivityRemarkService activityRemarkService;

    @RequestMapping("/workbench/activity/saveCreateActivityRemark.do")
    @ResponseBody
    public Object saveCreateActivityRemark(ActivityRemark activityRemark, HttpSession session) {
        // 二次封装参数, 前台传过来的是id和noteContent
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        activityRemark.setId(UUIDUtils.getUUID());
        activityRemark.setCreateTime(DateUtils.formatDateTime(new Date()));
        activityRemark.setCreateBy(user.getId());
        activityRemark.setEditFlag(Constants.REMARK_EDIT_FLAG_NOT_EDITED);
        ReturnObject returnObject = new ReturnObject();
        try {
            // 调用service层方法保存创建的市场活动备注
            int count = activityRemarkService.saveCreateActivityRemark(activityRemark);
            if (count > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setMessage(Constants.SAVE_CREATE_ACTIVITY_REMARK_SUCCESS);
                returnObject.setRetData(activityRemark);
            } else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage(Constants.SAVE_CREATE_ACTIVITY_REMARK_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage(Constants.SAVE_CREATE_ACTIVITY_REMARK_FAILED);
        }

        return returnObject;
    }

    @RequestMapping("/workbench/activity/deleteActivityRemarkById.do")
    @ResponseBody
    public Object deleteActivityRemarkById(String id) {
        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法删除备注
            int count = activityRemarkService.deleteActivityRemarkById(id);
            if (count > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setMessage(Constants.DELETE_ACTIVITY_SUCCESS);
            } else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage(Constants.DELETE_ACTIVITY_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage(Constants.DELETE_ACTIVITY_FAILED);
        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/saveEditActivityRemark.do")
    @ResponseBody
    public Object saveEditActivityRemark(ActivityRemark remark, HttpSession session) {
        // 封装参数
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        remark.setEditTime(DateUtils.formatDateTime(new Date()));
        remark.setEditBy(user.getId());
        remark.setEditFlag(Constants.REMARK_EDIT_FLAG_HAS_EDITED);

        ReturnObject returnObject = new ReturnObject();
        try {
            // 调用service层方法，保存修改的市场活动备注
            int count = activityRemarkService.saveEditActivityRemark(remark);
            if (count > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setMessage(Constants.EDIT_ACTIVITY_REMARK_SUCCESS);
                returnObject.setRetData(remark);
            } else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage(Constants.EDIT_ACTIVITY_REMARK_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage(Constants.EDIT_ACTIVITY_REMARK_FAILED);
        }
        return returnObject;
    }

}
