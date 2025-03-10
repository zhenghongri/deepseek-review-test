package com.example.deepseekreviewtest;

import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateInsertSqlMethod {


    public static void simpleRead() {
        String fileName = "EMP-DN-2025-03-10-未POD的列表.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, BatchDTO.class, new ExcelImportListener()).sheet().doRead();
    }

    public static void main(String[] args) {
        simpleRead();
    }
}
