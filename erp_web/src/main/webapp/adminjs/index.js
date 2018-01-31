
window.onload = function(){
	$('#loading-mask').fadeOut();
}
var onlyOpenTitle="欢迎使用";//不允许关闭的标签的标题

var _menus={
		/*"icon":"icon-sys",
		"menuid":"0",
		"menuname":"系统菜单",
		"menus":
			[
			 	{
			 		"icon":"icon-sys","menuid":"100","menuname":"一级菜单","menus":
					[
						{"icon":"icon-sys","menuid":"101","menuname":"二级菜单","url":""}	,
						{"icon":"icon-sys","menuid":"102","menuname":"二级菜单","url":""}						
					]
			 	}
			 	
			 ]*/
		};



//jquery代码
$(function(){	
	
	//显示用户名,若没有在页面加载的时候会自动跳出到登录页面
	showName();
	//显示当前时间
	showTime();
	//显示角色
	showRole();
	
	
	//加载菜单栏(用json发起请求并返回具体数据)
	$.ajax({
		url:'menu_getMenuTree',
		dataType:'json',
		type:'post',
		success:function(menu){
			_menus=menu;
			InitLeftMenu();
		}
	});
	
	tabClose();
	
	tabCloseEven();
	//安全退出
	$("#loginOut").bind('click',function(){
		$.messager.confirm('确认','真的要退出?',function(yes){
			if (yes) {
				$.ajax({
					url:'login_loginOut',
					success:function(){
						//不需要判断是否成功，因为没有返回值
						location.href="login.html";
					}
				});
			}
		});
	});
	
	
})


//显示用户名
function showName(){
	$.ajax({
		url:'login_showName',
		dataType:'json',
		type:'post',
		success: function(rtn){
			if (rtn.success) {
				//登录成功
				$("#username").html(rtn.message);
			} else {
				//没有登录，跳到登录界面
				location.href="login.html";
			}
		}
	
	});
}


//显示角色
function showRole(){
	$.ajax({
		url:'emp_showRole',
		dataType:'json',
		type:'post',
		success: function(rtn){
			if (rtn.success) {
				//登录成功
				$("#roles").html(rtn.message);
			} else {
				//还没有角色
				$("#roles").html(rtn.message);
			}
		}
	
	});
}


//显示当前时间
function showTime(){  
    //获取系统时间。  
    var dateTime=new Date();  
    var hh=dateTime.getHours();  
    var mm=dateTime.getMinutes();  
    var ss=dateTime.getSeconds();  
      
    //分秒时间是一位数字，在数字前补0。  
    mm = extra(mm);  
    ss = extra(ss);  
      
    //设置时间格式形如：19:18:02  
    document.getElementById("nowTime").innerHTML=hh+":"+mm+":"+ss;  
      
    //每隔1000ms执行方法systemTime()。  
    setTimeout("showTime()",1000);  
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

//初始化左侧
function InitLeftMenu() {
	$("#nav").accordion({animate:false,fit:true,border:false});
	var selectedPanelname = '';
	
	    $.each(_menus.menus, function(i, n) {
			var menulist ='';
			menulist +='<ul class="navlist">';
	        $.each(n.menus, function(j, o) {
				menulist += '<li><div ><a ref="'+o.menuid+'" href="javascript:void(0)" rel="' + o.url + '" ><span class="icon '+o.icon+'" >&nbsp;</span><span class="nav">' + o.menuname + '</span></a></div> ';
				/*
				if(o.child && o.child.length>0)
				{
					//li.find('div').addClass('icon-arrow');
	
					menulist += '<ul class="third_ul">';
					$.each(o.child,function(k,p){
						menulist += '<li><div><a ref="'+p.menuid+'" href="#" rel="' + p.url + '" ><span class="icon '+p.icon+'" >&nbsp;</span><span class="nav">' + p.menuname + '</span></a></div> </li>'
					});
					menulist += '</ul>';
				}
				*/
				menulist+='</li>';
	        })
			menulist += '</ul>';
	
			$('#nav').accordion('add', {
	            title: n.menuname,
	            content: menulist,
					border:false,
	            iconCls: 'icon ' + n.icon
	        });
	
			if(i==0)
				selectedPanelname =n.menuname;
	
	    });
	
	$('#nav').accordion('select',selectedPanelname);



	$('.navlist li a').click(function(){
		var tabTitle = $(this).children('.nav').text();

		var url = $(this).attr("rel");
		var menuid = $(this).attr("ref");
		var icon = $(this).find('.icon').attr('class');

		var third = find(menuid);
		if(third && third.child && third.child.length>0)
		{
			$('.third_ul').slideUp();

			var ul =$(this).parent().next();
			if(ul.is(":hidden"))
				ul.slideDown();
			else
				ul.slideUp();



		}
		else{
			addTab(tabTitle,url,icon);
			$('.navlist li div').removeClass("selected");
			$(this).parent().addClass("selected");
		}
	}).hover(function(){
		$(this).parent().addClass("hover");
	},function(){
		$(this).parent().removeClass("hover");
	});





	//选中第一个
	//var panels = $('#nav').accordion('panels');
	//var t = panels[0].panel('options').title;
    //$('#nav').accordion('select', t);
}
//获取左侧导航的图标
function getIcon(menuid){
	var icon = 'icon ';
	$.each(_menus.menus, function(i, n) {
		 $.each(n.menus, function(j, o) {
		 	if(o.menuid==menuid){
				icon += o.icon;
			}
		 })
	})

	return icon;
}

function find(menuid){
	var obj=null;
	$.each(_menus.menus, function(i, n) {
		 $.each(n.menus, function(j, o) {
		 	if(o.menuid==menuid){
				obj = o;
			}
		 });
	});

	return obj;
}

function addTab(subtitle,url,icon){
	if(!$('#tabs').tabs('exists',subtitle)){
		$('#tabs').tabs('add',{
			title:subtitle,
			content:createFrame(url),
			closable:true,
			icon:icon
		});
	}else{
		$('#tabs').tabs('select',subtitle);
		$('#mm-tabupdate').click();
	}
	tabClose();
}

function createFrame(url)
{
	var s = '<iframe scrolling="auto" frameborder="0"  src="'+url+'" style="width:100%;height:100%;"></iframe>';
	return s;
}

function tabClose()
{
	/*双击关闭TAB选项卡*/
	$(".tabs-inner").dblclick(function(){
		var subtitle = $(this).children(".tabs-closable").text();
		$('#tabs').tabs('close',subtitle);
	})
	/*为选项卡绑定右键*/
	$(".tabs-inner").bind('contextmenu',function(e){
		$('#mm').menu('show', {
			left: e.pageX,
			top: e.pageY
		});

		var subtitle =$(this).children(".tabs-closable").text();

		$('#mm').data("currtab",subtitle);
		$('#tabs').tabs('select',subtitle);
		return false;
	});
}


//绑定右键菜单事件
function tabCloseEven() {

    $('#mm').menu({
        onClick: function (item) {
            closeTab(item.id);
        }
    });

    return false;
}

function closeTab(action)
{
    var alltabs = $('#tabs').tabs('tabs');
    var currentTab =$('#tabs').tabs('getSelected');
	var allTabtitle = [];
	$.each(alltabs,function(i,n){
		allTabtitle.push($(n).panel('options').title);
	})


    switch (action) {
        case "refresh":
            var iframe = $(currentTab.panel('options').content);
            var src = iframe.attr('src');
            $('#tabs').tabs('update', {
                tab: currentTab,
                options: {
                    content: createFrame(src)
                }
            })
            break;
        case "close":
            var currtab_title = currentTab.panel('options').title;
            $('#tabs').tabs('close', currtab_title);
            break;
        case "closeall":
            $.each(allTabtitle, function (i, n) {
                if (n != onlyOpenTitle){
                    $('#tabs').tabs('close', n);
				}
            });
            break;
        case "closeother":
            var currtab_title = currentTab.panel('options').title;
            $.each(allTabtitle, function (i, n) {
                if (n != currtab_title && n != onlyOpenTitle)
				{
                    $('#tabs').tabs('close', n);
				}
            });
            break;
        case "closeright":
            var tabIndex = $('#tabs').tabs('getTabIndex', currentTab);

            if (tabIndex == alltabs.length - 1){
                alert('亲，后边没有啦 ^@^!!');
                return false;
            }
            $.each(allTabtitle, function (i, n) {
                if (i > tabIndex) {
                    if (n != onlyOpenTitle){
                        $('#tabs').tabs('close', n);
					}
                }
            });

            break;
        case "closeleft":
            var tabIndex = $('#tabs').tabs('getTabIndex', currentTab);
            if (tabIndex == 1) {
                alert('亲，前边那个上头有人，咱惹不起哦。 ^@^!!');
                return false;
            }
            $.each(allTabtitle, function (i, n) {
                if (i < tabIndex) {
                    if (n != onlyOpenTitle){
                        $('#tabs').tabs('close', n);
					}
                }
            });

            break;
        case "exit":
            $('#closeMenu').menu('hide');
            break;
    }
}


//弹出信息窗口 title:标题 msgString:提示信息 msgType:信息类型 [error,info,question,warning]
function msgShow(title, msgString, msgType) {
	$.messager.alert(title, msgString, msgType);
}




//设置登录窗口
function openPwd() {
    $('#w').dialog({
        title: '修改密码',
        width: 300,
        height: 210,
        modal: true,
        shadow: true,
        closed: true,
        buttons:[{
	         text:'保存',
	         iconCls:'icon-save',
	         handler:function(){
	        	 //提交保存按钮
	        	 
	        	 //定义变量
	        	 var oldPwd = $("#txtOldPass").val();
	        	 var newPwd = $("#txtNewPass").val();
	        	 var rePwd = $("#txtRePass").val();
	        	 
	        	 //校验前后输入的新密码是否正确（可以用easyui替代吧？）
	        	 if (oldPwd =='') {
					$.mesaager.alert('提示','原密码不能为空！','info');
					return;//还在原来页面
				}
	        	 if (newPwd =='') {
	        		 $.mesaager.alert('提示','新密码不能为空！','info');
	        		 return;//还在原来页面
	        	 }
	        	 if (newPwd != oldPwd) {
	        		 $.mesaager.alert('提示','新密码不能不一致！','info');
	        		 return;//还在原来页面
	        	 }
	        	 //数据请求，因为不是form表单
	        	 $.ajax({
	        		 url:'emp_updatePwd',
	        		 //手动拼接json数据
	        		 data:{"oldPwd":oldPwd,"newPwd":newPwd,},
	        		 dataType:'json',
	        		 type:'post',
	        		 successs:function(rtn){
	        			 $.masseger.alert('提示',rtn.message,'info',function(){
	        				 if (rtn.success) {
								$("#w").dialog('close');
								//清空内容，确保
								 $("#txtOldPass").val('');
					        	 $("#txtNewPass").val('');
					        	 $("#txtRePass").val('');
							} 
	        			 });
	        		 }
	        	 });
	        	 
	         }
        },{
        	text:'关闭',
        	iconCls:'icon-cancel',
        	handler:function(){
        		 $('#w').dialog('close');
        	}
        	
        }]
       
    });
    
    
}


//关闭登录窗口
function closePwd() {
    $('#w').window('close');
}



//修改密码
function serverLogin() {
    var $newpass = $('#txtNewPass');
    var $rePass = $('#txtRePass');

    if ($newpass.val() == '') {
        msgShow('系统提示', '请输入密码！', 'warning');
        return false;
    }
    if ($rePass.val() == '') {
        msgShow('系统提示', '请在一次输入密码！', 'warning');
        return false;
    }

    if ($newpass.val() != $rePass.val()) {
        msgShow('系统提示', '两次密码不一至！请重新输入', 'warning');
        return false;
    }

    $.post('/ajax/editpassword.ashx?newpass=' + $newpass.val(), function(msg) {
        msgShow('系统提示', '恭喜，密码修改成功！<br>您的新密码为：' + msg, 'info');
        $newpass.val('');
        $rePass.val('');
        close();
    })
    
}

$(function() {

    openPwd();

    $('#editpass').click(function() {
        $('#w').window('open');
    });

    $('#btnEp').click(function() {
        serverLogin();
    })

	$('#btnCancel').click(function(){closePwd();})

   
});


