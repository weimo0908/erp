package cn.kumiaojie.test.dao;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.kumiaojie.erp.dao.IGoodsDao;
import cn.kumiaojie.erp.entity.Goods;

public class GoodsDaoTest {

	/**
	 * 成功测试
	 */
	@Test
	public void Test01(){
		ApplicationContext ac =new ClassPathXmlApplicationContext("classpath*:applicationContext_*.xml");
		IGoodsDao goodsDao = (IGoodsDao) ac.getBean("goodsDao");
		Goods goods= goodsDao.get(2L);
		System.out.println(goods.getName());
	}
}
