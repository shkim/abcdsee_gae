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
	if (trim(f1.nickname.value) == '')
	{
		alert("<fmt:message key="alert.form.fillnick"/>");
		f1.nickname.focus();
		return false;
	}
	if (trim(f1.subject.value) == '')
	{
		alert("<fmt:message key="alert.form.fillsubj"/>");
		f1.subject.focus();
		return false;
	}
	if (trim(f1.content.value) == '')
	{
		alert("<fmt:message key="alert.form.filldesc"/>");
		f1.content.focus();
		return false;
	}

	return true;
}
</script>
</head>
<body>
<jsp:include page="inc_bughead.jsp" flush="true">
<jsp:param name="contiUrl" value="/bugs/list" />
<jsp:param name="seltab" value="9" />
</jsp:include>

<span class="form_title"><fmt:message key="title.reportform"/></span>
<form name="f1" action="${postUrl}" method="post" enctype="multipart/form-data" onsubmit="return chksbm()">
<input type="hidden" name="cmd" value="save"/>

<table class="bugform">
	<tr>
		<th><fmt:message key="label.nickname"/></th>
		<td><input type="text" name="nickname" size="40" value="${realnick}"/></td>
	</tr>

	<tr>
		<th><fmt:message key="label.priority"/></th>
		<td>
			<input type="radio" name="priority" value="1"/><fmt:message key="label.priority.1"/> <span class="help_priority">-- <fmt:message key="help.priority.critical"/></span><br/>
			<input type="radio" name="priority" value="2"/><fmt:message key="label.priority.2"/> <span class="help_priority">-- <fmt:message key="help.priority.high"/></span><br/>
			<input type="radio" name="priority" value="3" checked="true"/><fmt:message key="label.priority.3"/> <span class="help_priority">-- <fmt:message key="help.priority.middle"/></span><br/>
			<input type="radio" name="priority" value="4"/><fmt:message key="label.priority.4"/> <span class="help_priority">-- <fmt:message key="help.priority.low"/></span><br/>
		</td>
	</tr>

	<tr>
		<th><fmt:message key="label.subject"/></th>
		<td><input type="text" name="subject" size="80"/></td>
	</tr>

	<tr>
		<th><fmt:message key="label.description"/></th>
		<td><textarea name="content" rows="20" cols="70"></textarea></td>
	</tr>

	<tr>
		<th>Attachment</th>
		<td><input type="file" name="attach1"></td>
	</tr>
</table>

<input type="submit" name="sbm" value="<fmt:message key="label.submit"/>"/>
</form>

</body>
</html>
