<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><html><head>
<title>ABCDSee</title>
</head><body>
<script>
window.alert('<fmt:message key="${msg}"/>');
<c:choose><c:when test="${back}">
window.history.back();
</c:when><c:otherwise>
<%
	String href = (String) pageContext.getAttribute("href", 2);
	if (href.charAt(0) == '/')
		pageContext.setAttribute("href", request.getContextPath() + href);
%>
window.location.href='${href}';
</c:otherwise>
</c:choose>
</script>
</body></html>