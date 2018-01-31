/*storeoper.html的js代码*/

$(function(){
	
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
        	//alert(JSON.stringify(data.rows));
        	var b=buyData(data.rows);//采购
        	var s=sellData(data.rows);//销售数据
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
            type: 'line'
        },
        title: {
            text: $('#year').combobox('getValue') + '销售与采购统计分析图'
        },
        subtitle: {
            text: '数据来源:kumiaojie.com'
        },
        //信用
        credits:{enabled:false},
        xAxis: {
            categories: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
        },
        yAxis: {
            title: {
                text: '额度(RMB ¥)'
            }
        },
        //把数据显示在图标上
        plotOptions: {
            line: {
                dataLabels: {
                    enabled: true          // 开启数据标签
                },
                enableMouseTracking: true // 关闭鼠标跟踪，对应的提示框、点击事件会失效
            }
        },
        tooltip: {
            valueSuffix: '元'
        },
        
        legend: {
            layout: 'vertical',
            align: 'center',
            verticalAlign: 'bottom',
            borderWidth: 0
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
function buyData(data){
	//采购集合
	var buyArray =  new Array()
	//遍历元数据
	$(data).each(function(i,n){
		buyArray.push(n.y);
	});
//	alert(buyArray);
	return buyArray;
}

function sellData(data){
	//采购集合
	var sellArray =  new Array()
	//遍历元数据
	$(data).each(function(i,n){
		sellArray.push(n.h);
	});
//	alert(sellArray);
	return sellArray;
}


