/*report_storedetail_column.html的js代码*/

$(function(){
	//查询选项框初始化数据
	$('#storeuuid').combobox('setValue', '大中华总仓库');
	
	//加载表格数据
	$('#grid').datagrid({
		url:'report_storedetailReport',
		columns:[[
		     {field:'name',title:'商品种类',width:100,align:'center',},
		     {field:'y',title:'库存量',width:100,align:'center',
		    	 formatter:function(value){
		    		 if ("null"==value) {
		    			 return '<font color="#5cb85c">暂无添加记录</font>';
					}else{
						return value;
					}
		    	 }
		     }
		]],
		  //序号效果
        rownumbers:true,
        //斑马线效果(隔行换色)
        striped:true,
        //只能选择一行
        singleSelect:true,
        fitColumns:true,
        
        onLoadSuccess:function(data){
//        	alert(JSON.stringify(data.rows));
        	var x=proData(data.rows,'name');//商品种类
        	var d=proData(data.rows,'y');//销售数据
        	showChart(x,d);//显示图表
        }

	});
	

	//查询按钮
	$('#btnSearch').bind('click',function(){
		var formData =$('#searchForm').serializeJSON();
		$('#grid').datagrid('load',formData);
		
	});
	
});

/**
 * 显示表格
 */
function showChart(xData,data){
	
	 $('#storedetailChart').highcharts({
	        chart: {
	            type: 'column',
	            margin: 75,
	            options3d: {
	                enabled: true,
	                alpha: 15,
	                beta: 15,
	                depth: 50
	            }
	        },
	        title: {
	            text: $('#storeuuid').combobox('getText') + ' 库存量统计3D柱状图'
	        },
	        subtitle: {
	            text: '请注意值为 0 和 null 的区别'
	        },
	      //信用
	        credits:{enabled:false},
	        
	        plotOptions: {
	            column: {
	                depth: 25
	            }
	        },
	        //x轴显示的商品种类名称
	        xAxis: {
	            categories:xData
	            
	        },
	        yAxis: {
	        	min: 0,
	            title: {
	                text: '库存量(商品单位)'
	            }
	        },
	        
	      //图例
	        legend: {
	            layout: 'horizontal',//水平布局
	            align: 'center',
	            verticalAlign: 'bottom',//底部
	            borderWidth: 0,		//宽度
	            x: 0,
	            y: 20
	        },
	        
	        series: [{
	            name: '库存量',
	          //数量
	            data: data
	        }]
	    });
	
	
}


/**
 * 数据处理
 */
function proData(data,symbol){
	//采购集合
	var dataArray =  new Array()
	//遍历元数据
	$(data).each(function(i,n){
		dataArray.push(n[symbol]);
	});
//	alert(dataArray);
	return dataArray;
}


