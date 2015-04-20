<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@ page import="com.google.appengine.api.users.UserService"
%><%@ page import="com.google.appengine.api.users.UserServiceFactory"
%><%@ page import="shkim.abcdsee.qaboard.domain.BugReply"
%><html>
<head>
	<title>ABCDSee TODOs</title>
	<link rel="stylesheet" type="text/css" href="<c:url value='/static/main.css'/>"/>
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.0/jquery.min.js"></script>
</head>
<body>
<jsp:include page="inc_bughead.jsp" flush="true">
<jsp:param name="contiUrl" value="/todo/list" />
<jsp:param name="seltab" value="9" />
</jsp:include>

<div id="bugview_title">${todo.subject}</div>

<table class="vt">
<tr class="vt_head">
	<td class="vt_todoprio"><fmt:message key="label.priority"/>: <fmt:message key="label.todo.prio.${todo.priority}"/></td>
	<td class="vt_date"><fmt:formatDate type="both" value="${todo.lastUpdate}"/></td>
</tr>
<tr>
	<td colspan="2">
		<pre class="vt_content">${todo.content}</pre>
	</td>
</tr>

<c:if test="${isAdmin}">
<tr><td class="tail" colspan="2">
	<a href="../form?id=${todo.id}">Edit</a>
</td></tr>
</c:if>
</table>
<br/>
<div id="copyright">
<fmt:message key="copyright"/>
</div>
</body>
</html>
