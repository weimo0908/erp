package cn.kumiaojie.erp.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.struts2.ServletActionContext;

import cn.kumiaojie.erp.biz.IDepBiz;
import cn.kumiaojie.erp.entity.Dep;
import cn.kumiaojie.erp.entity.Emp;
import cn.kumiaojie.erp.exception.ERPException;
import cn.kumiaojie.erp.web.utils.BaseActionUtil;

/**
 * 部门Action
 * 
 * @author Stivechen
 *
 */
public class DepAction extends BaseAction<Dep>{

	private IDepBiz depBiz;

	public void setDepBiz(IDepBiz depBiz) {
		this.depBiz = depBiz;
		super.setBaseBiz(this.depBiz);
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
	
	public void export(){
		String filename = "部门信息表.xls";
		Emp login = (Emp) SecurityUtils.getSubject().getPrincipal();
		String loginUser = login.getName();
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			response.setHeader("Content-Disposition",  "attachment;filename=" + new String(filename.getBytes(),"ISO-8859-1"));		
				depBiz.doExport(response.getOutputStream(), getT1(),loginUser);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void doImport() throws ParseException{
		//判断文件类型
		if (!"application/vnd.ms-excel".equals(fileContentType)) {
			//文件类型不对
			BaseActionUtil.returnAjax(false, "导入的文件必须是excel文件!");
			return;
		}
		try {
			depBiz.doImport(new FileInputStream(file));
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
