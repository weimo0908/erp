package cn.kumiaojie.erp.biz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.kumiaojie.erp.entity.Dep;

public interface IDepBiz extends IBaseBiz<Dep>{
	public void doExport(OutputStream os,Dep t1,String userName );
	public void doImport(InputStream is) throws IOException;
	
}
