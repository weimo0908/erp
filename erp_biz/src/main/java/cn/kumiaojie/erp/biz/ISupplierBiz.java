package cn.kumiaojie.erp.biz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.kumiaojie.erp.entity.Supplier;

public interface ISupplierBiz extends IBaseBiz<Supplier>{
	
	/**
	 * 导出excel表格
	 * @param os
	 * @param t1
	 */
	public void export(OutputStream os, Supplier t1);
	
	/**
	 * 导入excel表格
	 * @param is
	 * @throws IOException
	 */
	public void doImport(InputStream is)throws IOException;
}
