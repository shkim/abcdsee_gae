<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>ABCDSee</title>
</head>
<body>
<h2>ABCDSee QABoard</h2>

<a href="<c:url value='/bugs/list'/>">Bug List</a>
<br/>
<a href="<c:url value='/todo/list'/>">TODO List</a>
<br/>
<a href="<c:url value='/version/latest'/>">Latest version</a>

</body></html>
