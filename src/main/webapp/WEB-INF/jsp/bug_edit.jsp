<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><html>
<head>
	<title>ABCDSee Bug#${bug.id} Edit</title>
	<link rel="stylesheet" type="text/css" href="<c:url value='/static/main.css'/>"/>
</head>
<body>
<jsp:include page="inc_bughead.jsp" flush="true">
<jsp:param name="contiUrl" value="/bugs/list" />
<jsp:param name="seltab" value="9" />
</jsp:include>

<span class="form_title"><fmt:message key="title.editform"/></span>

<a name="form"/>
<form name="f1" action="${postUrl}" method="post" onsubmit="return chksbm()">
<input type="hidden" name="bugId" value="${bug.id}"/>
<input type="hidden" name="rIndex" value="${rIndex}"/>
<table class="bugform">
	<tr>
		<th><fmt:message key="label.nickname"/></th>
		<td><input type="text" name="nickname" size="40" value="${reply.nickname}"/></td>
	</tr>
<c:if test="${rIndex == 1}">
	<tr>
		<th><fmt:message key="label.subject"/></th>
		<td><input type="text" name="subject" size="70" value="${bug.subject}"/></td>
	</tr>
</c:if>
	<tr>
		<th><fmt:message key="label.description"/></th>
		<td><textarea name="content" rows="10" cols="70">${reply.content}</textarea></td>
	</tr>
</table>

<input type="submit" name="sbm" value="<fmt:message key="label.submit"/>"/>
</form>


<script type="text/javascript">
function chksbm()
{
	return true;
}
</script>

</body></html>
