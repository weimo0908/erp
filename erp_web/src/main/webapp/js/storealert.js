/*storeoper.html的js代码*/
$(function(){
	$('#grid').datagrid({
		url:'storedetail_storealertList',
		columns:[[
		      {field:'uuid', title:'商品编号',width:100,align:'center'},
		      {field:'name', title:'商品名称',width:100,align:'center'},
		      {field:'storenum', title:'库存量',width:100,align:'center'},
		      {field:'outnum', title:'待发货量',width:100,align:'center'},
		   ]],
			//序号效果
		    rownumbers:true,
		    //斑马线效果(隔行换色)
		    striped:true,
		    //收缩宽度
			fitColumns:true,
		    //只能选择一行
		    singleSelect:true,
		    pageNumber:1,
		    pageSize:10,
		    pageList:[10,15,20],
		    toolbar:[{
		    	text:'发送库存预警邮件',
		    	iconCls:'icon-add-email',
		    	handler:function(){
		    		method = "add";
					$('#emailDlg').form('clear');//清空表单
					$('#emailDlg').dialog('open');//打开窗口
					//加载邮箱地址(让数据加载延后,分解压力)
					loadEmailAdd();
		    	}
		    }]
	
	});


	/* 发邮件窗口属性 */
	$('#emailDlg').dialog({
		
	    title: '库存预警邮件发送',    
	    width: 550,    
	    height: 150,    
	    closed: true,    
	    maximizable:true,
	    collapsible:true,
	    minimizable:true,
	    modal: true,
	    iconCls:'icon-add-email'
	});    
	
	/* 发送按钮 */
	$('#btnSend').bind('click',function(){
		//此处有表单校验
		var isvalidate =$('#emailForm').form('validate');
		if (isvalidate==false) {
			return;
		}
		//把form表单提交的内容转成json
		var formData = $('#emailForm').serializeJSON();
		$.ajax({
			url:'storedetail_sendStorealertMail',
			data:formData,
			dataType:'json',
			type:'post',
			success:function(rtn){
				$.messager.alert("提示",rtn.message,'info',function(){
					if (rtn.success) {
						//成功的话，我们要关闭窗口
						$('#emailDlg').dialog('close');
					}
				});
			}
		});
	});
	
})

function loadEmailAdd(){
	//加载员工信息下拉列表
	$('#emp').combogrid({    
		url:'emp_list',
	    panelWidth:800,    
	    idField:'email',    
	    textField:'email',  
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
	    columns:[[    
	        {field:'uuid',title:'编号',align:'center',width:60,sortable:true},    
	        {field:'name',title:'名称',align:'center',width:100},    
	        {field:'address',title:'联系地址',align:'center',width:120},    
	        {field:'tele',title:'联系电话',align:'center',width:120},    
	        {field:'email',title:'邮箱地址',align:'center',width:100},
	        {field:'dep',title:'员工部门',width:100,align:'center',
	        	formatter:function(value){
	        		return value.name;
	        	}}, 
	    ]],
		mode:'remote'
	});  
}
