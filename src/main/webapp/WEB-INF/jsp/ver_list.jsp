<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@ page import="com.google.appengine.api.users.UserService"
%><%@ page import="com.google.appengine.api.users.UserServiceFactory"
%><html>
<head>
	<title>ABCDSee Version List</title>
	<link rel="stylesheet" href="<c:url value='/static/main.css'/>"/>
</head>
<body>

<table border="1">
	<tr>
		<th>ID</th>
		<th>Version</th>
		<th>URL</th>
		<th>FileSize</th>
		<th>Notes</th>
	</tr>

<c:forEach var="ver" items="${verList}">
<tr>
	<td><a href="form?id=${ver.id}">#${ver.id}</a></td>
	<td>${ver.name}</td>
	<td>${ver.downloadUrl}</td>
	<td>${ver.fileSize}</td>
	<td><pre>${ver.changeNotes}</pre></td>
</tr>
</c:forEach>
</table>

<br/>
<a href="form">Add new version</a>
</body>
</html>
