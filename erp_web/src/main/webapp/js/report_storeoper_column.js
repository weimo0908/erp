/*report_storeoper_column.html的js代码*/

$(function(){
	//加载年月下拉选项
    $("#year").combobox({
         valueField:'year',
         textField:'year',
         panelHeight:'auto',
         editable:false
    });
    $("#month").combobox({
         valueField:'month',
         textField:'month',
         panelHeight:'auto',
         editable:false
    });
    //创建年份数据
    var yearArray = [];//年份数组
    var startYear;//起始年份
    var thisYear= new Date().getUTCFullYear();//今年
    var endYear =thisYear +1;//结束年份
    //添加年份(2014-2018)
    for(startYear=endYear-5;startYear<=endYear;startYear++){
         yearArray.push({"year":startYear});
    }
    $("#year").combobox('loadData',yearArray);//下拉框加载数据
    $("#year").combobox('setValue',thisYear);//默认选中今年

    //创建月份数据
    var monthArray = [];//年份数组
    var startMonth=1;//起始年份
    var thisMonth= new Date().getUTCMonth()+1;//本月

    //添加月份1-12月
    for(startMonth;startMonth<=12;startMonth++){
    	startMonth = extra(startMonth);//补位
         monthArray.push({"month":startMonth});
    }
    monthArray.push({"month":'全年'});
    $("#month").combobox('loadData',monthArray);//下拉框加载数据
    $("#month").combobox('setValue','全年');//默认选中当月
    
	
	//加载表格数据
	$('#grid').datagrid({
		url:'report_stroeoperReport',
		columns:[[
		     {field:'name',title:'商品种类',width:100,align:'center',},
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
//        	alert(JSON.stringify(data.rows));
        	var x=proData(data.rows,'name');//商品种类
        	var p=proData(data.rows,'y');//采购数据
        	var s=proData(data.rows,'h');//销售数据
        	showChart(x,p,s);//显示图表
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
function showChart(xData,pData,sData){
	
	$('#stroeoperChart').highcharts({
        chart: {
            type: 'column'
        },
        title: {
            text: $('#year').combobox('getValue')+':'+$('#month').combobox('getValue') + '销售与采购统计分析图'
        },
        subtitle: {
            text: '数据来源:kumiaojie.com'
        },
        //信用
        credits:{enabled:false},
        
        xAxis: {
            categories: xData,
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
            data: pData
	        }, {
            name: '销售量',
            data: sData
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

//补位函数。  
function extra(x)  
{  
    //如果传入数字小于10，数字前补一位0。  
    if(x < 10)  
    {  
        return "0" + x;  
    }  
    else  
    {  
        return x;  
    }  
}  

