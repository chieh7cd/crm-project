package com.bjpowernode.crm.settings.service;

import com.bjpowernode.crm.settings.domain.DicValue;

import java.util.List;

/**
 * @Author: W
 * @Date: @Date: 2022-04-23 10:30
 * @Description:
 */
public interface DicValueService {

    List<DicValue> queryDicValueByTypeCode(String typeCode);

}
