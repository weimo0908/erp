package cn.kumiaojie.erp.biz.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import cn.kumiaojie.erp.biz.IDepBiz;
import cn.kumiaojie.erp.dao.IDepDao;
import cn.kumiaojie.erp.entity.Dep;
import cn.kumiaojie.erp.entity.Emp;
import net.sf.jxls.transformer.XLSTransformer;

/**
 * DepBiz实现类
 * 
 * @author Stivechen
 *
 */
public class DepBiz extends BaseBiz<Dep> implements IDepBiz {

	// 注入IDepDao
	private IDepDao depDao;

	public void setDepDao(IDepDao depDao) {
		this.depDao = depDao;
		//继承父类的setBaseDao,并注入dep,让其具体的方法有具体的对象
		super.setBaseDao(this.depDao);
	}
	
	
	public void doExport(OutputStream os,Dep t1,String userName ){
		List<Dep> depList = depDao.getList(t1, null, null);
		//通过创建部门名称集合减少数据库查询次数
		Map<Long,String> depNameMap = new HashMap<Long,String>();
		for (Dep dep : depList) {
			depNameMap.put(dep.getUuid(), dep.getName());
		}
		//显示数据
		Map<String,Object> dataMap = new HashMap<String,Object>();
		//日期格式
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		//导出当前时间和导出人
		Date date = new Date();
		String dateNow = sdf.format(date);
		dataMap.put("date", dateNow);		
		dataMap.put("userName", userName);
		//将查询到的部门添加到数据
		dataMap.put("dep",depList);
		//将部门名称集合添加到数据中
		dataMap.put("depName",depNameMap);
		HSSFWorkbook wk = null;		 
        try {
            wk = new HSSFWorkbook(new ClassPathResource("export_dep.xls").getInputStream());
            XLSTransformer transformer = new XLSTransformer();	           
            transformer.transformWorkbook(wk, dataMap);
            wk.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(null != wk){
                try {
                    wk.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
	
	}
	
	public void doImport(InputStream is) throws IOException{
		HSSFWorkbook wb =null;
		try {
			wb = new HSSFWorkbook(is);
			HSSFSheet sheet = wb.getSheetAt(0);
			//读取数据
			//读取最后一行行号
			int lastRow = sheet.getLastRowNum();
			Dep dep = null;
			for (int i = 2; i <= lastRow; i++) {			
				dep = new Dep();
				//通过部门名称来判断员工是否存在
				dep.setName(sheet.getRow(i).getCell(1).getStringCellValue());				
				List<Dep> list = depDao.getList(null,dep,null);
				if (list.size()>0) {
					dep = list.get(0);
				}
				dep.setName(sheet.getRow(i).getCell(1).getStringCellValue());
				//根据格式选择不同方法
				try {
					dep.setTele(sheet.getRow(i).getCell(2).getStringCellValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					dep.setTele(Math.round(sheet.getRow(i).getCell(2).getNumericCellValue())+"");
					
				}								
				//如果用户不存在则添加
				if (list.size() == 0) {
					depDao.add(dep);
				}
			}		
		}finally{
			if(null != wb){
				try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

}
