package com.pepper.controller.emap.util;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * @author Administrator
 *
 */
public class ExportExcelUtil {	
	
	public void export( Collection<?> dataSet, OutputStream outputStream,List<ExcelColumn> excelColumn) throws IOException, IllegalArgumentException, IllegalAccessException {
		if(dataSet == null) {
			return;
		}
		// 声明一个工作薄
		@SuppressWarnings("resource")
		XSSFWorkbook workbook = new XSSFWorkbook();
		// 生成一个表格
		XSSFSheet sheet = workbook.createSheet("sheet");
		XSSFRow row = sheet.createRow(0);
		for(int i =0 ; i<excelColumn.size();i++ ) {
			XSSFCell cell = row.createCell(i);
			cell.setCellValue(excelColumn.get(i).getName());
		}
		int rowIndex = 1;
		for(Object data : dataSet) {
			row = sheet.createRow(rowIndex);
			for(int i =0 ; i<excelColumn.size();i++ ) {
				String key = excelColumn.get(i).getKey();
				Boolean isNotField = excelColumn.get(i).getNotField();
				String defaultValue = excelColumn.get(i).getDefaultValue();
				String cellValue = "";
				if (Objects.nonNull(isNotField) && isNotField){
					cellValue = defaultValue;
				}else {
					cellValue = getCellValue(data,key);
				}
				XSSFCell cell = row.createCell(i);
				cell.setCellValue(cellValue);
			}
			++rowIndex;
		}
		workbook.write(outputStream);
	}
	
	
	private String getCellValue(Object data,String key) throws IllegalArgumentException, IllegalAccessException {
		if(data==null) {
			return "";
		}
		if(data instanceof Map||data instanceof HashMap) {
			return ((Map)data).get(key).toString();
		}
		List<Field> listField = new ArrayList<Field>();
		Class<?> c= data.getClass();
	    while (c!= null){
	    	listField.addAll(new ArrayList<>(Arrays.asList(c.getDeclaredFields())));
	        c= c.getSuperclass();
	    }
		for(Field field : listField) {
			String temp = key.indexOf(".")>0?key.split("\\.")[0]:key;
			if(field.getName().equals(temp)) {
				field.setAccessible(true);
				if(key.indexOf(".")>0) {
					return getCellValue(field.get(data),key.substring(key.indexOf(".")+1));
				 }
				 Object value = field.get(data);
				 return value ==null?"":value.toString();
			}
		}
		
		return "";
	}
}
