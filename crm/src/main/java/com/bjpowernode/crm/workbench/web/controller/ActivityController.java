package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.constant.Constants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.HSSFUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityRemarkService;
import com.bjpowernode.crm.workbench.service.ActivityService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

/**
 * @Author: W
 * @Date: @Date: 2022-04-19 10:24
 * @Description:
 */
@Controller
public class ActivityController {

    @Resource
    private UserService userService;

    @Resource
    private ActivityService activityService;

    @Resource
    private ActivityRemarkService activityRemarkService;

    @RequestMapping("/workbench/activity/index.do")
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView();
        // 调用service层, 查询所有用户
        List<User> userList = userService.queryAllUsers();
        // 封装到request作用域
        mv.addObject("userList", userList);
        mv.setViewName("workbench/activity/index");
        return mv;
    }

    @RequestMapping("/workbench/activity/saveCreateActivity.do")
    @ResponseBody
    public Object saveCreateActivity(HttpSession session, Activity activity) {
        // 还需要封装id,create_time, create_by
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        activity.setId(UUIDUtils.getUUID());
        activity.setCreateTime(DateUtils.formatDateTime(new Date()));
        activity.setCreateBy(user.getId());
        // 调用activityService, 保存市场创建的活动
        ReturnObject returnObject = new ReturnObject();
        try {
            int count = activityService.saveCreateActivity(activity);
            if (count > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setMessage(Constants.SAVE_CREATE_ACTIVITY_SUCCESS);
            } else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage(Constants.SAVE_CREATE_ACTIVITY_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage(Constants.SAVE_CREATE_ACTIVITY_FAILED);
        }

        return returnObject;
    }

    @RequestMapping("/workbench/activity/queryActivityByConditionForPage.do")
    @ResponseBody
    public Object queryActivityByConditionForPage(String name, String owner, String startDate,
                                                  String endDate, int pageNo, int pageSize) {
        // 封装参数到map中
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("owner", owner);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("beginNo", (pageNo - 1) * pageSize);
        map.put("pageSize", pageSize);
        // 调用service方法查询数据
        List<Activity> activityList = activityService.queryActivityByConditionForPage(map);
        int totalRows = activityService.queryCountOfActivityByCondition(map);
        // 查询到的数据放到map中，响应到页面
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("activityList", activityList);
        resultMap.put("totalRows", totalRows);
        return resultMap;
    }


    @RequestMapping("/workbench/activity/deleteActivityByIds.do")
    @ResponseBody
    public Object deleteActivityByIds(String[] id) {
        ReturnObject returnObject = new ReturnObject();
        try {
            // 调用service层方法删除数据
            int count = activityService.deleteActivityByIds(id);
            if (count > 0) {
                // 删除成功
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setMessage(Constants.DELETE_ACTIVITY_SUCCESS);
            } else {
                // 删除失败
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage(Constants.DELETE_ACTIVITY_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 删除失败
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage(Constants.DELETE_ACTIVITY_FAILED);
        }
        return returnObject;
    }


    @RequestMapping("/workbench/activity/queryActivityById.do")
    @ResponseBody
    public Object queryActivityById(String id) {
        // 调用service查询市场活动
        return activityService.queryActivityById(id);
    }

    @RequestMapping("/workbench/activity/saveEditActivity.do")
    @ResponseBody
    public Object saveEditActivity(HttpSession session, Activity activity) {
        // 封装参数
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        activity.setEditTime(DateUtils.formatDateTime(new Date()));
        activity.setEditBy(user.getId());
        ReturnObject returnObject = new ReturnObject();
        try {
            // 调用service方法修改市场活动信息
            int count = activityService.saveEditActivity(activity);
            if (count > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setMessage(Constants.UPDATE_ACTIVITY_SUCCESS);
            } else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage(Constants.UPDATE_ACTIVITY_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage(Constants.UPDATE_ACTIVITY_FAILED);
        }

        return returnObject;
    }

    @RequestMapping("/workbench/activity/fileDownload.do")
    public void fileDownload(HttpServletResponse response) throws Exception {
        //1.设置响应类型
        response.setContentType("application/octet-stream;charset=UTF-8");
        //2.获取输出流
        OutputStream out = response.getOutputStream();

        //浏览器接收到响应信息之后，默认情况下，直接在显示窗口中打开响应信息；即使打不开，也会调用应用程序打开；只有实在打不开，才会激活文件下载窗口。
        //可以设置响应头信息，使浏览器接收到响应信息之后，直接激活文件下载窗口，即使能打开也不打开
        response.addHeader("Content-Disposition", "attachment;filename=mystudentList.xls");

        //读取excel文件(InputStream)，把输出到浏览器(OutoutStream)
        InputStream is = new FileInputStream("D:\\dev\\code\\IdeaProjects\\crm-project\\crm\\src\\main\\webapp\\WEB-INF\\excelfiles\\studentList.xls");
        byte[] buff = new byte[256];
        int len = 0;
        while ((len = is.read(buff)) != -1) {
            out.write(buff, 0, len);
        }

        //关闭资源
        is.close();
        out.flush();
    }

    @RequestMapping("/workbench/activity/exportAllActivities.do")
    public void exportAllActivities(HttpServletResponse response) throws Exception {
        // 调用service层方法，查询所有市场活动
        List<Activity> activityList = activityService.queryAllActivities();
        // 创建excel文件
        HSSFWorkbook workbook = new HSSFWorkbook();
        if (activityList != null && activityList.size() > 0) {
            createExcelAndOut2Browser(workbook, activityList, response);
        }
        workbook.close();
    }

    @RequestMapping("/workbench/activity/exportSelectedActivities.do")
    public void exportSelectedActivities(String[] ids, HttpServletResponse response)
            throws Exception {
        // 调用service层方法，根据id数组查询市场活动
        List<Activity> activityList = activityService.querySelectedActivities(ids);
        // 创建excel文件
        HSSFWorkbook workbook = new HSSFWorkbook();
        if (activityList != null && activityList.size() > 0) {
            createExcelAndOut2Browser(workbook, activityList, response);
        }
        workbook.close();
    }


    public void createExcelAndOut2Browser(HSSFWorkbook workbook, List<Activity> activityList,
                                          HttpServletResponse response) throws Exception {
        HSSFSheet sheet = workbook.createSheet("市场活动列表");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("所有者");
        cell = row.createCell(2);
        cell.setCellValue("名称");
        cell = row.createCell(3);
        cell.setCellValue("开始日期");
        cell = row.createCell(4);
        cell.setCellValue("结束日期");
        cell = row.createCell(5);
        cell.setCellValue("成本");
        cell = row.createCell(6);
        cell.setCellValue("描述");
        cell = row.createCell(7);
        cell.setCellValue("创建时间");
        cell = row.createCell(8);
        cell.setCellValue("创建者");
        cell = row.createCell(9);
        cell.setCellValue("修改时间");
        cell = row.createCell(10);
        cell.setCellValue("修改者");

        //遍历activityList，创建HSSFRow对象，生成所有的数据行
        Activity activity = null;
        for (int i = 0; i < activityList.size(); i++) {
            activity = activityList.get(i);
            //每遍历出一个activity，生成一行
            row = sheet.createRow(i + 1);
            //每一行创建11列，每一列的数据从activity中获取
            cell = row.createCell(0);
            cell.setCellValue(activity.getId());
            cell = row.createCell(1);
            cell.setCellValue(activity.getOwner());
            cell = row.createCell(2);
            cell.setCellValue(activity.getName());
            cell = row.createCell(3);
            cell.setCellValue(activity.getStartDate());
            cell = row.createCell(4);
            cell.setCellValue(activity.getEndDate());
            cell = row.createCell(5);
            cell.setCellValue(activity.getCost());
            cell = row.createCell(6);
            cell.setCellValue(activity.getDescription());
            cell = row.createCell(7);
            cell.setCellValue(activity.getCreateTime());
            cell = row.createCell(8);
            cell.setCellValue(activity.getCreateBy());
            cell = row.createCell(9);
            cell.setCellValue(activity.getEditTime());
            cell = row.createCell(10);
            cell.setCellValue(activity.getEditBy());
        }

        //根据wb对象生成excel文件
       /* OutputStream os=new FileOutputStream("D:\\course\\18-CRM\\阶段资料\\serverDir\\activityList.xls");
        wb.write(os);*/
        //关闭资源
        /*os.close();
        wb.close();*/

        //把生成的excel文件下载到客户端
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename=activityList.xls");
        OutputStream out = response.getOutputStream();
        /*InputStream is=new FileInputStream("D:\\course\\18-CRM\\阶段资料\\serverDir\\activityList.xls");
        byte[] buff=new byte[256];
        int len=0;
        while((len=is.read(buff))!=-1){
            out.write(buff,0,len);
        }
        is.close();*/

        workbook.write(out);
        out.flush();
    }


//    workbench/activity/fileUpload.do

    /**
     * 配置springmvc的文件上传解析器
     */
    @RequestMapping("/workbench/activity/fileUpload.do")
    @ResponseBody
    public Object fileUpload(String userName, MultipartFile myFile) throws Exception {
        //把文本数据打印到控制台
        System.out.println("userName=" + userName);
        //把文件在服务器指定的目录中生成一个同样的文件
        String originalFilename = myFile.getOriginalFilename();
        File file = new File(
                "D:\\dev\\code\\IdeaProjects\\crm-project\\crm\\src\\main\\webapp\\WEB-INF\\excelfiles\\",
                originalFilename);//路径必须手动创建好，文件如果不存在，会自动创建
        myFile.transferTo(file);

        //返回响应信息
        ReturnObject returnObject = new ReturnObject();
        returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
        returnObject.setMessage(Constants.UPLOAD_FILE_SUCCESS);
        return returnObject;
    }

    /**
     * 批量导入市场活动，需要配置springmvc文件上传解析器
     */
    @RequestMapping("/workbench/activity/importActivity.do")
    @ResponseBody
    public Object importActivity(MultipartFile activityFile, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        ReturnObject returnObject = new ReturnObject();
        List<Activity> activityList = new ArrayList<>();
        // 把接收到的excel文件写到磁盘目录中
        try {
            /*String originalFilename = activityFile.getOriginalFilename();
            File file = new File(
                    "D:\\dev\\code\\IdeaProjects\\crm-project\\crm\\src\\main\\webapp\\WEB-INF\\excelfiles\\",
                    originalFilename);//路径必须手动创建好，文件如果不存在，会自动创建*/
           /* activityFile.transferTo(file);
            // 解析excel文件，获取文件中的数据，并且封装到activityList
            InputStream is = new FileInputStream(file);*/

            InputStream is = activityFile.getInputStream();
            HSSFWorkbook workbook = new HSSFWorkbook(is);
            HSSFSheet sheet = workbook.getSheetAt(0);
            HSSFRow row = null;
            HSSFCell cell = null;
            Activity activity = null;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                row = sheet.getRow(i);
                activity = new Activity();
                activity.setId(UUIDUtils.getUUID());
                activity.setOwner(user.getId());
                activity.setCreateTime(DateUtils.formatDateTime(new Date()));
                activity.setCreateBy(user.getId());
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    cell = row.getCell(j);
                    String cellValue = HSSFUtils.getCellValueForStr(cell);
                    if (j == 0) {
                        activity.setName(cellValue);
                    } else if (j == 1) {
                        activity.setStartDate(cellValue);
                    } else if (j == 2) {
                        activity.setEndDate(cellValue);
                    } else if (j == 3) {
                        activity.setCost(cellValue);
                    } else if (j == 4) {
                        activity.setDescription(cellValue);
                    }
                }
                activityList.add(activity);
            }

            //调用service层方法，保存市场活动
            int ret = activityService.saveCreateActivityByList(activityList);
            if (ret > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setRetData(ret);
            } else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage(Constants.IMPORT_FILE_FAILED);
            }
        } catch (IOException e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage(Constants.IMPORT_FILE_FAILED);
        }

        return returnObject;
    }


    /**
     * 查看市场活动明细
     */
    @RequestMapping("/workbench/activity/detailActivity.do")
    public String detailActivity(String id, HttpServletRequest request) {
        // 调用service层方法，查询数据
        Activity activity = activityService.queryActivityForDetailById(id);
        List<ActivityRemark> remarkList = activityRemarkService.queryActivityRemarkForDetailByActivityId(id);
        // 把数据保存到request作用域中
        request.setAttribute("activity", activity);
        request.setAttribute("remarkList", remarkList);
        // 跳转到detail页面,请求转发
        return "workbench/activity/detail";
    }


}
