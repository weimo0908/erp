package cn.kumiaojie.erp.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import cn.kumiaojie.erp.biz.IGoodsBiz;
import cn.kumiaojie.erp.entity.Goods;
import cn.kumiaojie.erp.exception.ERPException;
import cn.kumiaojie.erp.web.utils.BaseActionUtil;

/**
 * 商品Action
 * 
 * @author Stivechen
 *
 */
public class GoodsAction extends BaseAction<Goods>{

	private IGoodsBiz goodsBiz;

	public void setGoodsBiz(IGoodsBiz goodsBiz) {
		this.goodsBiz = goodsBiz;
		super.setBaseBiz(this.goodsBiz);
	}
	
	//检索的q
	private String q ;
	public String getQ() {
		return q;
	}
	public void setQ(String q) {
		this.q = q;
	}
	
	/**
	 * 重写list方法
	 */
	@Override
	public void list() {
		if (null == getT1()) {
			setT1(new Goods());
		}
		//这是q查询条件
		getT1().setName(q);
		super.list();
	}
	
	
	/**
	 * 文件导出
	 */
	public void export(){
		//excel文件的名称
		String fileName = "商品列表.xls";
		//响应
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(),"ISO-8859-1"));
			goodsBiz.export(response.getOutputStream(), getT1());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//上传的文件
	private File file;
	//上传文件的文件名称
	private String fileFileName;
	//上传文件的文件类型
	private String fileContentType;
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
	/**
	 * 导入文件
	 */
	public void doImport(){
		//判断文件类型
		if (!"application/vnd.ms-excel".equals(fileContentType)) {
			BaseActionUtil.returnAjax(false, "上传的文件必须为excel文件");
			return;
		}
		try {
			goodsBiz.doImport(new FileInputStream(file));
			BaseActionUtil.returnAjax(true,"上传文件成功");
		} catch (ERPException e) {
			BaseActionUtil.returnAjax(false, e.getMessage());
		} catch (IOException e) {
			BaseActionUtil.returnAjax(false, "上传文件失败");
			e.printStackTrace();
		}
	}
}






