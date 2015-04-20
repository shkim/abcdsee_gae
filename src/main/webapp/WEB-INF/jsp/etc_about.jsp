<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><html>
<head>
	<title>About ABCDSee</title>
	<link rel="stylesheet" href="<c:url value='/static/main.css'/>"/>
</head>
<body>
(TO BE UPDATED)<br/>
<div align="center">
<h2>ABCDSee</h2>
<h4>Free image viewer for Windows</h4>
</div>

Current version: <a href="relnotes">${lver.name}</a> (unstable)<br/>
Download: <a href="${lver.downloadUrl}">${lver.downloadUrl}</a> (${lver.fileSize} bytes)

</body>
</html>