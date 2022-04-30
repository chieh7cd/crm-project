package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.ClueRemark;

import java.util.List;

/**
 * @Author: W
 * @Date: @Date: 2022-04-23 18:24
 * @Description:
 */
public interface ClueRemarkService {

    List<ClueRemark> queryClueRemarkForDetailByClueId(String clueId);

}
