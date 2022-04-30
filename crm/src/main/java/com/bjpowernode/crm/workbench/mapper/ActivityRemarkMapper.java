package com.bjpowernode.crm.workbench.mapper;

import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.domain.ClueRemark;

import java.util.List;

public interface ActivityRemarkMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity_remark
     *
     * @mbggenerated Fri Apr 22 16:46:36 CST 2022
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity_remark
     *
     * @mbggenerated Fri Apr 22 16:46:36 CST 2022
     */
    int insert(ActivityRemark record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity_remark
     *
     * @mbggenerated Fri Apr 22 16:46:36 CST 2022
     */
    int insertSelective(ActivityRemark record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity_remark
     *
     * @mbggenerated Fri Apr 22 16:46:36 CST 2022
     */
    ActivityRemark selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity_remark
     *
     * @mbggenerated Fri Apr 22 16:46:36 CST 2022
     */
    int updateByPrimaryKeySelective(ActivityRemark record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity_remark
     *
     * @mbggenerated Fri Apr 22 16:46:36 CST 2022
     */
    int updateByPrimaryKey(ActivityRemark record);


    /**
     * 根据activityId查询该市场活动下所有市场活动的备注信息
     * @param activityId 市场活动id
     */
    List<ActivityRemark> selectActivityRemarkForDetailByActivityId(String activityId);

    /**
     * 保存创建的市场活动备注
     */
    int insertActivityRemark(ActivityRemark activityRemark);


    /**
     * 根据id删除市场活动备注信息
     */
    int deleteActivityRemarkById(String id);

    /**
     * 保存修改的市场活动备注
     */
    int updateActivityRemark(ActivityRemark remark);




}