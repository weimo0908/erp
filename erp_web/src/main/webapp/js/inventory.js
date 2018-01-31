//创建：create  核审：check  查看：search
var oper = Request['oper'];
var year = '';
var goodsuuid = '';
var chartName = '';

$(function(){
	//配置文件加载
	var gridConf = {    
		    url:'inventory_search.action',    
		    columns:[[    
		        {field:'uuid',title:'编号',width:50},    
		        {field:'goodsName',title:'商品名',width:100},    
		        {field:'storeName',title:'仓库名',width:100},    
		        {field:'num',title:'数量',width:80},    
		        {field:'type',title:'类型',width:40,formatter:formatType},    
		        {field:'createtime',title:'登记日期',width:100,formatter:formatTime},    
		        {field:'checktime',title:'审核日期',width:100,formatter:formatTime},    
		        {field:'createrName',title:'登记人',width:70},    
		        {field:'checkerName',title:'审核人',width:70},    
		        {field:'state',title:'状态',width:50,formatter:formatState},    
		        {field:'remark',title:'备注',width:100}
		       
		    ]],
		    fitColumns:true,
		    pagination:true,
		    singleSelect:true
		}
	
	
	//如果'创建'盘盈盘亏界面
	if(oper == 'create'){
		//添加删除，更新操作
		var _columns = gridConf.columns[0]
		_columns.push({field:'..',title:'操作',width:60,formatter:function(value,row,index){
		        	var del = '<a href="javascript:del('+row.uuid+')">删除</a>||';
		        	var update = '<a href="javascript:update()">更新</a>'
		        	return del+update;
		        }});
		
		//添加一个登记按钮
		gridConf.toolbar = [{
	    	text:'盘盈盘亏登记',
			iconCls: 'icon-add',
			handler: function(){
				//添加按钮
				$('#inventory_dialog').dialog({
					buttons:[{
						text:'保存',
						iconCls:'icon-save',
						handler:function(){
							var _data = $('#inventory_form').serializeJSON();
							
							//字段校验
							if(checkField(_data) == false){
								return false;
							}
							
							//上传
							$.ajax({
								url:'inventory_add.action',
								data:_data,
								dataType:'json',
								type:'post', 
								success:function(rtn){
									$.messager.alert('提示',rtn.message,'info',function(){
										if(rtn.success){
											//成功则关闭窗口,并清空表格中数据
											$('#inventory_dialog').dialog('close');
											$('#inventory_form').form('clear');
											//重新加载datagrid
											$('#datagrid').datagrid('reload');
										}
									});
								}
							});
						}
					},{
						text:'关闭',
						iconCls:'icon-cancel',
						handler:function(){
							$('#inventory_dialog').dialog('close');
						}
					}]
				})
				//清空表单
				$('#inventory_form').form('clear');
				//设置盘盈为选中
				$('#type').prop('checked','checked');
				//打开申请页面
				$('#inventory_dialog').dialog('open');
			}
		}];
		
		//更改dategird使其只能看未审核的
		gridConf.url='inventory_search.action?t1.state=0';
		
		//添加一个双击事件
		gridConf.onDblClickRow=function(rowIndex,rowData){
			update(rowData);
		}
	}
	
	//如果‘审核’盘盈盘亏界面
	if(oper == 'check'){
		//更改dategird使其只能看未审核的
		gridConf.url='inventory_search.action?t1.state=0';
		
		//添加一个双击事件
		gridConf.onDblClickRow=function(rowIndex,rowData){
			$('#uuid').html(rowData.uuid);
			$('#createtime').html(formatTime(rowData.createtime));
			$('#goodsName').html(rowData.goodsName);
			$('#storeName').html(rowData.storeName);
			$('#goodsNum').html(rowData.num);
			$('#inventoryType').html(formatType(rowData.type));
			$('#remark').html(rowData.remark);
			
			$('#detail_dialog').dialog('open');
		}
		
		//为详情表格添加一个审核的按钮
		$('#detail_dialog').dialog({
			buttons:[{
				text:'审核',
				iconCls:'icon-add',
				handler:function(){
					$.messager.confirm('提示','确定要审核嘛？',function(r){
						if(r){
							var uuid = $('#uuid').html();
							$.ajax({
								url:'inventory_doCheck.action?id='+uuid,
								dataType:'json',
								type:'post',
								success:function(rtn){
									$.messager.alert('提示',rtn.message,'info',function(){
										if(rtn.success){
											//成功则关闭窗口,并清空表格中数据
											$('#detail_dialog').dialog('close');
											//重新加载datagrid
											$('#datagrid').datagrid('reload');
										}
									});
								}
							})
						}
					});
				}
			}]
		})
	}
	
	//如果是查看界面
	if(oper == 'search'){
		//添加一个看审核还是未审核的搜索项
		$('#searchState').html('状态：<input id="searchRadio" type="radio" name="t1.state" value="" checked>全部'
				+'<input type="radio" name="t1.state" value="0" >未审核'
				+'<input type="radio" name="t1.state" value="1" >已审核');
		
		//添加一个双击事件
		gridConf.onDblClickRow=function(rowIndex,rowData){
//			alert(JSON.stringify(rowData));
			$('#show_uuid').html(rowData.uuid);
			$('#show_state').html(formatState(rowData.state));
			$('#show_createtime').html(formatTime(rowData.createtime));
			$('#show_createName').html(rowData.createrName);
			$('#show_goodsNum').html(rowData.num);
			$('#show_checktime').html(formatTime(rowData.checktime));
			$('#show_checkName').html(rowData.checkerName);
			$('#show_inventoryType').html(formatType(rowData.type));
			$('#show_goodsName').html(rowData.goodsName);
			$('#show_storeName').html(rowData.storeName);
			$('#show_remark').html(rowData.remark);
			
			$('#show_dialog').dialog('open');
		}
		
		//把界面变成
		$('#select_store_span').html('选择仓库<input id="select_store" name="storeuuid"/>');
		//选择仓库下拉表
		$('#select_store').combobox({    
//		    url:'store_findAll.action',    
		    url:'store_myList.action',    
		    valueField:'uuid',    
		    textField:'name',
		    onSelect:function(record){
		    	//拿到仓库的uuid
		    	var storeuuid = record.uuid;
		    	
		    	//获取图表名字
		    	chartName = $('#select_store').combobox('getText');

		    	$('#select_time_span').html('选择年份<input id="select_time" name="year"/>');
		    	
		    	//选择年份下拉表
				$('#select_time').combobox({    
//				    url:'inventory_getYearList.action?id='+storeuuid,    
				    valueField:"year",    
				    textField:"year",
				    onSelect:function(record){
				    	//设置年份
				    	year = record.year;
				    	
				    	//画图
				    	toFindChart(storeuuid);
				    	
				    	//设置初始商品
						$('#select_goods').combobox('setValue',goodsuuid);
				    }
				}); 
		    	
		    	
		    	//查询当前数据库年份
		    	$.ajax({
		    		url:'inventory_getYearList.action?id='+storeuuid,
		    		dataType:'json',
		    		success:function(rtn){
		    			$('#select_time').combobox({
		    				data:rtn
		    			});
		    			if(year == ''){
		    				year = rtn[0].year;
		    			}else{
		    				$.each(rtn,function(i,e){
		    					if(year == e.year){
		    						return false;
		    					}else{
		    						year = e.year;
		    					}
		    				});
		    			}
		    			
						//设置初始年份
						$('#select_time').combobox('setValue',year);
						
						//画图
						toFindChart(storeuuid);
		    		}
		    	});
		    	
				//选择商品下拉表
				$('#select_goods_span').html('选择商品<input id="select_goods" name="goodsuuid"/>');
				$('#select_goods').combobox({    
				    url:'storedetail_findGoodsInStore.action?id='+storeuuid,    
				    valueField:'goodsuuid',    
				    textField:'goodsName',
				    onChange:function(record){
				    	//设置商品
				    	goodsuuid = record;
				    	
				    	//查询
				    	toFindChart(storeuuid);
				    }
				});
				
		    }
		});  
		
	}
	
	//加载datagrid
	$('#datagrid').datagrid(gridConf);
	
	//盘盈盘亏登记窗口
	$('#inventory_dialog').dialog({    
	    title: '盘盈盘亏登记',    
	    width: 255,    
	    height: 260,    
	    closed: true,    
	    modal: true
	}); 
	
	//盘盈盘亏明细窗口
	$('#detail_dialog').dialog({    
		title: '盘盈盘亏审核',    
		width: 260,    
		height: 280,    
		closed: true,    
		modal: true
	}); 
	
	//盘盈盘亏展示窗口
	$('#show_dialog').dialog({    
		title: '盘盈盘亏详情',    
		width: 400,    
		height: 280,    
		closed: true,    
		modal: true,
		buttons:[{
			iconCls:'icon-cancel',
			text:'关闭',
			handler:function(){
				$('#show_dialog').dialog('close');
			}
		}],
		onClose:function(){
			//关闭时清空
			$('#show_checktime').html('');
			$('#show_checkName').html('');
			$('#show_remark').html('');
		}
	}); 
	
	//选择仓库下拉表
	$('#store').combobox({    
//	    url:'store_findAll.action',    
	    url:'store_myList.action',    
	    valueField:'uuid',    
	    textField:'name',
	    onChange:function(record){
	    	//拿到仓库的uuid
	    	var storeuuid = record;
	    	//去查对应仓库下的商品
	    	$('#goods').combobox({
	    		url:'storedetail_findGoodsInStore.action?id='+storeuuid
	    	});
	    }
	});  
	
	//选择商品下拉表
	$('#goods').combobox({    
		valueField:'goodsuuid',    
		textField:'goodsName'
	});  
	
	

})

//格式化类型
function formatType(value,row,index){
	//类型 0:盘赢。。。。1：盘亏
	switch (value) {
		case '0' : return '<font color="green">盘盈</font>';
		case '1' : return '<font color="red">盘亏</font>';
		default : return '';
	}
}

//格式化日期
function formatTime(value,row,index){
	return (new Date(value)).Format("yyyy-MM-dd hh:mm:ss.S");
}

//格式化状态
function formatState(value,row,index){
	//0:未审核。。。。1：已审核
	switch (value) {
		case '0' : return '未审核';
		case '1' : return '已审核';
		default : return '';
	}
}

//搜索功能 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!我电脑日期不同，要设置!!!
function search(){
	var _data = $('#search_form').serializeJSON();
	//设置登记的起始日期
	if(_data['t1.createtime'] != ''){
		_data['t1.createtime']+=' 00:00:00';
	}
	//设置登记的结束日期
	if(_data['t2.createtime'] != ''){
		_data['t2.createtime']+=' 23:59:59';
	}
	//设置审核的起始日期
	if(_data['t1.checktime'] != ''){
		_data['t1.checktime']+=' 00:00:00';
	}
	//设置审核的结束日期
	if(_data['t2.checktime'] != ''){
		_data['t2.checktime']+=' 23:59:59';
	}
	
	$('#datagrid').datagrid('load',_data);
}

//重置搜索功能
function refresh(){
	$('#search_form').form('clear');
	//设置查询type为 全部
	$('#radio').prop('checked','chekced');
	//如果是search界面时
	if(oper=='search'){
		//设置查询state为全部
		$('#searchRadio').prop('checked','checked');
	}
	var _data = $('#search_form').serializeJSON();
	$('#datagrid').datagrid('load',_data);
}

//删除功能
function del(uuid){
	$.messager.confirm('警告','确定删除嘛？',function(r){
		if(r){
			$.ajax({
				url:'inventory_delete.action?t.uuid='+uuid,
				dataType:'json',
				success:function(rtn){
					$.messager.alert('提示',rtn.message,'info');
					if(rtn.success){
						$('#datagrid').datagrid('reload');
					}
				}
			})
		}
	});
}

//更新功能
function update(rowData){
	if(!rowData){
		rowData  = $('#datagrid').datagrid('getSelected')
	}
	
	//增加一个修改按钮
	$('#inventory_dialog').dialog({
		buttons:[{
			text:'修改',
			iconCls:'icon-save',
			handler:function(){
				var _data = $('#inventory_form').serializeJSON();
				
				//字段校验
				if(checkField(_data) == false){
					return false;
				}

				//上传
				$.ajax({
					url:'inventory_update.action',
					data:_data,
					dataType:'json',
					type:'post',
					success:function(rtn){
						$.messager.alert('提示',rtn.message,'info',function(){
							if(rtn.success){
								//成功则关闭窗口,并清空表格中数据
								$('#inventory_dialog').dialog('close');
								$('#inventory_form').form('clear');
								//重新加载datagrid
								$('#datagrid').datagrid('reload');
							}
						});
					}
				})
			}
		}]
	})
	$('#store').combobox('setValue',rowData.storeuuid);
	$('#goods').combobox('setValue',rowData.goodsuuid);
	$('#num').val(rowData.num);
	var type = rowData.type;
	if(type == '0'){
		$('#type').prop('checked','checked');
	}
	if(type == '1'){
		$('#type2').prop('checked','checked');
	}
	$('#inventory_remark').val(rowData.remark);
	$('#inventory_uuid').val(rowData.uuid);
	$('#inventory_dialog').dialog('open');
}

//校验字段的方法
function checkField(_data){
	//进行校验storeuuid的校验
	var storeuuid = _data['t.storeuuid'];
	if(storeuuid == ''){
		$.messager.alert('提示','请选择一个仓库','info',function(){
			$('#store').combobox('showPanel');
		});
		return false;
	}
	//拿到加载的combobox中的仓库
	var stores = $('#store').combobox('getData');
	var noStore = true;
	$.each(stores,function(index,e){
		if(e.uuid==storeuuid){
			//如果输入的仓库跟加载中的匹配
			noStore = false;
			return false;
		}
	});
	if(noStore){
		$.messager.alert('提示','没有此仓库','info',function(){
			$('#store').combobox('showPanel');
		});
		return false;
	}
	
	//进行商品的校验
	var goodsuuid = _data['t.goodsuuid'];
	if(goodsuuid == ''){
		$.messager.alert('提示','请选择一个商品','info',function(){
			$('#goods').combobox('showPanel');
		});
		return false;
	}
	//拿到加载的combobox中的仓库
	var goods = $('#goods').combobox('getData');
	var noGoods = true;
	$.each(goods,function(index,e){
		if(e.goodsuuid == goodsuuid){
			//如果输入的仓库跟加载中的匹配
			noGoods = false;
			return false;
		}
	});
	if(noGoods){
		$.messager.alert('提示','该仓库中没有该商品','info',function(){
			$('#goods').combobox('showPanel');
		});
		return false;
	}
	
	//对数量进行校验
	var num = _data['t.num'];
	if(num==''){
		$.messager.alert('提示','请填写数量','info',function(){
			$('#num').focus();
		});
		return false;
	}
	if(num <= 0){
		$.messager.alert('提示','请填写正确的数量','info',function(){
			$('#num').focus();
		});
		return false;
	}else if(num>0){}else{
		$.messager.alert('提示','数量格式不对','info',function(){
			$('#num').focus();
		});
		return false;
	}
}

//画图
function draw(_data){
	//图表
	 $('#container').highcharts({
	        title: {
	            text: chartName+' '+year+'年度的盘盈盘亏详情',
	            x: -20 //center
	        },
	        xAxis: {
	        	//下面的名！！
	            categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
	                'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
	        },
	        yAxis: {
	            title: {
	                text: '数量(number)'
	            },
	            plotLines: [{
	                value: 0,
	                width: 1,
	                color: '#808080'
	            }]
	        },
	        tooltip: {
	            valueSuffix: '个'
	        },
	        legend: {
	            layout: 'vertical',
	            align: 'right',
	            verticalAlign: 'middle',
	            borderWidth: 0
	        },
	        series:_data
	    });
}

//查询图表
function toFindChart(storeuuid){
	$.ajax({
		url:'report_getInventoryReport.action',
		data:{'storeuuid':storeuuid,'year':year,'goodsuuid':goodsuuid},
		dataType:'json',
		type:'post',
		success:function(rtn){
			draw(rtn);
		}
	})
}