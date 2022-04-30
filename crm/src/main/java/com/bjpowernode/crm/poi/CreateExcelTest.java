package com.bjpowernode.crm.poi;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Author: W
 * @Date: @Date: 2022-04-21 09:28
 * @Description: 使用apache-poi生成excel文件
 */
public class CreateExcelTest {

    public static void main(String[] args) throws IOException {
        // 创建HSSFWorkbook对象，对应一个excel文件
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 使用workbook创建HSSFSheet对象，对应一页
        HSSFSheet sheet = workbook.createSheet();
        // 使用sheet创建HSSFRow对象，对应这一页的一行, 参数是行号，从0开始
        HSSFRow row = sheet.createRow(0);
        // 使用row对象创建HSSFCell对象，对应这一行中的列, 参数是列号，从0开始
        HSSFCell cell = row.createCell(0);
        // 往这一列中写内容
        cell.setCellValue("学号");
        cell = row.createCell(1);
        cell.setCellValue("姓名");
        cell = row.createCell(2);
        cell.setCellValue("年龄");

        // 生成HSSFCellStyle样式对象
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        // 使用sheet创建 10个HSSFRow对象，对应sheet中的10行
        for (int i = 1; i <= 10; i++) {
            row = sheet.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(100 + i);
            cell = row.createCell(1);
            cell.setCellValue("NAME" + i);
            cell = row.createCell(2);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(20 + i);
        }
//        D:\dev\code\IdeaProjects\crm-project\crm\src\main\webapp\WEB-INF\excelfiles
        // 调用工具函数生成excecl文件
        FileOutputStream fos = new FileOutputStream("D:\\dev\\code\\IdeaProjects\\crm-project\\crm\\src\\main\\webapp\\WEB-INF\\excelfiles\\studentList.xls");
        workbook.write(fos);

        fos.close();
        workbook.close();

        System.out.println("==========create success==========");
    }

}
