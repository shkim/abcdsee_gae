<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@ page import="com.google.appengine.api.users.UserService"
%><%@ page import="com.google.appengine.api.users.UserServiceFactory"
%><html>
<head>
	<title>ABCDSee Bug Tracker</title>
	<link rel="stylesheet" href="<c:url value='/static/main.css'/>"/>
</head>
<body>
<jsp:include page="inc_bughead.jsp" flush="true">
<jsp:param name="contiUrl" value="/todo/list" />
<jsp:param name="seltab" value="0" />
</jsp:include>

<table class="bt">
	<tr>
		<th class="head" width="120"><fmt:message key="label.priority"/></th>
		<th class="head"><fmt:message key="label.subject"/></th>
	</tr>

<c:forEach var="todo" items="${todoList}">
<tr onmouseover="on_hover(this)" onmouseout="on_out(this)">
	<td class="bt_date"><fmt:message key="label.todo.prio.${todo.priority}"/></td>
	<td class="bt_subject"><a href="view/${todo.id}">${todo.subject}</a></td>
</tr>
</c:forEach>
</table>
<%
UserService userService = UserServiceFactory.getUserService();
if (userService.isUserLoggedIn() && userService.isUserAdmin()) {
%>
	<a href="/todo/form">Write a new TODO</a>
<% } %>

<script>
function on_hover(row)
{
	row.style.backgroundColor='#eeeeff';
}
function on_out(row)
{
	row.style.backgroundColor='#ffffff';
}
</script>
<br/>
<div id="copyright">
<fmt:message key="copyright"/>
</div>
</body>
</html>
