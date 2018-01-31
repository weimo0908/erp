/*report_orders.html的js代码*/
if (Request['type']==1) {
	document.title="采购统计分析图";
	url='report_ordersPieReport?type=1';
	chartsTitle='采购统计分析图';
	nameTitle='采购量比例';
	leberTitle='采购额';
}
if (Request['type']==2) {
	document.title="销售统计分析图";
	url ='report_ordersPieReport?type=2';
	chartsTitle='销售统计分析图';
	nameTitle='销售量比例';
	leberTitle='销售额';
}

$(function(){
	
	$('#grid').datagrid({
		url:url,
		columns:[[
		     {field:'name',title:'商品类型',width:100,align:'center'},
		     {field:'y',title:leberTitle,width:100,align:'center'}
		]],
		  //序号效果
        rownumbers:true,
        //斑马线效果(隔行换色)
        striped:true,
        //只能选择一行
        singleSelect:true,
        fitColumns:true,
        onLoadSuccess:function(data){
        	//alert(JSON.stringify(data));
        	showChart(data.rows);
        }

	});

	//查询按钮
	$('#btnSearch').bind('click',function(){
		var formData =$('#searchForm').serializeJSON();
		if (formData.endDate != '') {
			formData.endDate += " 23:59:59";
		}
		$('#grid').datagrid('load',formData);
	});
	
	
});

/**
 * 显示饼状图
 * @param _data
 */
function showChart(_data){
	 $('#pieChart').highcharts({
		 	//图标基本属性
	        chart: {
	        	//区域背景颜色
	            plotBackgroundColor: null,
	            //区域边框宽度
	            plotBorderWidth: null,
	            //区域阴影
	            plotShadow: false,
	        },
	        //图标标题
	        title: {
	            text: chartsTitle
	        },
	        //副标题
	        subtitle: {
	            text: '数据来源:kumiaojie.com'
	        },
	        //信用
	        credits:{enabled:false},
	        //工具提示
	        tooltip: {
	            //工具提示显示格式
	            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
	        },
	        //区域选项
	        plotOptions: {
	            pie: {
	            	//允许点击区域后选择
	                allowPointSelect: true,
	                //光标类型
	                cursor: 'pointer',
	                //数据标签
	                dataLabels: {
	                	enabled: true,
	                    format: '<b>{point.name}</b>: {point.percentage:.1f} %',
	                    style: {
	                        color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
	                    }
	                },
	                //是否显示图例
	                showInLegend: true
	            }
	        },
	        //数据组
	        series: [{
	            type: 'pie',
	            name: nameTitle,
	            data: _data
	        }]
	    });
	 
}


