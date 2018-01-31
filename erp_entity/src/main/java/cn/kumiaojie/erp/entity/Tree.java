package cn.kumiaojie.erp.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 树形菜单实体类
 * @author Stivechen
 *
 */
public class Tree {

	/**
	 * id：节点ID，对加载远程数据很重要。
		text：显示节点文本。
		checked：表示该节点是否被选中。
		children: 一个节点数组声明了若干节点。
	 */
	private String id;//菜单节点id
	private String text;//节点文本
	private boolean checked;//是否被选中
	private List<Tree> children;//子节点
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Boolean getChecked() {
		return checked;
	}
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
	public List<Tree> getChildren() {
		//为了避免在获取二级菜单时候出现空引用
		if (null ==children) {
			children = new ArrayList<Tree>();
		}
		return children;
	}
	public void setChildren(List<Tree> children) {
		this.children = children;
	}
	
	
}
