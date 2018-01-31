package cn.kumiaojie.erp.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import cn.kumiaojie.erp.biz.ISupplierBiz;
import cn.kumiaojie.erp.entity.Supplier;
import cn.kumiaojie.erp.exception.ERPException;
import cn.kumiaojie.erp.web.utils.BaseActionUtil;

/**
 * 部门Action
 * 
 * @author Stivechen
 *
 */
public class SupplierAction extends BaseAction<Supplier> {

	private ISupplierBiz supplierBiz;

	public void setSupplierBiz(ISupplierBiz supplierBiz) {
		this.supplierBiz = supplierBiz;
		super.setBaseBiz(this.supplierBiz);
	}

	// 自动不全模式remote
	private String q;

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	// 文件上传
	private File file;// 上传文件
	private String fileFileName;// 文件名称
	private String fileContentType;// 上传文件类型

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	/*
	 * 自动补全
	 */
	public void list() {
		// 不能把客户/供应商的type给过滤掉了(为空的时候创建对象)
		if (null == getT1()) {
			// 构建查询条件
			setT1(new Supplier());
		}
		// 设置查询条件的名称
		getT1().setName(q);
		// 调用父类方法
		super.list();
	}

	/**
	 * 导出excel表格
	 */
	public void export() {
		// 设置文件名
		String fileName = "";
		if (Supplier.TYPE_CUSTOMER.equals(getT1().getType())) {
			fileName = "酷妙街商城客户表";
		}
		if (Supplier.TYPE_SUPPLIER.equals(getT1().getType())) {
			fileName = "酷妙街商城供应商表";
		}
		// 加上后缀
		fileName += ".xls";
		// 创建response
		HttpServletResponse response = ServletActionContext.getResponse();
		// 设置头文件,实现下载
		try {
			// 固定写法
			response.setHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes(), "ISO-8859-1"));
			// 导出
			supplierBiz.export(response.getOutputStream(), getT1());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上传数据问件
	 */
	public void doImport(){
		//判断文件类型
		if (!"application/vnd.ms-excel".equals(fileContentType)) {
			//文件类型不对
			BaseActionUtil.returnAjax(false, "导入的文件必须是excel文件!");
			return;
		}
		try {
			supplierBiz.doImport(new FileInputStream(file));
			BaseActionUtil.returnAjax(true, "导入数据成功!");
		} catch (ERPException e) {
			BaseActionUtil.returnAjax(false, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			BaseActionUtil.returnAjax(false, "导入数据失败!");
			e.printStackTrace();
		}
	}
}
