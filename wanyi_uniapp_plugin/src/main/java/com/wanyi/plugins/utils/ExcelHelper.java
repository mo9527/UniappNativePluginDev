package com.wanyi.plugins.utils;

import static org.apache.poi.ss.usermodel.CellType.BLANK;
import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.wanyi.plugins.exception.BusinessException;
import com.wanyi.plugins.utils.text.StringUtils;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ExcelHelper {

    private static final String TAG = "ExcelHelper";

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static List<Map<String, Object>> readExcel(String filePath, Context context){
        try {
            String realPath = convertPath(context, filePath);

            File file = new File(realPath);

            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                return readExcel(fis);
            } else {
                Log.e(TAG, "文件不存在: " + realPath);
            }
        }catch (FileNotFoundException e){
            Log.e(TAG, "读取Excel文件出错, filePath: " + filePath, e);
            throw new BusinessException("无法读取文件内容");
        }

        return Lists.newArrayList();
    }

    public static List<Map<String, Object>> readExcel(InputStream in){
        Workbook workbook = null;
        List<Map<String, Object>> rowList = new ArrayList<>();
        try {
            workbook = new XSSFWorkbook(in);

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Map<String, Object> rowMap = new TreeMap<>();
                for (Cell cell : row) {
                    Object cellValue = null;
                    CellType cellType = cell.getCellType();
                    String columnLetter = CellReference.convertNumToColString(cell.getColumnIndex());
                    if (cellType == STRING || cellType == BLANK){
                        cellValue = cell.getStringCellValue();
                    }else if (cellType == NUMERIC){
                        if (DateUtil.isCellDateFormatted(cell)){
                            cellValue = dateFormat.format(cell.getDateCellValue());
                        }else {
                            cellValue = ((XSSFCell) cell).getRawValue();
                        }

                    }else if (cellType == BOOLEAN){
                        cellValue = cell.getBooleanCellValue();
                    }else if (cellType == FORMULA){
                        cellValue = getFormulaValue(cell, workbook);
                    }else {
                        Log.e(TAG, StringUtils.format("Unknown CellType: {}, row: {}, column: {}", cellType, row.getRowNum(), columnLetter));
                        cellValue = cell.getStringCellValue();
                    }

                    if (!ObjectUtils.isEmpty(cellValue)){
                        if (cellValue instanceof String){
                            cellValue = ((String) cellValue).trim().replaceAll("[\\t\\n]", "");
                        }
                        rowMap.put(columnLetter, cellValue);
                    }
                }
                if (MapUtils.isNotEmpty(rowMap)){
                    rowList.add(rowMap);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            IOUtils.closeQuietly(workbook);
        }
        return rowList;
    }

    private static Object getFormulaValue(Cell cell, Workbook workbook){
        Object r = null;
        if (cell.getCellType() == FORMULA) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            CellValue cellValue = evaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case STRING:
                    r = cellValue.getStringValue();
                    break;
                case NUMERIC:
                    r = cellValue.getNumberValue();
                    break;
                case BOOLEAN:
                    r = cellValue.getBooleanValue();
                    break;
                default:
                    Log.e(TAG, "Formula value is unknown");
                    break;
            }
        }
        return r;
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
}
