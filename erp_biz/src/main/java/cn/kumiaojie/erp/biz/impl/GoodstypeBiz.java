package cn.kumiaojie.erp.biz.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.springframework.core.io.ClassPathResource;
import cn.kumiaojie.erp.biz.IGoodstypeBiz;
import cn.kumiaojie.erp.dao.IGoodstypeDao;
import cn.kumiaojie.erp.entity.Emp;
import cn.kumiaojie.erp.entity.Goodstype;
import cn.kumiaojie.erp.exception.ERPException;
import net.sf.jxls.transformer.XLSTransformer;

/**
 * GoodsBiz实现类
 * 
 * @author Stivechen
 *
 */
public class GoodstypeBiz extends BaseBiz<Goodstype> implements IGoodstypeBiz {

	// 注入IGoodsDao
	private IGoodstypeDao goodstypeDao;
	public void setGoodstypeDao(IGoodstypeDao goodstypeDao) {
		this.goodstypeDao = goodstypeDao;
		//继承父类的setBaseDao,并注入goodstype,让其具体的方法有具体的对象
		super.setBaseDao(this.goodstypeDao);
	}
	
	/**
	 * 导出文件
	 */
	public void export(OutputStream oStream, Goodstype goodstype) {
		//创建map集合存储数据
		Map<String, Object> dataMap = new HashMap<String,Object>();
		//获得所有的商品类型
		List<Goodstype> goodstypeList = super.getList(goodstype, null, null);
		//创建日期格式化器
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
		Date date = new Date();
		//获得登录用户
		Emp emp = (Emp) SecurityUtils.getSubject().getPrincipal();
		String userName = emp.getName();
		dataMap.put("userName", userName);
		dataMap.put("sdf", sdf);
		dataMap.put("date", date);
		dataMap.put("goodstypeList", goodstypeList);
		HSSFWorkbook wb = null;
		try {
			wb = new HSSFWorkbook(new ClassPathResource("export_goodstype.xls").getInputStream());
			XLSTransformer transformer = new XLSTransformer();
			transformer.transformWorkbook(wb, dataMap);
			wb.write(oStream);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (null != wb) {
				try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	/**
	 * 导入文件
	 */
	public void doImport(InputStream is) throws IOException {
		//创建工作薄
		HSSFWorkbook wb = null;
		try {
			wb = new HSSFWorkbook(is);
			//获得工作表
			HSSFSheet sheet = wb.getSheetAt(0);
			if (!"Sheet1".equals(sheet.getSheetName())) {
				throw new ERPException("工作表名称不正确");
			}
			//读取数据 获得excel表的最后一行
			int lastRowNum = sheet.getLastRowNum();
			Goodstype goodstype = null;
			for (int i = 2; i <= lastRowNum; i++) {
				//构建查询条件
				goodstype = new Goodstype();
				goodstype.setName(sheet.getRow(i).getCell(1).getStringCellValue());//名称
				List<Goodstype> list = goodstypeDao.getList(null, goodstype, null);
				if (list.size() > 0) {
					goodstype = list.get(0);
				}
				//更新
				goodstype.setName(sheet.getRow(i).getCell(1).getStringCellValue());//名称
				//添加
				if (list.size() == 0) {
					goodstype.setUuid(null);
					goodstypeDao.add(goodstype);
				}
			} 
		} finally {
			if (null != wb) {
				try {
					wb.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}		
	}
	
}
