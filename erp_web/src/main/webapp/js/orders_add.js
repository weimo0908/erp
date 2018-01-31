/*orders.html的js代码之一*/
/*订单新增js  */


//定义全局变量,当前编辑的行索引
var existEditIndex = -1;

$(function(){
	$('#ordersgrid').datagrid({    
	    columns:[[    
	        {field:'goodsuuid',title:'商品编号',width:100,editor:{type:'numberbox',options:{
	        	disabled:true
	        }}},    
	        //下拉列表,异步请求加载数据
	        {field:'goodsname',title:'商品名称',width:100,editor:{type:'combobox',options:{
	        	url:'goods_list',
	        	valueField:'name',
	        	textField:'name',
	        	//可收索方法?(在goodsaction中增加q)
	        	mode:'remote',
	        	//添加选择方法(onSelect是combobox里的方法)
	        	onSelect:function(goods){
	        		 //获取商品编辑编辑器
	        		  var goodsuuidEditor = getEditor('goodsuuid');
	        		  //target，指向真正使用element
	        		  $(goodsuuidEditor.target).val(goods.uuid);
	        		  //获取价格编辑器
	        		  var priceEditor = getEditor('price');
	        		  //判断页面请求的状态
	        		  if (Request['type']*1==1) {
	        			  //采购:设置为进货价格
	        			  $(priceEditor.target).val(goods.inprice);
					}
	        		  if (Request['type']*1==2) {
						//销售:设置为销售价格
	        			  $(priceEditor.target).val(goods.outprice);
					}
	        		  
	        		  //一选中商品就选择到num输入框上
	        		  var numEditor = getEditor('num');
	        		  $(numEditor.target).select();
	        		  
	        		  //绑定事件
	        		  bindGridEvent();
	        		  //同时也要计算金额
	        		  cal();
	        		  //计算总价
	        		  sum();
	        		  
	        	}
	        		
	        		
	        }}},    
	        {field:'price',title:'商品价格',width:100,editor:{type:'numberbox',options:{
	        	precision:2
	        	
	        	//双击进入编辑状态onDblClickCell
	        
	        }}},   
	        
	        {field:'num',title:'商品数量',width:100,editor:'numberbox'},    
	        {field:'money',title:'金额',width:100,editor:{type:'numberbox',options:{
	        	disabled:true,
	        	precision:2
	        	}}},    
	        {field:'-',title:'操作',width:100,align:'center',formatter:function(value,row,rowIndex){
	        	//去掉页脚上的删除
	        	if (row.num =='合计') {
					return;
				}
	        	return '<a href="javascript:void(0)" onclick="deleteRow('+rowIndex+')">删除</a>';
	        }
	        		
	        } 
	    ]],
	    
	    //序号效果
	    rownumbers:true,
	    //斑马线效果(隔行换色)
	    striped:true,
	    //收缩宽度
		fitColumns:true,
	    //只能选择一行
	    singleSelect:true,
	    //显示行脚
	    showFooter:true,
	    //工具栏
		toolbar: [{
			text:'增加',
			iconCls: 'icon-add',
			handler: function(){
					//判断如果当前存在可编辑的行,则关闭它
					if (existEditIndex>-1) {
						$("#ordersgrid").datagrid('endEdit',existEditIndex);
					}
					//在表格的最后添加一行,初始化变量
					$("#ordersgrid").datagrid('appendRow',{num:0,money:0});
					//获取新添加的一行index
					existEditIndex=$("#ordersgrid").datagrid('getRows').length-1;
					//开启编辑状态
					$("#ordersgrid").datagrid('beginEdit',existEditIndex);
					
				}
		},'-',{
			//需要根据不同的类型选择不同的提示
			text:'提交',
			iconCls: 'icon-save',
			handler: function(){
				//让正在编辑的行关闭编辑
				if(existEditIndex >-1){
					$('#ordersgrid').datagrid('endEdit',existEditIndex);
				}	
				//转换html页面中的orderForm(供应商)为json
				var formdata=$('#orderForm').serializeJSON();
				//判断是否没有选择
				if (formdata['t.supplieruuid']=='') {
					$.messager.alert('提示',addTitle,'info');
					return;
				}
				
				//获取表格所有数据
				var rows=$('#ordersgrid').datagrid('getRows');
				//若为空则不跳转
				if(rows.length==0){
					$.messager.alert('提示',addInfo,'info');
					return;
				}
					
				//判断商品名字,价格,个数是否选了
				var flag = true;
				$.each(rows,function(i,r){
					if (r.money*1==0) {//商品金额是否等于0.也就是没选择对应的内容
						flag = false;
						return false;
					}
				});
				if (flag==false) {
					$.messager.alert('提示','您有没选择商品名称.价格.数量,请添加该商品数量或移除该商品','info');
					return;
				} else {
					//在formdata中再加入一个json属性key.(供应商+采购订单)
					formdata.json=JSON.stringify(rows);
					//用ajax提交数据
					$.ajax({
						url:'orders_add?t.type='+Request['type'],
						data:formdata,
						dataType:'json',
						type:'post',
						success:function(rtn){
							$.messager.alert('提示',rtn.message,'info',function(){
								//后续操作
								if (rtn.success) {
									//提交成功后
									$('#supplier').combogrid('clear');//清空下拉列表
									//清空表格
									$('#ordersgrid').datagrid('loadData',{total:0, rows:[],footer:[{num: '合计', money: 0}]});
									//关闭订单窗口
									$('#addOrderDlg').dialog('close');
									//刷新订单列表
									$('#grid').datagrid('reload');
								}
							});
						}
					});
				}
				
			}
		
		}],
		
		//单击某一行事件
		onClickRow:function(rowIndex,rowData){
			//rowIndex：点击的行的索引值，该索引值从0开始。
			//rowData：对应于点击行的记录。			
			//关闭当前可以编辑的行
			$("#ordersgrid").datagrid('endEdit',existEditIndex);
			//设置单击的行的索引为正在编辑的索引
			existEditIndex = rowIndex;
			//开启当前的行编辑状态
			$("#ordersgrid").datagrid('beginEdit',existEditIndex);
			//再次单击的时候绑定事件
			bindGridEvent();
		}
		
	
	});  
	
	//添加行脚
	$('#ordersgrid').datagrid('reloadFooter',[{num:'合计',money:0}]);
	
	//加载供应商/客户下拉列表
	$('.searchsupplier').combogrid({    
		url:'supplier_list?t1.type='+Request['type'],//1是供应商,2是客户
	    panelWidth:800,    
	    idField:'uuid',    
	    textField:'name',  
	    //序号效果
	    rownumbers:true,
	    //斑马线效果(隔行换色)
	    striped:true,
	    //收缩宽度
		fitColumns:true,
	    //只能选择一行
	    singleSelect:true,
	    columns:[[    
	        {field:'uuid',title:'编号',align:'center',width:60},    
	        {field:'name',title:'名称',align:'center',width:100},    
	        {field:'address',title:'联系地址',align:'center',width:120},    
	        {field:'contact',title:'联系人',align:'center',width:120},    
	        {field:'tele',title:'联系电话',align:'center',width:120},    
	        {field:'email',title:'邮箱地址',align:'center',width:200}    
	    ]],  
	    mode:'remote'
	});  

});


/**
 * 获取编辑器方法(传入字段名称,返回编辑器)
 */
function getEditor(_field){
	return $('#ordersgrid').datagrid('getEditor',{index:existEditIndex,field:_field});
}

/**
 * 计算金额方法
 */
function cal(){
	//获得num数量的编辑器
	var numEditor = getEditor('num');
	//获得他的值
	var num =$(numEditor.target).val();
	//获取商品的价格
	var priceEditor = getEditor('price');
	var price = $(priceEditor.target).val();
	//进行计算num*price,并保留两位小数
	var money = (num*price).toFixed(2);
	//获取money编辑器
	var moneyEditor = getEditor('money');
	//赋值
	$(moneyEditor.target).val(money);
	//更新表中的数据,设置row,json里的key对应的值
	$('#ordersgrid').datagrid('getRows')[existEditIndex].money=money;
	
}

function sum(){
	//获取所有的rows(getRows)
	var rows=$('#ordersgrid').datagrid('getRows');
	//初始化total总价
	var total =0;
	//循环累加
	$.each(rows,function(i,r){
		total += parseFloat(r.money);
	});
	total=total.toFixed(2);
	//重新加载页脚
	$('#ordersgrid').datagrid('reloadFooter',[{num:'合计',money:total}]);
}


/**
 * 绑定表格编辑框的键盘输入事件
 */
function bindGridEvent(){
	//再想要的选择器上绑定keyup事件,然后在绑定计算等方法
	//获取price的编辑器
	var priceEditor = getEditor('price');
	//绑定键盘输入事件
	$(priceEditor.target).bind('keyup',function(){
		//计算金额
		cal();
		//计算总价
		sum();
	});
	
	//获取num编辑器
	var numEditor = getEditor('num');
	//绑定键盘输入事件
	$(numEditor.target).bind('keyup',function(){
		//计算金额
		cal();
		//计算总价
		sum();
	});
}

/**
 * 删除行
 */
function deleteRow(rowIndex){
	//关闭当前正在编辑的行
	$('#ordersgrid').datagrid('endEdit',existEditIndex);
	//删除行
	$('#ordersgrid').datagrid('deleteRow',rowIndex);
	
	//getData返回加载完毕后的数据
	var data=$('#ordersgrid').datagrid('getData');
	//重新加载
	$('#ordersgrid').datagrid('loadData',data);
	//重新统计总价
	sum();
}
