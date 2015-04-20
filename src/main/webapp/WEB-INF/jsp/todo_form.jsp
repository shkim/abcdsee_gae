<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>ABCDSee - Report a new bug</title>
	<link rel="stylesheet" type="text/css" href="<c:url value='/static/main.css'/>"/>
<script type="text/javascript">
function trim(s) { return s.replace(/^\s+|\s+$/g, ''); };
function chksbm()
{
	var f1 = document.f1;
	if (trim(f1.subject.value) == '')
	{
		alert("Please fill in the subject field.");
		f1.subject.focus();
		return false;
	}

	if (trim(f1.content.value) == '')
	{
		alert("Please fill in the description field.");
		f1.content.focus();
		return false;
	}

	f1.sbm.disabled = true;
	return true;
}
</script>
</head>
<body>
<jsp:include page="inc_bughead.jsp" flush="true">
<jsp:param name="contiUrl" value="/todo/list" />
<jsp:param name="seltab" value="9" />
</jsp:include>

<span class="form_title">Write a new TODO item.</span>
<form name="f1" action="${postUrl}" method="post">
<input type="hidden" name="id" value="${todoId}"/>

<table class="bugform">
	<tr>
		<th>Ordering</th>
		<td><input type="text" name="ordering" size="10" value="${ordering}"/></td>
	</tr>

	<tr>
		<th><fmt:message key="label.priority"/></th>
		<td>
		<c:forEach var="prnum" begin="1" end="3">
			<input type="radio" name="priority" value="${prnum}"<c:if test="${prnum == priority}"> checked="true"</c:if>/><fmt:message key="label.todo.prio.${prnum}"/><br/>
		</c:forEach>
		</td>
	</tr>

	<tr>
		<th><fmt:message key="label.subject"/></th>
		<td><input type="text" name="subject" size="80" value="${subject}"/></td>
	</tr>

	<tr>
		<th><fmt:message key="label.description"/></th>
		<td><textarea name="content" rows="20" cols="70">${content}</textarea></td>
	</tr>
</table>

<input type="submit" name="sbm" value="<fmt:message key="label.submit"/>"/>
</form>

</body>
</html>
