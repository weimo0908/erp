/*orders.html的js代码之一*/
//需要用户登录emp

$(function(){
	//弹出窗口变量
	var inoutTitle="";
	//按钮名称
	var btnText="";
	
	//添加静态页面传参(type =1:采购订单;type=2:销售订单)
	var url = 'orders_getListByPage';//所有订单
	//我的订单(myorders)
	if (Request['oper']=='myorders') {//先判断第一层级oper
		if (Request['type']*1==1) {
			url='orders_myListByPage?t1.type=1';
			document.title='我的采购订单';
			btnText ='采购申请';
			addTitle='请选择供应商';
			addInfo='您还没有采购商品';
			//显示供应商
			$('#addOrdersSupplier').html('供应商');
			$('#sup').html('供应商');
			$('#inOrOut').html('入库时间');
		}
		if (Request['type']*1==2) {
			//同时查询销售出入库情况:0=未出库，1=已出库
			url='orders_myListByPage?t1.type=2&t1.state=0';
			document.title='我的销售订单';
			btnText='销售订单录入';
			addTitle='请选择客户';
			addInfo='您还没有销售商品';
			//显示客户
			$('#addOrdersSupplier').html('客户');
			$('#sup').html('客户');
			$('#inOrOut').html('出库时间');
		}
		
	}
	//采购/销售订单查询
	if (Request['oper']=='orders') {
		if (Request['type']*1==1) {
			url +="?t1.type=1";
			document.title ="采购订单查询";
			$('#sup').html('供应商');
			$('#inOrOut').html('入库时间');
		}
		if (Request['type']*1==2) {
			url +="?t1.type=2";
			document.title ="销售订单查询";
			$('#sup').html('客户');
			$('#inOrOut').html('出库时间');
		}
	}
	
	
	//审核(doCheck)state=0,只查询未审核的订单
	if (Request['oper']=='doCheck') {
		url +="?t1.type=1&t1.state=0";
		document.title ="采购订单审核";
	}
	//确认(doStart)state=1
	if (Request['oper']=='doStart') {
		url +="?t1.type=1&t1.state=1";
		document.title ="采购订单确认";
	}
	//入库(doInStore)state =2
	if (Request['oper']=='doInStore') {
		url +="?t1.type=1&t1.state=2";
		document.title ="采购订单入库";
		inoutTitle="入库"
	}
	//出库(doInStore)state =0
	if (Request['oper']=='doOutStore') {
		url +="?t1.type=2&t1.state=0";
		document.title ="销售订单出库";
		inoutTitle="出库"
	}
	
	//创建订单详情toolbar数组
	var toolbar = new Array();
	
	//数据初始化方法
	$('#grid').datagrid({
		url:url,//抽取了方法
		//抽取方法,根据不同的type返回不同内容
		columns:getColumns(),
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
	    sortName:'createtime',
	    //默认asc排序
	    sortOrder:'desc',
	    //分页
	    pagination:true,
	    pagePosition:"bottom",
	    pageNumber:1,
	    pageSize:10,
	    pageList:[10,15,20],
	    
	    onDblClickRow:function(rowIndex, rowData){
	    	//显示订单详情内容(输出一下你就知道怎么写了)
	    	//先加载数据在打开窗口
	    	$('#uuid').html(rowData.uuid);//订单编号
	    	$('#suppliername').html(rowData.supplierName);//供应商
	    	$('#state').html(getState(rowData.state));//订单状态
	    	$('#creater').html(rowData.createrName);//下单员
	    	$('#checker').html(rowData.checkerName);//审查员
	    	$('#starter').html(rowData.starterName);//采购员
	    	$('#ender').html(rowData.enderName);//库管员
	    	$('#createtime').html(formateDate(rowData.createtime));//下单日期
	    	$('#checktime').html(formateDate(rowData.checktime));//审查日期
	    	$('#starttime').html(formateDate(rowData.starttime));//采购日期
	    	$('#endtime').html(formateDate(rowData.endtime));//入库日期
	    	$('#endtime').html(formateDate(rowData.endtime));//入库日期
	    	$('#waybillsn').html("");
	    	
	    	//先创建运单详情toolbar的数组
	    	var toolbar = new Array();
	    	var options = $('#ordersDlg').dialog('options');//对象的元素
	    	//获取原有工具栏
	    	var t = options.toolbar;
	    	//追加到toolbar中,而且每次都只取第一个---->导出excel表格的按钮
	    	toolbar.push(t[0]);
	    	
	    	//判断是销售订单而且已经出库
	    	if (rowData.state*1==1&&rowData.type*1==2) {
	    		//加入运单号
	    		$('#waybillsn').html(rowData.waybillsn);
	    		//添加订单详情工具
	    		toolbar.push({
	    			text:'运单详情',
	    			iconCls:'icon-search',
	    			handler:function(){
	    				//打开窗口
	    				$('#waybillDlg').dialog('open');
	    				//加载数据
	    				$('#waybillgrid').datagrid({
	    					url:'orders_waybilldetailList?waybillsn='+$('#waybillsn').html(),
	    					columns:[[
    					          {field:'sn',title:'运单号',width:100,align:'center'},
    						      {field:'exedate',title:'执行日期',width:100,align:'center'},
    						      {field:'exetime',title:'执行时间',width:100,align:'center'},
    						      {field:'info',title:'执行信息',width:100,align:'center'}
	    					]],
	    					 //收缩宽度
	    					fitColumns:true,
	    					//序号效果
	    				    rownumbers:true
	    				});
	    			}
	    		});
			}
	    	
	    	//重新渲染工具
	    	$("#ordersDlg").dialog({
	    		toolbar:toolbar
	    	});
	    	
	    	//打开窗口
	    	$('#ordersDlg').dialog('open');
	    	//添加订单明细(当双击某一行的时候其实是不会再发送查询语句,因为页面加载时候已经查询了)
	    	$('#itemgrid').datagrid('loadData',rowData.orderDetails);
	    }
			
	});
	
	/* 模糊查询 */
	$('#btnSearch').bind('click',function(){
		//把表单转成接json对象
		var formData = $('#searchForm').serializeJSON();
		$('#grid').datagrid('load',formData);
	});
	
	
	//明细表格itemgrid
	$('#itemgrid').datagrid({
		//url:'orderdetail_getListByPage?t1.state=0',
		columns:[[
		      {field:'uuid',title:'编号',width:100,align:'center'},
		      {field:'goodsuuid',title:'商品编号',width:100,align:'center'},
		      {field:'goodsname',title:'商品名称',width:100,align:'center'},
		      {field:'price',title:'商品价格',width:100,align:'center'},
		      {field:'num',title:'商品数量',width:100,align:'center'},
		      {field:'money',title:'金额',width:100,align:'center'},
		      {field:'state',title:'仓库状态',width:100,align:'center',formatter:getDetailState}
		      ]],
	    //序号效果
	    rownumbers:true,
	    //斑马线效果(隔行换色)
	    striped:true,
	    //收缩宽度
		fitColumns:true,
	    //只能选择一行
	    singleSelect:true,
	    //分页
//	    pagination:true,
//	    pagePosition:"bottom",
//	    pageNumber:1,
//	    pageSize:3,
//	    pageList:[3,5,10]
	    
	});
	
		
	    //判断是否加审核按钮
	    if (Request['oper']=='doCheck') {
	    	toolbar.push({
	    		text:'审核',//按钮名称
	    		iconCls:'icon-ok',
	    		handler:doCheck //审查方法
	    	});
		}

	    //判断是否添加确认按钮
	    if (Request['oper']=='doStart') {
	    	toolbar.push({
	    		text:'确认',
	    		iconCls:'icon-ok',
	    		handler:doStart
	    	});
		}
	    //添加导出excel表格 
	    toolbar.push({
	    	text:'导出Excel',
			iconCls:'icon-excel',
			handler:doExport
	    });
	    
	    
	    
	    //出入库窗口
	    $("#itemDlg").dialog({
	    	title:inoutTitle,
	    	heigth:'200',
	    	width:'300',
	    	modal:true,
	    	closed:true,
	    	buttons:[{
	    		text:inoutTitle,
	    		iconCls:'icon-save',
	    		handler:doInOutStore
	    	}]
	    });
	    
	    $("#ordersDlg").dialog({
	    	toolbar:toolbar
	    });
	    
	    //为入库窗口创建双击打开出/入库操作窗口(需要用户登录)
	    if (Request['oper']=='doInStore'|| Request['oper']=='doOutStore') {
	    	$('#itemgrid').datagrid({
	    		onDblClickRow:function(rowIndex, rowData){
	    			//填充数据
	    			$('#itemuuid').val(rowData.uuid);//input标签赋值
	    			$('#goodsuuid').html(rowData.goodsuuid);//html标签赋值
	    			$('#goodsname').html(rowData.goodsname);//html标签赋值
	    			$('#goodsnum').html(rowData.num);//html标签赋值
	    			//打开窗口
	    			$('#itemDlg').dialog('open');
	    		}
	    	});
		}
	    
	    //订单申请窗口初始化addOrderDlg
	    if (Request['oper']=='myorders') {
			$('#grid').datagrid({
				toolbar:[{
					text:btnText,
					iconCls:'icon-add',
					handler:function(){
						$('#addOrderDlg').dialog('open');
					}
				}]
			});
		}
	
	    //增加订单窗口的初始化
	    $('#addOrderDlg').dialog({
	    	title:'添加订单',
	    	width:700,
	    	height:400,
	    	modal:true,
	    	closed:true
	    });
	    
	    //运单详情窗口初始化
	    $('#waybillDlg').dialog({
	    	title:'运单详情',
	    	width:500,
	    	height:300,
	    	modal:true,
	    	closed:true
	    });
});

function getColumns(){
	if (Request['type']*1==1) {
		//采购订单
		return [[
		         {field:'uuid',title:'编号',width:100,align:'center',sortable:true},
		         {field:'createtime',title:'下单日期',width:100,align:'center',sortable:true,formatter:formateDate},
		         {field:'checktime',title:'审核日期',width:100,align:'center',sortable:true,formatter:formateDate},
		         {field:'starttime',title:'确认日期',width:100,align:'center',sortable:true,formatter:formateDate},
		         {field:'endtime',title:'入库日期',width:100,align:'center',sortable:true,formatter:formateDate},
		         {field:'createrName',title:'下单员',width:100,align:'center'},
		         {field:'checkerName',title:'审核员',width:100,align:'center'},
		         {field:'starterName',title:'采购员',width:100,align:'center'},
		         {field:'enderName',title:'库管员',width:100,align:'center'},
		         {field:'supplierName',title:'供应商',width:200,align:'center'},
		         {field:'totalmoney',title:'合计金额',width:100,align:'center',sortable:true},
		         {field:'state',title:'流程状态',width:100,align:'center',formatter:getState},
		         {field:'waybillsn',title:'运单号',width:100,align:'center'}
		         ]];
	}
	if (Request['type']*1==2) {
		//销售订单
		return [[
		         {field:'uuid',title:'编号',width:100,align:'center',sortable:true},
		         {field:'createtime',title:'下单日期',width:100,align:'center',sortable:true,formatter:formateDate},
		         {field:'endtime',title:'出库日期',width:100,align:'center',sortable:true,formatter:formateDate},
		         {field:'createrName',title:'下单员',width:100,align:'center'},
		         {field:'enderName',title:'库管员',width:100,align:'center'},
		         {field:'supplierName',title:'客户',width:200,align:'center'},
		         {field:'totalmoney',title:'合计金额',width:100,align:'center',sortable:true},
		         {field:'state',title:'流程状态',width:100,align:'center',formatter:getState},
		         {field:'waybillsn',title:'运单号',width:100,align:'center'}
		         ]];
	}
}


/**
 * doCheck审核方法
 */
function doCheck(){
	$.messager.confirm('确认','是否要审核此订单?',function(yes){
		if (yes) {
			//确定审核(发送ajax请求)
			$.ajax({
				//页面上需要一个id(当前订单号)
				url:'orders_doCheck?id='+$('#uuid').html(),//html标签取值.
				dataType:'json',	
				type:'post',
				success:function(rtn){
					$.messager.alert('提示',rtn.message,'info',function(){
						if (rtn.success) {
							//审查成功后
							//关闭订单详情窗口
							$('#ordersDlg').dialog('close');
							//刷新订单列表(reload。等同于'load'方法，但是它将保持在当前页。)
							$('#grid').datagrid('reload');
						}
					});
				}
			});
		}
	});
}

/**
 * doStart确认方法
 */
function doStart(){
	$.messager.confirm('确认','是否要确认此订单?',function(yes){
		if (yes) {
			//确认订单--->发送ajax请求
			$.ajax({
				url:'orders_doStart?id='+$('#uuid').html(),//html标签取值
				dataType:'json',
				type:'post',
				success:function(rtn){
					//提示确认信息
					$.messager.alert('提示',rtn.message,'info',function(){
						if (rtn.success) {
							//成功后关闭窗口刷新主页面
							$('#ordersDlg').dialog('close');
							$('#grid').datagrid('reload');
						}
					});
				}
			});
			
		}
	});
}

/**
 * 入库方法
 */
function doInOutStore(){
	var message ="";//出入库提示信息
	var url ="";//出入库对应地址
	//判断是出库还是入库
	if (Request['type']*1==1) {
		//入库
		message ="是否确认入库?";
		url ="orderdetail_doInStore";
	}
	if (Request['type']*1==2) {
		//出库
		message ="是否确认出库?";
		url ="orderdetail_doOutStore";
	}
	//把itemForm表单转json格式
	var formDate=$("#itemForm").serializeJSON();
	//当仓库未选择的时候
	if (formDate.storeuuid=='') {
		$.messager.alert('提示','您还没选择仓库','info');
		return;
	}
	//如果选择了仓库,再提示一次是否入库
	$.messager.confirm('确认',message,function(yes){
		if (yes) {
			//用ajax提交订单
			$.ajax({
				url:url,
				data:formDate,
				dataType:'json',
				type:'post',
				success:function(rtn){
					//提交后返回的信息
					$.messager.alert('提示',rtn.message,'info',function(){
						if (rtn.success) {
							//如果成功,先关闭入库窗口(itemDlg)
							$("#itemDlg").dialog('close');
							//刷新明细表,更改state为对应的出入库状态
							//getSelect返回第一个呗选中的行
							//页面修改,避免重新读取(出入库对应的数字都一样,所以不用跟着变)
							$('#itemgrid').datagrid('getSelected').state=1;//设置为出/入
							//获取加载后的数据
							var data=$('#itemgrid').datagrid('getData');//格式为{total:num,rows{xxx:xxx,....}}
							//重新刷新
							$('#itemgrid').datagrid('loadData',data);
							//判断是否所有的明细都已经入库-->为了是否判断关闭明细表
							var allIn= true;
							//此处的datagrid.rows是否等于$('#grid').datagrid('getRows')一样的
							$.each(data.rows,function(i,row){
								if(row.state==0){
									//有一个未出/入库则不能关闭
									allIn = false;
									return false;
								}
							});
							if (allIn) {
							//若全部都入库了,关闭订单详情	
								$('#ordersDlg').dialog('close');
								//刷新订单表格
								$('#grid').datagrid('reload');
							}
							
						}
					});
				}
			});
		}
		
	});
	
	
}



/**
 * 时间转换格式
 */
function formateDate(dateValue){
	if(dateValue != null){
		return new Date(dateValue).Format("yyyy-MM-dd");
	}else{
		return '';
	}
	
}

/**
 * 获取订单状态(流程状态)
 */
function getState(value){
	//加入采购或销售状态判断
	if (Request['type']*1 == 1) {
		//采购
		switch(value*1){
		case 0:return '<font color="#d9534f">未审核</font>';
		case 1:return '<font color="#428bca">已审核</font>';
		case 2:return '<font color="#5bc0de">已确认</font>';
		case 3:return '<font color="#5cb85c">已入库</font>';
		default: return '';
		}
	}
	if (Request['type']*1==2) {
		//销售
		switch(value*1){
		case 0: return '<font color="#d9534f">未出库</font>';
		case 1: return '<font color="#5cb85c">已出库</font>';
		default: return '';
		}
	}
}

/**
 * 获取订单详情状态(入库状态)
 */
function getDetailState(value){
	if (Request['type']*1==1) {
		switch(value*1){
		case 0: return '<font color="#d9534f">未入库</font>';
		case 1: return '<font color="#5cb85c">已入库</font>';
		default:return '';
		}
	}

	if (Request['type']*1==2) {
		switch(value*1){
		case 0: return '<font color="#d9534f">未出库</font>';
		case 1: return '<font color="#5cb85c">已出库</font>';
		default:return '';
		}
	}
}

/**
 * 导出excel表格
 */
function doExport(){
	$.download("orders_export",{"id":$("#uuid").html()});
}
