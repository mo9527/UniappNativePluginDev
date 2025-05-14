package com.wanyi.plugins.utils;


import static org.apache.poi.ss.usermodel.CellType.FORMULA;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.wanyi.plugins.exception.BusinessException;
import com.wanyi.plugins.utils.text.StringUtils;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.*;

public class ExcelHelper {

    private static final String TAG = "ExcelHelper";

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
    }

    public static List<Map<String, Object>> readExcel(String filePath, Context context){
        String realPath = convertPath(context, filePath);

        File file = new File(realPath);

        if (file.exists()) {
            return readExcel(file);
        } else {
            Log.e(TAG, "文件不存在: " + realPath);
        }

        return new ArrayList<>();
    }

    public static List<Map<String, Object>> readExcel(File file){
        List<Map<String, Object>> resultList = new ArrayList<>();

        try {
            Log.i(TAG, "开始读取Excel文件");
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0){
                    Log.i(TAG, "读取Excel第一行标题，不处理");
                    continue;
                }
                Map<String, Object> rowMap = new LinkedHashMap<>();
                for (Cell cell : row) {
                    String colLetter = getColumnLetter(cell.getColumnIndex());
                    rowMap.put(colLetter, getCellValue(cell));
                }
                resultList.add(rowMap);
            }

        } catch (Exception e) {
            Log.e(TAG, "读取Excel文件出错", e);
            throw new BusinessException("读取Excel文件出错");
        }

        return resultList;
    }

    private static String getColumnLetter(int column) {
        StringBuilder sb = new StringBuilder();
        while (column >= 0) {
            sb.insert(0, (char) ('A' + column % 26));
            column = column / 26 - 1;
        }
        return sb.toString();
    }

    private static Object getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case BOOLEAN: return cell.getBooleanCellValue();
            case NUMERIC:
                return DateUtil.isCellDateFormatted(cell)
                        ? cell.getDateCellValue()
                        : cell.getNumericCellValue();
            case FORMULA: return cell.getCellFormula();
            case BLANK: return "";
            default: return "";
        }
    }

    private static String convertPath(Context context, String path) {
        if (path.startsWith("file://")) {
            return path.replace("file://", "");
        } else if (path.startsWith("content://")) {
            Uri uri = Uri.parse(path);
            return PathUtils.getPath(context, uri);
        } else {
            return path;
        }
    }

    public static void initProperties(){
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");
        System.setProperty("org.apache.poi.ss.ignoreMissingFontSystem", "true");
    }
}
