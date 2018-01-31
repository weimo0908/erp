package cn.kumiaojie.erp.biz.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import cn.kumiaojie.erp.biz.ISupplierBiz;
import cn.kumiaojie.erp.dao.ISupplierDao;
import cn.kumiaojie.erp.entity.Supplier;
import cn.kumiaojie.erp.exception.ERPException;

/**
 * SupplierBiz实现类
 * 
 * @author Stivechen
 *
 */
public class SupplierBiz extends BaseBiz<Supplier> implements ISupplierBiz {

	// 注入ISupplierDao
	private ISupplierDao supplierDao;

	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
		// 继承父类的setBaseDao,并注入supplier,让其具体的方法有具体的对象
		super.setBaseDao(this.supplierDao);
	}

	/**
	 * 导出excel表格
	 * @param os
	 * @param t1
	 */
	public void export(OutputStream os, Supplier t1) {
		// 获取需要导出的数据list
		List<Supplier> list = supplierDao.getList(t1, null, null);
		// 根据查询条件中的类型来创建相应名称的工作表
		// 创建excel的工作簿
		HSSFWorkbook wb = new HSSFWorkbook();

		String sheetName = "";
		if (Supplier.TYPE_SUPPLIER.equals(t1.getType())) {
			// 供应商
			sheetName = "供应商";
		}
		if (Supplier.TYPE_CUSTOMER.equals(t1.getType())) {
			// 客户
			sheetName = "客户";
		}
		// 判断list不为空
		// 创建一个工作表
		HSSFSheet sheet = wb.createSheet(sheetName);
		// 创建大标题
		// 标题,从1开始
		HSSFRow row = sheet.createRow(0);
		// 小标题的内容(不需要编号即uuid,因为这由数据库自己创建)
		String[] header = { "名称", "联系地址", "联系人", "电话", "邮箱" };
		// 行宽
		int[] width = { 5000, 10000, 4000, 8000, 12000 };

		// 创建表小标题
		HSSFCell cell = null;
		for (int i = 0; i < header.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(header[i]);
			// 在工作表中设置所有的列宽
			sheet.setColumnWidth(i, width[i]);
		}
		// 输入数据
		int rowCount = 1;// 行,从第二行开始了
		for (Supplier s : list) {
			row = sheet.createRow(rowCount);
			row.createCell(0).setCellValue(s.getName());// 名称
			row.createCell(1).setCellValue(s.getAddress());// 地址
			row.createCell(2).setCellValue(s.getContact());// 联系人
			row.createCell(3).setCellValue(s.getTele());// 电话
			row.createCell(4).setCellValue(s.getEmail());// 邮箱
			rowCount++;
		}
		// 写出
		try {
			wb.write(os);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关流
			try {
				wb.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 导入表格
	 */
	@Override
	public void doImport(InputStream is) throws IOException {
		HSSFWorkbook wb = null;
		try {
			// 获取导入的表格
			wb = new HSSFWorkbook(is);
			HSSFSheet sheet = wb.getSheetAt(0);
			String type = "";
			if ("供应商".equals(sheet.getSheetName())) {
				type = Supplier.TYPE_SUPPLIER;
			} else if ("客户".equals(sheet.getSheetName())) {
				type = Supplier.TYPE_CUSTOMER;
			} else {
				throw new ERPException("工作表名不正确!");
			}
			// 读取数据
			// 找到最大行号
			int lastRow = sheet.getLastRowNum();
			// 声明接收对象
			Supplier supplier = null;
			for (int i = 1; i <= lastRow; i++) {
				// i=1,去掉标题
				supplier = new Supplier();
				supplier.setName(sheet.getRow(i).getCell(0).getStringCellValue());// 供应商名字
				// 通过名字查询,判断名字是否存在
				List<Supplier> list = supplierDao.getList(null, supplier, null);
				if (list.size() > 0) {
					// 持久化,可以更新对象
					supplier = list.get(0);
				}
				supplier.setAddress(sheet.getRow(i).getCell(1).getStringCellValue());// 地址
				supplier.setContact(sheet.getRow(i).getCell(2).getStringCellValue());// 联系人
				supplier.setTele(sheet.getRow(i).getCell(3).getStringCellValue());// 电话
				supplier.setEmail(sheet.getRow(i).getCell(4).getStringCellValue());// 电话
				if (list.size() == 0) {
					// 新增
					supplier.setType(type);
					supplierDao.add(supplier);
				}
			}
		} finally {
			if (null != wb) {
				try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
