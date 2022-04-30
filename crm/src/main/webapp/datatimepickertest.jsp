<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <%--引用bootstrap框架--%>
    <link rel="stylesheet" type="text/css" href="jquery/bootstrap_3.3.0/css/bootstrap.min.css"/>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
    <%--引用datatimepicker插件--%>
    <link rel="stylesheet" type="text/css" href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css"/>
    <script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
    <script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
    <title>演示bootstrap_datatimepicker插件</title>
    <script type="text/javascript">
        $(function (){
            // 当容器加载完成，对容器调用工具函数
            $("#myDate").datetimepicker({
                language:'zh-CN', //语言
                format:'yyyy-mm-dd', // 日期格式
                minView:'month', // 可以选择的最小视图, -->显示到月份
                initialDate:new Date(), // 打开后的初始化日期:今天
                autoclose:true, // 选择完自动关闭, 默认不关闭
                todayBtn:true, // 设置是否显示今天的按钮，默认false不显示
                clearBtn:true, // 设置是否显示清空按钮, 默认是false
            });
        });
    </script>
</head>
<body>
<div>
    <input type="text" id="myDate" readonly>
</div>
</body>
</html>
