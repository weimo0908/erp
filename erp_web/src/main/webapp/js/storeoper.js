/*storeoper.html的js代码*/

$(function(){
	$('#grid').datagrid({
		url:'storeoper_getListByPage',
		columns:[[
		      {field:'uuid', title:'编号',width:100,align:'center'},
		      {field:'empName', title:'操作员姓名',width:100,align:'center',sortable:true},
		      {field:'opertime', title:'操作日期',width:100,align:'center',formatter:formatDate ,sortable:true},
		      {field:'storeName', title:'仓库名称',width:100,align:'center',sortable:true},
		      {field:'goodsName', title:'商品名称',width:100,align:'center'},
		      {field:'num', title:'商品数量',width:100,align:'center'},
		      {field:'type', title:'类型',width:100,align:'center',formatter:function(value){
		    	  //1:入库;2:出库
		    	  if (value*1==1) {
					return '<font color="#428bca">入库</font>';
				}
		    	  if (value*1 ==2) {
					return '<font color="#5bc0de">出库</font>';
				}
		      }}
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
		    sortName:'opertime',
		    //默认asc排序
		    sortOrder:'desc',
		    //分页
		    pagination:true,
		    pagePosition:"bottom",
		    pageNumber:1,
		    pageSize:5,
		    pageList:[5,10,15,20]   
	});
	
	//点击查询事件
	$('#btnSearch').bind('click',function(){
		//把表单转换成json对象
		var formData=$('#searchForm').serializeJSON();
		//从新加载数据
		$('#grid').datagrid('load',formData);
	});
});

//时间格式转换
function formatDate(value){
	return new Date(value).Format('yyyy-MM-dd hh:mm:ss');
}


