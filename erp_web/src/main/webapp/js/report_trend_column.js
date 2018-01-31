/*report_trend_column.html的js代码*/

$(function(){
	//grid显示 
	$('#grid').datagrid({
		url:'report_trendReportWithAll',
		columns:[[
		     {field:'name',title:'月份',width:100,align:'center'},
		     {field:'y',title:'采购额度',width:100,align:'center'},
		     {field:'h',title:'销售额度',width:100,align:'center'}
		]],
		  //序号效果
        rownumbers:true,
        //斑马线效果(隔行换色)
        striped:true,
        //只能选择一行
        singleSelect:true,
        fitColumns:true,
        
        onLoadSuccess:function(data){
        	var b=proData(data.rows,'y');//采购
        	var s=proData(data.rows,'h');//销售数据
        	showChart(b,s);//显示图表
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
function showChart(buyData,sellData){
	
	$('#trendChart').highcharts({
        chart: {
            type: 'column'
        },
        title: {
            text: $('#year').combobox('getValue') + '年销售与采购统计分析图'
        },
        subtitle: {
            text: '数据来源:kumiaojie.com'
        },
        //信用
        credits:{enabled:false},
        
        xAxis: {
            categories: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
            crosshair: true
        },
        yAxis: {
        	min: 0,
            title: {
                text: '额度(RMB ¥)'
            }
        },
        //把数据显示在图标上
        plotOptions: {
            column: {
                pointPadding: 0.2,
                borderWidth: 0,
                dataLabels: {
                    enabled: true
                    }
            }
        },
        tooltip: {
            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
            '<td style="padding:0"><b>{point.y:.1f} 元</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        },
        //图例
        legend: {
            layout: 'horizontal',//水平布局
            align: 'center',
            verticalAlign: 'bottom',//底部
            borderWidth: 0		//宽度
        },
        series: [{
            name: '采购量',
            data: buyData
	        }, {
            name: '销售量',
            data: sellData
        }]
    });
}


/**
 * 数据处理
 * 	"name": "1月",
 *  "h": 38895,-->销售
 *  "y": 1500 -->采购
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


