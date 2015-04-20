<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><html>
<head>
	<title>ABCDSee Release Notes</title>
	<link rel="stylesheet" href="<c:url value='/static/main.css'/>"/>
</head>
<body>
<div class="rn_title">ABCDSee Release Notes</div>
<br/>

<c:forEach var="ver" items="${verList}">
<span class="rn_ver">Version ${ver.name}</span> &nbsp; <span class="rn_date">(<fmt:formatDate value="${ver.releaseDate}"/>)</span>
<br/>
<ul class="rn_notes">${ver.changeNotes}</ul>
</c:forEach>

</body>
</html>