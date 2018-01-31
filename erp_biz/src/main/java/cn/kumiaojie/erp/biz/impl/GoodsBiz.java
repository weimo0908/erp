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
import cn.kumiaojie.erp.biz.IGoodsBiz;
import cn.kumiaojie.erp.dao.IGoodsDao;
import cn.kumiaojie.erp.dao.IGoodstypeDao;
import cn.kumiaojie.erp.entity.Emp;
import cn.kumiaojie.erp.entity.Goods;
import cn.kumiaojie.erp.entity.Goodstype;
import cn.kumiaojie.erp.exception.ERPException;
import net.sf.jxls.transformer.XLSTransformer;

/**
 * GoodsBiz实现类
 * 
 * @author Stivechen
 *
 */
public class GoodsBiz extends BaseBiz<Goods> implements IGoodsBiz {

	// 注入IGoodsDao
	private IGoodsDao goodsDao;

	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
		//继承父类的setBaseDao,并注入goods,让其具体的方法有具体的对象
		super.setBaseDao(this.goodsDao);
	}
	//注入商品类型IGoodstypeDao
	private IGoodstypeDao goodstypeDao;
	public void setGoodstypeDao(IGoodstypeDao goodstypeDao) {
		this.goodstypeDao = goodstypeDao;
	}

	@Override
	/**
	 * 导出文件
	 */
	public void export(OutputStream oStream, Goods goods) {
		//创建map集合存储数据
		Map<String, Object> dataMap = new HashMap<String,Object>();
		//获得所有的商品
		List<Goods> goodsList = super.getList(goods, null, null);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
		//获得当前时间
		Date date = new Date();
		//获得主角,也就是登录用户
		Emp emp = (Emp) SecurityUtils.getSubject().getPrincipal();
		String userName = emp.getName(); 
		dataMap.put("userName", userName);//当前登录用户
		dataMap.put("goodsList", goodsList);//商品集合
		dataMap.put("sdf", sdf);//日期格式化器
		dataMap.put("date", date);//当前时间
		HSSFWorkbook wb = null;
		try {
			wb = new HSSFWorkbook(new ClassPathResource("export_goods.xls").getInputStream());
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
			System.out.println(lastRowNum + "==================================");
			Goods goods = null;
			Goodstype goodstype = null;
			for (int i = 2; i <= lastRowNum; i++) {
				//构建查询条件
				goods = new Goods();
				goods.setName(sheet.getRow(i).getCell(0).getStringCellValue());//名称
				goods.setProducer(sheet.getRow(i).getCell(1).getStringCellValue());//厂家
				List<Goods> list = goodsDao.getList(null, goods, null);
				if (list.size() > 0) {
					goods = list.get(0);
				}
				//更新
				goods.setOrigin(sheet.getRow(i).getCell(2).getStringCellValue());//产地
				goods.setUnit(sheet.getRow(i).getCell(3).getStringCellValue());//计量单位
				goods.setInprice(sheet.getRow(i).getCell(4).getNumericCellValue());//进货价
				goods.setOutprice(sheet.getRow(i).getCell(5).getNumericCellValue());//销售价
				//因为商品类型是对象 所以判断商品类型
				if (null != goods.getGoodstype()) {
					//根据商品类型的id查询出商品类型对象
					goodstype = goodstypeDao.get(goods.getGoodstype().getUuid());
					//在设置进去
					goods.setGoodstype(goodstype);
				}
				//添加
				if (list.size() == 0) {
					//查询出所有的商品类型
					List<Goodstype> goodstypeList = goodstypeDao.getList(null, null, null);
					for (Goodstype g : goodstypeList) {
						//判断如果excel表的商品类型名称和数据库查出来的商品类型名称相同
						if (sheet.getRow(i).getCell(6).getStringCellValue().equals(g.getName())) {
							//则设置商品类型对象
							goods.setGoodstype(g);
						}
					}
					//添加进数据库
					goodsDao.add(goods);
				}
			}
		}finally{
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





