package com.bjpowernode.crm.commons.utils;

import java.util.UUID;

/**
 * @Author: W
 * @Date: @Date: 2022-04-19 16:30
 * @Description: 生成32位不重复的id值
 */
public class UUIDUtils {

    /**
     * @return 生成32位不重复的id值
     */
    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

}
