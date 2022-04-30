package com.bjpowernode.crm.commons.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: W
 * @Date: @Date: 2022-04-18 17:58
 * @Description: 对日期类型进行处理的工具类
 */
public class DateUtils {

    /**
     * 对指定的Date对象进行格式化: yyyy-MM-dd HH:mm:ss
     * @param date 要格式化的日期
     * @return 按指定格式解析后的日期字符串
     */
    public static String formatDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * 对指定的Date对象进行格式化: yyyy-MM-dd
     * @param date 要格式化的日期
     * @return 按指定格式解析后的日期字符串
     */
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    /**
     * 对指定的Date对象进行格式化: HH:mm:ss
     * @param date 要格式化的日期
     * @return 按指定格式解析后的日期字符串
     */
    public static String formatTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

}
