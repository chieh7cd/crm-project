<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <title>演示文件下载</title>
    <script type="text/javascript">
        $(function () {
            $("#fileDownloadBtn").click(function () {
                // 发送文件下载请求, 所有文件下载请求只能发同步请求, 不能发异步请求;
                // 同步请求：地址栏，超级链接，form表单
                window.location.href = "workbench/activity/fileDownload.do";

            });
        });
    </script>
</head>
<body>
<input type="button" value="下载文件" id="fileDownloadBtn">
</body>
</html>
