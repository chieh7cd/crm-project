package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.commons.constant.Constants;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.mapper.*;
import com.bjpowernode.crm.workbench.service.ClueService;
import org.springframework.stereotype.Service;
import sun.plugin.util.UIUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: W
 * @Date: @Date: 2022-04-23 11:14
 * @Description:
 */
@Service("clueService")
public class ClueServiceImpl implements ClueService {

    @Resource
    private ClueMapper clueMapper;

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private ContactsMapper contactsMapper;

    @Resource
    private ClueRemarkMapper clueRemarkMapper;

    @Resource
    private CustomerRemarkMapper customerRemarkMapper;

    @Resource
    private ContactsRemarkMapper contactsRemarkMapper;

    @Resource
    private ClueActivityRelationMapper clueActivityRelationMapper;

    @Resource
    private ContactsActivityRelationMapper contactsActivityRelationMapper;

    @Resource
    private TranMapper tranMapper;

    @Resource
    private TranRemarkMapper tranRemarkMapper;

    @Override
    public int saveCreateClue(Clue clue) {
        return clueMapper.insertClue(clue);
    }

    @Override
    public List<Clue> queryClueByConditionForPage(Map<String, Object> map) {
        return clueMapper.selectClueByConditionForPage(map);
    }

    @Override
    public int queryCountOfClueByCondition(Map<String, Object> map) {
        return clueMapper.selectCountOfClueByCondition(map);
    }

    @Override
    public Clue queryClueForDetailById(String id) {
        return clueMapper.selectClueForDetailById(id);
    }

    @Override
    public void saveConvertClue(Map<String, Object> map) {
        // 1.根据id查询线索的信息
        String clueId = (String) map.get("clueId");
        User user = (User) map.get(Constants.SESSION_USER);
        Clue clue = clueMapper.selectClueById(clueId);
        // 2.把该线索中有关公司的信息转换到客户表中
        Customer customer = new Customer();
        customer.setAddress(clue.getAddress());
        customer.setContactSummary(clue.getContactSummary());
        customer.setCreateBy(user.getId());
        customer.setCreateTime(DateUtils.formatDateTime(new Date()));
        customer.setDescription(clue.getDescription());
        customer.setId(UUIDUtils.getUUID());
        customer.setName(clue.getCompany());
        customer.setNextContactTime(clue.getNextContactTime());
        customer.setOwner(user.getId());
        customer.setPhone(clue.getPhone());
        customer.setWebsite(clue.getWebsite());
        customerMapper.insertCustomer(customer);
        // 3.根据该线索中有关个人的信息转换到联系人表中
        Contacts contacts = new Contacts();
        contacts.setAddress(clue.getAddress());
        contacts.setAppellation(clue.getAppellation());
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setCreateBy(user.getId());
        contacts.setCreateTime(DateUtils.formatDateTime(new Date()));
        contacts.setCustomerId(clue.getId());
        contacts.setDescription(clue.getDescription());
        contacts.setEmail(clue.getEmail());
        contacts.setFullname(clue.getFullname());
        contacts.setId(UUIDUtils.getUUID());
        contacts.setJob(clue.getJob());
        contacts.setMphone(clue.getMphone());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setOwner(user.getId());
        contacts.setSource(clue.getSource());
        contactsMapper.insertContacts(contacts);
        // 4.根据clueId查询该线索下的所有的备注
        List<ClueRemark> clueRemarkList = clueRemarkMapper.selectClueRemarkByClueId(clueId);
        // 如果该线索中有备注，5.把该线索下所有的备注转换到客户备注表中一份, 6.把该线索下所有的备注转换到联系人备注表中一份
        if (clueRemarkList != null && clueRemarkList.size() > 0) {
            CustomerRemark customerRemark = null;
            ContactsRemark contactsRemark = null;
            List<CustomerRemark> customerRemarkList = new ArrayList<>();
            List<ContactsRemark> contactsRemarkList = new ArrayList<>();
            for (ClueRemark clueRemark : clueRemarkList) {
                // 封装客户备注信息
                customerRemark = new CustomerRemark();
                customerRemark.setCreateBy(clueRemark.getCreateBy());
                customerRemark.setCreateTime(clueRemark.getCreateTime());
                customerRemark.setCustomerId(customer.getId());
                customerRemark.setEditBy(clueRemark.getEditBy());
                customerRemark.setEditFlag(clueRemark.getEditFlag());
                customerRemark.setEditTime(clueRemark.getEditTime());
                customerRemark.setId(UUIDUtils.getUUID());
                customerRemark.setNoteContent(clueRemark.getNoteContent());
                customerRemarkList.add(customerRemark);

                // 封装联系人备注信息
                contactsRemark = new ContactsRemark();
                contactsRemark.setContactsId(contacts.getId());
                contactsRemark.setCreateBy(clueRemark.getCreateBy());
                contactsRemark.setCreateTime(clueRemark.getCreateTime());
                contactsRemark.setEditBy(clueRemark.getEditBy());
                contactsRemark.setEditFlag(clueRemark.getEditFlag());
                contactsRemark.setEditTime(clueRemark.getEditTime());
                contactsRemark.setId(UUIDUtils.getUUID());
                contactsRemark.setNoteContent(clueRemark.getNoteContent());
                contactsRemarkList.add(contactsRemark);
            }
            // 把该线索下所有的备注转换到客户备注表中一份
            customerRemarkMapper.insertCustomerRemarkByList(customerRemarkList);
            // 把该线索下所有的备注转换到联系人备注表中一份
            contactsRemarkMapper.insertContactsRemarkByList(contactsRemarkList);
        }
        // 7.跟据clueId查询该线索和市场活动的关联关系
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationMapper.selectClueActivityRelationByClueId(clueId);
        // 8.把该线索和市场活动的关联关系转换到联系人和市场活动的关联关系表中
        if (clueActivityRelationList != null && clueActivityRelationList.size() > 0) {
            ContactsActivityRelation contactsActivityRelation = null;
            List<ContactsActivityRelation> contactsActivityRelationList = new ArrayList<>();
            for (ClueActivityRelation clueActivityRelation : clueActivityRelationList) {
                contactsActivityRelation = new ContactsActivityRelation();
                contactsActivityRelation.setActivityId(clueActivityRelation.getActivityId());
                contactsActivityRelation.setContactsId(contacts.getId());
                contactsActivityRelation.setId(UUIDUtils.getUUID());
                contactsActivityRelationList.add(contactsActivityRelation);
            }
            // 把该线索和市场活动的关联关系转换到联系人和市场活动的关联关系表中
            contactsActivityRelationMapper.insertContactsActivityRelationByList(contactsActivityRelationList);
        }
        // 如果需要创建交易，9.则往交易表中添加一条记录
        String isCreateTran = (String) map.get("isCreateTran");
        if ("true".equals(isCreateTran)) {
            Tran tran = new Tran();
            tran.setActivityId((String) map.get("activityId"));
            tran.setContactsId(contacts.getId());
            tran.setCreateBy(user.getId());
            tran.setCreateTime(DateUtils.formatDateTime(new Date()));
            tran.setCustomerId(customer.getId());
            tran.setExpectedDate((String) map.get("expectedDate"));
            tran.setId(UUIDUtils.getUUID());
            tran.setMoney((String) map.get("money"));
            tran.setName((String) map.get("name"));
            tran.setOwner(user.getId());
            tran.setStage((String) map.get("stage"));
            // 往交易表中添加一条记录
            tranMapper.insertTran(tran);

            // 10.如果需要创建交易，则还需把该线索下所有的备注转换到交易备注表中一份
            if (clueRemarkList != null && clueRemarkList.size() > 0) {
                TranRemark tranRemark = null;
                List<TranRemark> tranRemarkList = new ArrayList<>();
                for (ClueRemark clueRemark : clueRemarkList) {
                    tranRemark = new TranRemark();
                    tranRemark.setCreateBy(clueRemark.getCreateBy());
                    tranRemark.setCreateTime(clueRemark.getCreateTime());
                    tranRemark.setEditBy(clueRemark.getEditBy());
                    tranRemark.setEditFlag(clueRemark.getEditFlag());
                    tranRemark.setEditTime(clueRemark.getEditTime());
                    tranRemark.setId(UUIDUtils.getUUID());
                    tranRemark.setNoteContent(clueRemark.getNoteContent());
                    tranRemark.setTranId(tran.getId());
                    tranRemarkList.add(tranRemark);
                }
                // 把该线索下所有的备注转换到交易备注表中一份
                tranRemarkMapper.insertTranRemarkByList(tranRemarkList);
            }
        }
        // 11.删除该线索下的所有备注
        clueRemarkMapper.deleteClueRemarkByClueId(clueId);
        // 12.删除该线索和市场活动的关联关系
        clueActivityRelationMapper.deleteClueActivityRelationByClueId(clueId);
        // 13.删除该线索
        clueMapper.deleteClueById(clueId);
    }


}
