package cn.kumiaojie.erp.biz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import cn.kumiaojie.erp.entity.Goodstype;

/**
 * 商品的业务抽象类
 * @author Stivechen
 *
 */
public interface IGoodstypeBiz extends IBaseBiz<Goodstype>{
	
	/**
	 * 商品类型的导出,使用模板
	 * @param oStream 输出流
	 * @param goods 查询条件
	 */
	void export(OutputStream oStream ,Goodstype goodstype);
	/**
	 * 导入文件
	 */
	void doImport(InputStream is) throws IOException;
}
