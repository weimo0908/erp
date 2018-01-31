
var btnText = '';
var titleUuid='';
var titleName='';
var titleAdress='';
var existEditIndex=-1;
var btnText='';
var inOutTitle='';
var titleSupplier='';

/**
 * 状态获取
 * @param value
 * @returns {String}
 */
function getState(value){
	if(Request['type'] * 1 == 1){
		switch(value * 1){
		case 1 : return '未审核';
		case 2 : return '已审核';
		case 3 : return '已出库';
		default: return '';
		}
	}
	if(Request['type'] * 1 == 2){
		switch(value * 1){
		case 1 : return '未审核';
		case 2 : return '已审核';
		case 4 : return '已入库';
		default: return '';
		}
	}
}
/**
 * 退货订单详情状态获取
 */
function getRState(value){
	if(Request['type'] * 1 == 1){
		switch(value * 1){
		case 1 : return '未出库';
		case 2 : return '已出库';
		default: return '';
		}
	}
	if(Request['type'] * 1 == 2){
		switch(value * 1){
		case 1 : return '未入库';
		case 2 : return '已入库';
		default: return '';
		}
	}
}
/**
 * 时间格式转换
 * @param value
 * @returns
 */
function dateFormat(value){
	if(value != null){
		return new Date(value).Format('yyyy-MM-dd hh:mm:ss');
	}else{
		return '';
	}
}
/**
 * 获取编辑器
 * @param _field
 * @returns
 */
function getEditor(_field){
	return	$('#itemgrid').datagrid('getEditor',{index:existEditIndex,field:_field});
}
/**
 * 审核订单
 */
function doCheck(){
	//提示是否确认审核
	$.messager.confirm('确认对话框','确认要审核吗?',function(flag){
		if(flag == true){
			//ajax请求审核
			$.ajax({
				url:'returnorders_doCheck?id='+$('#id').html(),
				dataType:'json',
				type:'post',
				success:function(value){
					$.messager.alert('提示',value.message,'info',function(){
						if(value.success){
							//保存成功,关闭窗口
							$('#returnordersDlg').dialog('close');
							//刷新表格数据
							$('#grid').datagrid('reload');
						}else{
							$.messager.confirm('确认对话框','亲,您没有登录,是否跳转到登录页面?',function(flag){
								if(flag == true){
									location.href='login.html';
								}else{
									return;
								}
							});
						}
					});
				}
			});
		}
	});
}

/**
 * 退货出库
 */
function doInOutStore(){
	var message = "";
	var url = "";
	if(Request['type'] * 1 == 2){
		message = "确认要入库吗？";
		url = "returnorders_doInStore?id="+$('#detailuuid').html();
	}
	if(Request['type'] * 1 == 1){
		message = "确认要出库吗？";
		url = "returnorders_doOutStore?id="+$('#detailuuid').html();
	}
	var formdata = '';
	formdata.json = $('#returnitemgrid').serializeJSON();
	$.messager.confirm("确认",message,function(yes){
		if(yes){
			$.ajax({
				url: url,
				data: formdata,
				dataType: 'json',
				type: 'post',
				success:function(rtn){
					$.messager.alert('提示',rtn.message,'info',function(){
						if(rtn.success){
							//关闭入库窗口
							$('#detailitemDlg').dialog('close');
							//设置明细的状态
							$('#returnitemgrid').datagrid('getSelected').state = "2";
							//刷新明细列
							var data = $('#returnitemgrid').datagrid('getData');
							
							$('#returnitemgrid').datagrid('loadData',data);
							//如果所有明细都 入库了，应该关闭订单详情，并且刷新订单列表
							var allIn = true;
							$.each(data.rows,function(i,row){
								if(row.state * 1 == 1){
									allIn = false;
									//跳出循环
									return false;
								}
							});
							if(allIn == true){
								//关闭详情窗口
								$('#returnordersDlg').dialog('close');
								//刷新订单列表
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
 * 删除行
 * @param index
 */
function deleteRow(index){
	$.messager.confirm('确认提示对话','是否确认删除',function(flag){
		if(flag == true){
			$('#itemgrid').datagrid('deleteRow',index);
		}else{
			return;
		}
	});
}

function cal(value){
	//获取数量的编辑器
	var numEditor = getEditor('returnNum');
	//获取价格编辑器
	/*var priceEditor = getEditor('price');*/
	//获取总金额编辑器
	var moneyEditor = getEditor('returnMoney');
	//获取当前数量
	var num = $(numEditor.target).val();
	//获取当前价格
	/*var price = $(priceEditor.target).val();*/
	//计算出当前行总金额
	var rowTotal = num * value;
	//设置金额栏有两位小数点
	rowTotal = rowTotal.toFixed(2);
	//将编辑中的行中的小计金额也算入总计算中
	$(moneyEditor.target).val(rowTotal);
	
	$('#itemgrid').datagrid('getRows')[existEditIndex].returnMoney = rowTotal;
}



function sum(){
	//获取所有行
	var rows = $('#itemgrid').datagrid('getRows');
	//设置一个total变量
	var total = 0;
	//便利所有的行获取row中的金额数
	$.each(rows,function(i,n){
		var money = n.returnMoney;
		if(n.returnMoney == null){
			money = 0;
		}
		total += parseFloat(money);
	});
	//重新加载页脚数据
	$('#itemgrid').datagrid('reloadFooter',[
	   {returnNum:'合计退货金额',returnMoney:total.toFixed(2)}
	]);
	
	
}



$(function(){
	if(Request['type'] * 1 == 1){
		btnText='添加采购退货订单';
		$('#supplierName').html('供应商');
		titleUuid='供应商编号';
		titleName='供应商名称';
		titleAdress='供应商地址';
		titleSupplier='供应商';
	}
	if(Request['type'] * 1 == 2){
		btnText='添加销售退货订单';
		$('#supplierName').html('客户');
		titleUuid='客户编号';
		titleName='客户名称';
		titleAdress='客户地址';
		titleSupplier='客户';
	}
	var url = 'returnorders_getListByPage';
	var  toolbar = new Array();
	var  detailtoolbar = new Array();
	//创建采购退货订单页面
	if(Request['action'] == 'doRCreate'){
		url = url + '?t1.type=1&t1.state=0';
		toolbar.push({
				text:btnText,
				iconCls:'icon-add',
				handler:function(){
	      			$('#sendform').form('clear');
	      			//打开另外一个添加窗口
	      			$('#itemDlg').dialog('open');
	      			/*$('#itemgrid').datagrid('reloadFooter',[
	      			                                        {returnNum:'退货合计金额',returnMoney:0}
	      			                                        ]);*/
	      			$('#itemgrid').datagrid('loadData',{total:0,rows:[]}); 
	      		}
		});
	}
	//判断销售订单添加条件
	if(Request['action'] == 'doRInCheck'){
		url = url +'?t1.type=1&t1.state=1';
		btnText='审核';
	}
	//判断采购订单添加条件
	if(Request['action'] == 'doROutCheck'){
		url = url +'?t1.type=2&t1.state=1';
		btnText='审核';
	}
	//判断销售订单添加条件
	if(Request['action'] == 'doROutStore'){
		url = url +'?t1.type=1&t1.state=2';
		btnText='出库';
		inOutTitle='采购退货出库管理';
	}
	//判断采购订单添加条件
	if(Request['action'] == 'doRInStore'){
		url = url +'?t1.type=2&t1.state=2';
		btnText='入库';
		inOutTitle='销售退货入库管理';
	}
	
	
	
	$('#itemgrid').datagrid({
		columns:[[
		          {field:'uuid',title:'编号',width:100},
		          {field:'goodsname',title:'商品名称',width:100},
		          {field:'price',title:'商品价格',width:100},
		          {field:'num',title:'可退货数量',width:100},
		          {field:'money',title:'原订单金额',width:100},
		          {field:'returnNum',title:'退货数量',width:100,editor:{type:'numberbox'}},
		          {field:'returnMoney',title:'退货金额',width:100,editor:{type:'numberbox',options:{disabled:true,precision:2}}},
		          {field:'returnreason',title:'退货原因',width:100,editor:{type:'text'}},
		          {field:'-',title:'操作',width:100,formatter:function(value,row,rowIndex){
		        	   if(row.returnNum != '合计退货金额'){
		        		   return '<a href="javascript:void(0)" onclick="deleteRow('+rowIndex+')">删除</a>'
		        	   }
		           }},  
		          ]],
		          singleSelect:true,
		          pagination:true,
		          fitColumns:true,
		          showFooter:true,
		          onClickRow:function(rowIndex,rowData){
		        		if(existEditIndex > -1){
			   				$('#itemgrid').datagrid('endEdit',existEditIndex);
			   			}
			   			//因为添加一直是属于最后一行,所以我们获取最后一行的索引(索引=行号-1,从0开始的)
			   			existEditIndex =rowIndex;
			   			//获取新一行,开启编辑状态
			   			$('#itemgrid').datagrid('beginEdit',rowIndex);
			   			var numEditor = getEditor('returnNum');
			   			$(numEditor.target).bind('keyup',function(){
			   				cal(rowData.price);
			   				sum();
			   			})
		          }
	});
	
	$('#grid').datagrid({
		url:url,
		columns:[[
		          {field:'uuid',title:'编号',width:100},
		          {field:'createtime',title:'录入日期',width:100,formatter:dateFormat},
		          {field:'checktime',title:'审核日期',width:100,formatter:dateFormat},
		          {field:'endtime',title:'出库日期',width:100,formatter:dateFormat},
		          {field:'cretaeName',title:'下单员',width:100},
		          {field:'checkName',title:'审核员',width:100},
		          {field:'endtName',title:'库管员',width:100},
		          {field:'supplierName',title:titleSupplier,width:100},
		          {field:'totalmoney',title:'总金额',width:100},
		          {field:'state',title:'订单状态',width:100,formatter:getState},
		          {field:'waybillsn',title:'运单号',width:100},
		          ]],
		          singleSelect:true,
		          pagination:true,
		          fitColumns:true,
		          onDblClickRow:function(rowIndex,rowData){
		        	  //点击显示退货订单详情
		        	  	$('#id').html(rowData.uuid);
						$('#suppliername').html(rowData.supplierName);
						$('#state').html(getState(rowData.state));
						$('#creater').html(rowData.cretaeName);
						$('#checker').html(rowData.checkName);
						$('#ender').html(rowData.endtName);
						$('#createtime').html(dateFormat(rowData.createtime));
						$('#checktime').html(dateFormat(rowData.checktime));
						$('#endtime').html(dateFormat(rowData.endtime));
						
						$.ajax({
							url:'returnorders_list?t1.uuid='+rowData.uuid,
							dataType:'json',
							success:function(rtn){
								$('#returnitemgrid').datagrid('loadData',rtn[0].returnorderdetails);
							}
						})
						//打开入库窗口
						$('#returnordersDlg').dialog('open');
						
		          },
		          toolbar: toolbar
	});
	
	//初始化原订单combogrid下拉窗口
	$('#orders').combogrid({})
	
	
	$('#returnitemgrid').datagrid({
		columns:[[    
		          {field:'uuid',title:'编号',width:100},    
		          {field:'goodsuuid',title:'商品编号',width:100},    
		          {field:'goodsname',title:'商品名称',width:100},    
		          {field:'price',title:'商品价格',width:100},    
		          {field:'state',title:'状态',width:100,formatter:getRState},
		          {field:'num',title:'退货数量',width:100,},  
		          {field:'returnMoney',title:'退货金额',width:100,formatter:function(value,rowData,rowIndex){
		        	  var sum = rowData.price * rowData.num;
		        	  if(rowData.price == null){
		        		  return ;
		        	  }else{
		        		  return sum.toFixed(2);
		        	  }
		          }}
		      ]],
		      fitColumns:true,
		      singleSelect:true,
		      showFooter:true,
		      onLoadSuccess:function(value){
		    	var rows = value['rows'];
		    	var total  = 0;
		    	$.each(rows,function(i,n){
		    		total += n.num * n.price;
		    	})
		    	
		    	$('#returnitemgrid').datagrid('reloadFooter',[
												{uuid:'合计退货金额',goodsuuid:total.toFixed(2)},
		    	                              ]);

		      },
		      onDblClickRow:function(rowIndex,rowData){
		    	  	if(Request['action'] == 'doRCreate'){
		    	  		return;
		    	  	}else{
		    	  		$('#detailuuid').html(rowData.uuid);
		    	  		$('#goodsuuid').html(rowData.goodsuuid);
		    	  		$('#goodsname').html(rowData.goodsname);
		    	  		$('#goodsnum').html(rowData.num);
		    	  		$('#storeuuid').html(rowData.storeuuid);
		    	  		$('#detailitemDlg').dialog('open');
		    	  	}
		      }
		     
	});
	var orderdetailurl = '';
	if(Request['type'] * 1 == 1 ){
		orderdetailurl = 'returnorders_getInReturnOrderdetail?id=';
	}
	
	if(Request['type'] * 1 == 2 ){
		orderdetailurl = 'returnorders_getOutReturnOrderdetail?id=';
	}
	
	
	$('#supplier').combogrid({    
		panelWidth:750,   
	    idField:'uuid',    
	    textField:'name',    
	    url:'supplier_list?t1.type='+Request['type'],   
	    columns:[[    
	        {field:'uuid',title:titleUuid,width:60},    
	        {field:'name',title:titleName,width:100},    
	        {field:'address',title:titleAdress,width:120},    
	        {field:'contact',title:'联系人',width:100},    
	        {field:'tele',title:'电话',width:100},    
	        {field:'email',title:'邮件',width:100}    
	    ]],
	    mode:'remote',
	    onClickRow:function(rowIndex,rowData){
	    	var id = rowData.uuid;
	    	$('#orders').combogrid({    
	    		panelWidth:1000,   
	    	    idField:'uuid',    
	    	    textField:'uuid',
	    	    url:'orders_findOrdersToReturn.action?t1.supplieruuid='+id+'&t1.type='+Request['type'],  
	    	    columns:[[    
	    	        {field:'uuid',title:'编号',width:60},    
	    	        {field:'starttime',title:'确认日期',width:100,formatter:dateFormat},    
	    	        {field:'endtime',title:'入库日期',width:100,formatter:dateFormat},    
	    	        {field:'type',title:'订单类型',width:100,} ,   
	    	        {field:'createrName',title:'创建人名称',width:100},
	    	        {field:'checkerName',title:'审核员名称',width:100},  
	    	        {field:'starterName',title:'采购员名称',width:100},    
	    	        {field:'enderName',title:'库管员名称',width:100},    
	    	        {field:'supplierName',title:'供应商名称',width:100},    
	    	        {field:'totalmoney',title:'总金额',width:100},    
	    	        {field:'state',title:'状态',width:100},    
	    	        {field:'waybillsn',title:'运单号',width:100},    
	    	    ]],
	    	    mode:'remote',
	    	    pagination:true,
	    	    onLoadSuccess:function(){
	    	    	$('#orders').combogrid('showPanel');
	    	    },
	    	    onClickRow:function(rowIndex,rowData){
	    	    	
	    	    	$('#itemgrid').datagrid('loadData',{total:0,rows:rowData.orderDetails,footer:[{returnNum:'合计退货金额',returnMoney:0}]});
	    	    }
	    	});
	    	
	    }
	}); 
	
	
	
	$('#btn').bind('click',function(){
		
		//获取供应商和订单编号,判断是否为空,为空则提示
		if(existEditIndex > -1){
				$('#itemgrid').datagrid('endEdit',existEditIndex);
		}
		var formdata=$('#sendform').serializeJSON();
		if(formdata['t.supplieruuid'] == ''){
			$.messager.alert('提示','供应商不能为空!','info');
		}
		if(formdata['t.orders.uuid'] == ''){
			$.messager.alert('提示','订单编号不能为空!','info');
		}
		
		//拿到数据
		var rows = $('#itemgrid').datagrid('getRows');
		//用来装数据
		var items = new Array();
		//用来判断退货数量
		var info = true;
		
		$.each(rows,function(i,n){
			//如果有returnNum的值
			if(n.returnNum){
				//判断输入的数量是否符合标准
				if(n.num < n.returnNum){
					$.messager.alert('提示','退货数量超过订单数量,请重新输入!','info',function(){
						existEditIndex = i;
						$('#itemgrid').datagrid('beginEdit',i);
						var editor  = getEditor('returnNum');
						$(editor.target).focus();	
					});
					//若退货数量大于原订单数量改变字段
					info=false;
					//终止
					return false;
				}
				
				//把num字段改成退货数量，这样在后端可以直接转为returnorderdeail对象
				n.num = n.returnNum;
				items.push(n);
			}
		})
		//判断退货数量是否超出
		if(info == false){
			return false;
		}
		//封装formdata数据
		formdata.json = JSON.stringify(items);
		/*alert(JSON.stringify(formdata));*/
		//发送ajax请求
		var ordersurl =''; 
		if(Request['type']  * 1== 1 ){
			ordersurl = 'returnorders_addIn?t.type=';
		}
		
		if(Request['type']  * 1 == 2 ){
			ordersurl = 'returnorders_addOut?t.type=';
		}
		
		
		$.ajax({
			url:ordersurl+Request['type'],
			data:formdata,
			dataType:'json',
			type:'post',
			success:function(value){
				$.messager.confirm('确认对话框','是否确认提交?',function(flag){
					if(flag == true){
						$.messager.alert('提示',value.message,'info',function(){
							if(value.success){
								//成功后关闭窗口,刷新页面数据
								$('#itemDlg').dialog('close');
								$('#grid').datagrid('reload');
							}
						})
					}else{
						return;
					}
				})
			}
		});
	});
	/**
	 * 根据条件添加toolbar
	 */
	if(Request['action'] == 'doRInCheck'){
		toolbar.push({
			text:btnText,
			iconCls:'icon-search',
			handler:doCheck
		})
	}
	/**
	 * 根据条件添加toolbar
	 */
	if(Request['action'] == 'doROutCheck'){
		toolbar.push({
			text:btnText,
			iconCls:'icon-search',
			handler:doCheck
		})
	}
	/**
	 * 根据条件添加toolbar
	 */
	if(Request['action'] == 'doROutStore'){
		detailtoolbar.push({
			text:btnText,
			iconCls:'icon-save',
			handler:doInOutStore
		})
	}
	/**
	 * 根据条件添加toolbar
	 */
	if(Request['action'] == 'doRInStore'){
		detailtoolbar.push({
			text:btnText,
			iconCls:'icon-save',
			handler:doInOutStore
		})
	}
	//初始化窗口
	$('#returnordersDlg').dialog({
		toolbar:toolbar
	});
	
	//初始化窗口
	$('#detailitemDlg').dialog({
		title:inOutTitle,
		closed:true,
		height:250,
		width:250,
		toolbar:detailtoolbar
	});
	
	//初始化窗口
	$('#itemDlg').dialog({
		 title: '退货订单添加',    
		    width: 700,    
		    height: 400,    
		    closed: true,    
		    modal: true   
	});
	

});