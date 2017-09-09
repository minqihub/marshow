package com.framework.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.framework.utils.Json;
import com.framework.utils.SysLog;

import com.framework.utils.PropertiesReader;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * 处理Excel表格类
 * @author minqi 2016-11-21
 *
 */
public class ExcelUtils extends FileUtils {
	
	private static PropertiesReader property = PropertiesReader.getInstance();
	

	/**
	 * 读取模板Excel文件
	 * @param file
	 * @return Json格式的List
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List readTemplateExcel(File file){
		if(!file.exists()){
			return new ArrayList();
		}
		String excelName = file.getName();
		String KEYS = PropertiesReader.getInstance().getProperty(excelName);			//通过文件名再配置文件中找对应的keys[]
		String[] keys = KEYS.split(",");
		
		int rowBegin = Integer.parseInt(property.getProperty(excelName + "RowBegin"));
		int columnsBegin = Integer.parseInt(property.getProperty(excelName + "ColumnsBegin"));
		
		Workbook workbook = null;
		List list = new ArrayList();
		try{
           InputStream in = new FileInputStream(file);            						//直接从本地文件创建Workbook
           workbook = Workbook.getWorkbook(in);        									//构建Workbook对象, 只读Workbook对象
            
           Sheet sheet = workbook.getSheet(0);               							//获取第一张Sheet表,Sheet的下标是从0开始
           //TODO对sheet表，动态获取
           
           int columns = sheet.getColumns();               								//获取Sheet表中所包含的总列数
           int rows = sheet.getRows();               									//获取Sheet表中所包含的总行数
            
           for(int i = rowBegin-1; i < rows; i++){
        	   JSONObject jsonTemp = new JSONObject();
               for(int j = columnsBegin-1; j < columns; j++){
            	   Cell cell = sheet.getCell(j, i);										//获取指定单元格的对象引用
            	   jsonTemp.put(keys[j - columnsBegin + 1], cell.getContents());		//不论从第几列开始，keys[]都从0开始
                }
                list.add(jsonTemp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("读取模板Excel文件，异常");
        } finally {
        	if(workbook != null) workbook.close();
        }
     	return list;
	}
	
	/**
	 * 导出Excel
	 * @param json [{"id":"123","name":"张三"},{"id":"123","name":"张三"}....]
	 * @param headMap 定义表头，表头的key要和List里每个元素的key对应，例：{"id":"编号","name":"姓名"}
	 * @throws IOException 
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void exportExcel(List list, Map headMap) throws IOException, RowsExceededException, WriteException{
		
		OutputStream outPut = null;
		try {
			outPut = new FileOutputStream("E:/测试导出.xls");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("找不到导出路径");
		}
		
        //利用已经创建的Excel工作薄,创建新的可写入的Excel工作薄
        WritableWorkbook workbook = null;
		try {
			workbook = Workbook.createWorkbook(outPut);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("创建Excel工作簿IO异常");
		}   

        //创建新的一页
        WritableSheet sheet = workbook.createSheet("sheet1", 0);

        //定义第一行，表头：
		List keysAry = new ArrayList();
		int columnsIndex = 0;
		for (Iterator it = headMap.keySet().iterator(); it.hasNext();) {
            String tempKey = it.next().toString();
            keysAry.add(tempKey);

            //创建单元格，第一个参数为列坐标，第二个参数为行坐标，第三个参数为内容
            Label labelHead = new Label(columnsIndex, 0, headMap.get(tempKey).toString());
            sheet.addCell(labelHead);
            columnsIndex ++;
		}
        
		//写入正文表格内容：
		for (int i = 0; i < list.size(); i++) {
			JSONObject row = Json.toJO(list.get(i));
			
			for (int j = 0; j < keysAry.size(); j++) {
				Label labelContent = new Label(new Label(j, i+1, row.get(keysAry.get(j)).toString()));
				sheet.addCell(labelContent);
			}
		}

        //把创建的内容写入到输出流中，并关闭输出流
		if(workbook != null){
	        workbook.write();
	        workbook.close();
		}
		if(outPut != null){
	        outPut.close();
		}

        SysLog.log(ExcelUtils.class, "Excel导出完成！");
		
	}
	
	
	
	public static void main(String[] args) {
		List list = new ArrayList();
		
		for (int i = 0; i < 11; i++) {
			Map row = new HashMap();
			row.put("name", "张三"+i);
			row.put("age", i);
			row.put("height", 170+i);
			list.add(row);
		}
		
		Map headMap = new HashMap();
		headMap.put("name", "姓名");
		headMap.put("age", "年龄");
		headMap.put("height", "身高");
		
		
		try {
			ExcelUtils.exportExcel(list, headMap);
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
