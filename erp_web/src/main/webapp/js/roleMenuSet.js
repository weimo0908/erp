$(function(){
	$('#tree').tree({    
    	animate:true,
		checkbox:true
	});  

	$('#grid').datagrid({
		url:'role_list',
		columns:[[
		      {field:'uuid', title:'编号',width:100,align:'center',sortable:true},
		      {field:'name', title:'名称',width:100,align:'center'}
	   ]],
	 //序号效果
	    rownumbers:true,
	    //斑马线效果(隔行换色)
	    striped:true,
	    //收缩宽度
		fitColumns:true,
	    //只能选择一行
	    singleSelect:true,
	    //取消从服务器对数据进行排序。
	    remoteSort:false,
	    //需要排序的字段
	    sortName:'uuid',
	    //默认asc排序
	    sortOrder:'asc',
	    
	    onClickRow:function(rowIndex,rowData){
	    	$('#tree').tree({
	    		url:'role_readRoleMenu?id='+rowData.uuid,
	    		//是否显示动画效果
	    		animate:true,
	    		//显示复选框
	    		checkbox:true,
	    		//显示虚线
	    		lines:true
	    	});
	    }
	});
	
	$('#btnSave').bind('click',function(){
		//alert(JSON.stringify($('#tree').tree('getChecked')));
		var nodes=$('#tree').tree('getChecked');
		//判断是否有内容(不然后面会出现空指针异常)
		if (nodes.length==0) {
			$.messager.alert('提示','您还没为角色选择响应的菜单','info');
			return;
		}
		var ids = new Array();
		$.each(nodes,function(i,n){
			//通过alert可以知道nodes中的id是我们想要的
			ids.push(n.id);
		});
		//用一个符号隔开(,)
		var checkedStr=ids.join(',');
		//构建提交数据json格式:{key:value}
		var formData ={};
		//key赋值--->id
		formData.id = $('#grid').datagrid('getSelected').uuid;
		//value赋值--->checkedStr
		formData.checkedStr=checkedStr;
		//发起ajax传递
		$.ajax({
			url:'role_updateRoleMenus',
			type:'post',
			dataType:'json',
			data:formData,
			success:function(rtn){
				$.messager.alert('提示',rtn.message,'info');
			}
		});
		
	});
});