package cn.kumiaojie.erp.entity;

/**
 * 仓库实体类
 */
public class Store  {

	private Long uuid;// 仓库编号
	private String name;// 名称
//	private String address;//仓库地址
	private Long empuuid;// 员工编号
	private String managerName;//操作员工姓名(新增字段,不需要映射文件)

	public Long getUuid() {
		return uuid;
	}
	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getEmpuuid() {
		return empuuid;
	}
	public void setEmpuuid(Long empuuid) {
		this.empuuid = empuuid;
	}
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	

}
