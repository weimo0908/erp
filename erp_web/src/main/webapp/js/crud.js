/*通用crud截面js代码抽取*/


//抽取method方法,共用一个对话框
var method ="";

//区分供应商还是客户type(静态页面传参)
//根据type的值进入赋值
var listParam ="";
//记录保存url提交的type参数
var saveParam ="";

$(function(){
	
	$('#grid').datagrid({    
		/* 数据请求对应的action */
	    url:name+'_getListByPage'+listParam,  
	    //序号效果
	    rownumbers:true,
	    //斑马线效果(隔行换色)
	    striped:true,
	    //只能选择一行
	    singleSelect:true,
	    //取消从服务器对数据进行排序。
	    remoteSort:false,
	    //需要排序的字段
	    sortName:'uuid',
	    //默认asc排序
	    sortOrder:'asc',
		
		//收缩宽度
		fitColumns:true,
	    
	   //列表属性已经被提取
	    columns:columns,
	    
	    /* 分页功能 */
	    pagination:true,
	    pagePosition:"bottom",
	    pageNumber:1,
	    pageSize:10,
	    pageList:[10,15,20],
	    
	    /* 工具栏 */
	    toolbar: [{
	    	text: '新增',
			iconCls: 'icon-add',
			handler: function(){
				method = "add";
				$('#editDlg').panel({title:"添加窗口"});
				$('#editDlg').panel({iconCls:"icon-add"});
				$('#editForm').form('clear');//清空表单
				$('#editDlg').dialog('open');//打开窗口
			}
		},'-',{
			text:'导出Excel',
			iconCls:'icon-excel-download',
			handler:function(){
				var formData = $('#searchForm').serializeJSON();
				//下载文件
				$.download(name+"_export"+listParam, formData);
			}
		},'-',{
			text:'导入Excel',
			iconCls:'icon-excel-upload',
			handler:function(){
				//打开上传窗口
				$('#importDlg').dialog('open');
			}
		}]
	    
	}); 
	
	/* 模糊查询 */
	$('#btnSearch').bind('click',function(){
		//把表单转成接json对象
		var formData = $('#searchForm').serializeJSON();
		//JSON.stringify(object)用于将json对象转换为json字符串
		//alert(JSON.stringify(formData)); 
		$('#grid').datagrid('load',formData);
	});
	
	/*定义初始化窗口大小*/
	var h = 150;
	var w = 300;
	if(typeof(height) != "undefined"){
		h = height;
	}
	if(typeof(width) != "undefined"){
		w = width;
	}
	
	/* 编辑窗口属性 */
	$('#editDlg').dialog({
		
	    title: '编辑',    
	    width: w,    
	    height: h,    
	    closed: true,    
	    maximizable:true,
	    collapsible:true,
	    minimizable:true,
	    modal: true
	});    

	/* 保存按钮 */
	$('#btnSave').bind('click',function(){
		//此处有表单校验
		var isvalidate =$('#editForm').form('validate');
		if (isvalidate==false) {
			return;
		}
		//把form表单提交的内容转成json
		var formData = $('#editForm').serializeJSON();
		$.ajax({
			url:name+'_'+method + saveParam,
			data:formData,
			dataType:'json',
			type:'post',
			success:function(rtn){
				$.messager.alert("提示",rtn.message,'info',function(){
					//成功的话，我们要关闭窗口
					$('#editDlg').dialog('close');
					//刷新表格数据
					$('#grid').datagrid('reload');
				});
			}
		});
	});
		
	//判断是否有导入功能
	var importForm = document.getElementById('importForm');
	if (importForm) {//---->等同于null != importForm
		$('#importDlg').dialog({
			title:'导入数据',
			width:330,
			height:120,
			modal:true,
			closed: true, 
			buttons:[{
				text:'导入',
				handler:function(){
					$.ajax({
						url:name+'_doImport',
						data:new FormData($('#importForm')[0]),//---->数组形式,取第一个
						type:'post',
						//上传文件特有一下两个方法
						processData:false,
						contentType:false,
						
						dataType:'json',
						success:function(rtn){
							$.messager.alert('提示',rtn.message,'info',function(){
								if (rtn.success) {
									//关闭窗口
									$('#importDlg').dialog('close');
									$('#importForm').form('clear');
									//重新刷新显示列表
									$('#grid').datagrid('reload');
								}
							});
						}
					});
				}
			}]
		});
	}
			
});

/* 更改对象信息 */
function edit(uuid){
	//修改窗口title
	$('#editDlg').panel({title:"修改窗口"});
	$('#editDlg').panel({iconCls:"icon-edit"});
	//弹出窗口
	$('#editDlg').dialog('open');
	
	//清空表单内容
	$('#editForm').form('clear');
	method = 'update';
	//加载数据
	$('#editForm').form('load',name+'_get?id='+uuid);
}

/* 删除 */
function del(uuid){
	$.messager.confirm("确认","确认要删除吗？",function(yes){
		if(yes){
			$.ajax({
				url: name+'_delete?id=' + uuid,
				dataType: 'json',
				type: 'post',
				success:function(rtn){
					$.messager.alert("提示",rtn.message,'info',function(){
						//刷新表格数据
						$('#grid').datagrid('reload');
					});
				}
			});
		}
	});
}
		