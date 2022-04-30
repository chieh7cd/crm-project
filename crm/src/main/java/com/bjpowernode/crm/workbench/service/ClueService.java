package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

/**
 * @Author: W
 * @Date: @Date: 2022-04-23 11:13
 * @Description:
 */
public interface ClueService {

    int saveCreateClue(Clue clue);

    List<Clue> queryClueByConditionForPage(Map<String, Object> map);

    int queryCountOfClueByCondition(Map<String, Object> map);

    Clue queryClueForDetailById(String id);

    void saveConvertClue(Map<String, Object> map);

}
