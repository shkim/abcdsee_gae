<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>ABCDSee Version Info</title>
	<link rel="stylesheet" type="text/css" href="<c:url value='/static/main.css'/>"/>
</head>
<body>

<span class="form_title">Write a new version item.</span>

<form name="f1" action="save" method="post">
<input type="hidden" name="id" value="${verId}"/>

<table class="bugform">
	<tr>
		<th>Version</th>
		<td><input type="text" name="name" size="10" value="${ver.name}"/></td>
	</tr>

	<tr>
		<th>Download URL</th>
		<td><input type="text" name="url" size="60" value="${ver.downloadUrl}"/></td>
	</tr>

	<tr>
		<th>File size</th>
		<td><input type="text" name="filesize" size="10" value="${ver.fileSize}"/></td>
	</tr>

	<tr>
		<th>Notes</th>
		<td><textarea name="notes" rows="20" cols="70">${ver.changeNotes}</textarea></td>
	</tr>
</table>

<input type="submit"/>
</form>

<a href="list">Back to List</a>
</body>
</html>
