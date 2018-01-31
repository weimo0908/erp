/*pwd.html的js代码*/

$(function(){
	
	$('#grid').datagrid({    
		/* 数据请求对应的action */
	    url:'emp_getListByPage',  
	    //序号效果
	    rownumbers:true,
	    //斑马线效果(隔行换色)
	    striped:true,
	    //只能选择一行
	    singleSelect:true,
	    //排序效果
	    remoteSort:false,
	    
		multiSort:true,
	    
		columns:[[
  		        {field:'uuid',title:'员工编号',width:80,align:'center',sortable:true},    
		        {field:'username',title:'登录名',width:100,align:'center',sortable:true},    
		        {field:'name',title:'员工姓名',width:100,align:'center',sortable:true},    
		        {field:'gender',title:'员工性别',width:100,align:'center',
		        	formatter: function(value){
		        		if(1==value*1){
		        			return '男';
		        		}
		        		if(0==value*1){
		        			return '女';
		        		}
		        	}},    
		        	
		        {field:'email',title:'员工邮箱',width:150,align:'center'},    
		        {field:'tele',title:'员工电话',width:100,align:'center'},    
		        {field:'address',title:'员工地址',align:'center'},    
		        {field:'birthday',title:'员工生日',width:100,align:'center',sortable:true,
		        	formatter: function(value){
		        		return new Date(value).Format("yyyy-MM-dd");
		        	}},    
		        	
		        {field:'dep',title:'员工部门',width:100,align:'center',
		        	formatter:function(value){
		        		return value.name;
		        	}},     
			        	
			        {field:'-',title:'操作',width:100,align:'center',
			        	formatter: function(value,row,index){
			        		var oper = '<a href="javascript:void(0)" onclick="updatePwd_reset(' + row.uuid + ')">重置密码</a>';
				        	return oper;
						}
			        }
				]],
		singleSelect:true,
	    //取消从服务器对数据进行排序。
	    remoteSort:false,
	    //需要排序的字段
	    sortName:'uuid',
	    //默认asc排序
	    sortOrder:'asc',
	    /* 分页功能 */
	    pagination:true,
	    pagePosition:"bottom",
	    pageNumber:1,
	    pageSize:5,
	    pageList:[5,10,15,20]
	}); 
	
	$("#editDlg").dialog({
		title:'重置密码',
		width:300,
		height:120,
		closed:true,
		modal:true,
		iconCls:'icon-save',
		buttons:[
	         {
	        	text:'保存',
	        	iconCls:'icon-save',
	        	handler:function(){
	        		var formData =$("#editForm").serializeJSON();
	        		$.ajax({
	        			url:'emp_updatePwd_reset',
	        			data:formData,
	        			dataType:'json',
	        			type:'post',
	        			success:function(rtn){
	        				$.messager.alert('提示',rtn.message,'info',function(){
	        					if (rtn.success) {
									$("#editDlg").dialog('close');
								}
	        				});
	        			}
	        		});
	        	}
	         }
         ]
	});	
			
});

//打开重置密码窗口
function updatePwd_reset(uuid){
	//开窗口
	$("#editDlg").dialog('open');
	//清空数据
	$("#editForm").form('clear');
	//加载数据 ,把uuid带上,newPed由用户输入
	$('#editDlg').form('load',{id:uuid,newPwd:""});
}

		