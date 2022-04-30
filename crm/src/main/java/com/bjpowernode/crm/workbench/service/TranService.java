package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.FunnelVO;
import com.bjpowernode.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

/**
 * @Author: W
 * @Date: @Date: 2022-04-24 18:15
 * @Description:
 */
public interface TranService {

    List<Tran> queryTranByConditionForPage(Map<String, Object> map);

    int queryCountOfTranByCondition(Map<String, Object> map);

    void saveCreateTran(Map<String, Object> map);

    Tran queryTranForDetailById(String id);

    List<FunnelVO> queryCountOfTranGroupByStage();

}
