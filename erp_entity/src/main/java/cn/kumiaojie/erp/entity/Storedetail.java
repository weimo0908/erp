package cn.kumiaojie.erp.entity;

/**
 * 仓库库存实体类
 */
public class Storedetail {

	private Long uuid;// 编号
	private Long storeuuid;// 仓库编号
	private String storeName;//仓库名称(无映射文件)
	private Long goodsuuid;// 商品编号
	private String goodsName;//商品名称(无映射文件)
	private Long num;// 数量

	public Long getUuid() {
		return uuid;
	}

	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}

	public Long getStoreuuid() {
		return storeuuid;
	}

	public void setStoreuuid(Long storeuuid) {
		this.storeuuid = storeuuid;
	}

	public Long getGoodsuuid() {
		return goodsuuid;
	}

	public void setGoodsuuid(Long goodsuuid) {
		this.goodsuuid = goodsuuid;
	}

	public Long getNum() {
		return num;
	}

	public void setNum(Long num) {
		this.num = num;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}


	
}
