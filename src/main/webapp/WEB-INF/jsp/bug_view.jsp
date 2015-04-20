<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@ page import="com.google.appengine.api.users.UserService"
%><%@ page import="com.google.appengine.api.users.UserServiceFactory"
%><%@ page import="shkim.abcdsee.qaboard.domain.BugReply"
%><html>
<head>
	<title>ABCDSee Bug#${bug.id} Detail</title>
	<link rel="stylesheet" type="text/css" href="<c:url value='/static/main.css'/>"/>
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.0/jquery.min.js"></script>
</head>
<body>
<jsp:include page="inc_bughead.jsp" flush="true">
<jsp:param name="contiUrl" value="/bugs/list" />
<jsp:param name="seltab" value="9" />
</jsp:include>

<div id="bugview_title">Bug#${bug.id} : ${bug.subject}</div>
<div id="bugview_priority">
	<fmt:message key="label.priority"/>: <fmt:message key="label.priority.${bug.priority}"/>
</div>
<table class="vt">
<c:set var="icnt" value="0"/>
<c:set var="rcnt" value="0"/>
<c:forEach var="reply" items="${bug.replyList}">
<tr class="vt_head">
	<td class="vt_nick">${reply.nickname} <span class="st_bg${reply.state} stchg"><fmt:message key="label.stchg.${reply.state}"/></span></td>
	<td class="vt_date"><fmt:formatDate type="both" value="${reply.writeDate}"/></td>
</tr>
<tr>
	<td colspan="2">
		<pre class="vt_content"><c:out value="${reply.content}"/></pre>
		<c:set var="rcnt" value="${rcnt +1}"/>
		<c:if test="${rcnt == bug.replyCount}">
<%
	UserService userService = UserServiceFactory.getUserService();
	if (userService.isUserLoggedIn())
	{
		Object _reply = pageContext.getAttribute("reply", PageContext.PAGE_SCOPE);
		if (_reply != null && userService.getCurrentUser().equals( ((BugReply)_reply).getUser() ))
		%>
			<div class="editlink"><a href="<c:url value='/bugs/edit'/>/${bug.id}/${rcnt}" class="editlink">EDIT</a></div>
		<%
	}
%>
		</c:if>
	</td>
</tr>
<c:if test="${reply.fileSize > 0}">
<tr>
	<td colspan="2" class="vt_file">
	Attachment: <a href="/bugs/getfile/${reply.blobKeyString}">${reply.fileName}</a> (${reply.fileSize} bytes)
	<c:if test="${fn:startsWith(reply.fileContentType, 'image/')}">
		<c:set var="icnt" value="${icnt +1}"/>
		<span id="i${icnt}"><input type="button" value="Show Image" onclick="showImg(${icnt},'${reply.blobKeyString}')"></span>
	</c:if>
	</td>
</tr>
</c:if>
</c:forEach>

<c:if test="${!showform}">
	<tr><td class="tail" colspan="2">
<c:if test="${bug.section == 0}">
		<input type="button" value="Update the bug state" onclick="reply()"/>
</c:if>	
<c:if test="${canMoveSection}">
		<input type="button" value="Move to Previous Bugs" onclick="move2prev()"/>
</c:if>
	</td></tr>
</c:if>
</table>

<c:if test="${showform}">
<a name="form"/>
<form name="f1" action="${postUrl}" method="post" enctype="multipart/form-data" onsubmit="return chksbm()">
<input type="hidden" name="bugId" value="${bug.id}"/>
<input type="hidden" name="replyCount" value="${bug.replyCount}"/>
<table class="bugform">
	<tr>
		<th><fmt:message key="label.nickname"/></th>
		<td><input type="text" name="nickname" size="40" value="${realnick}"/></td>
	</tr>

	<tr>
		<th><fmt:message key="label.state"/></th>
		<td>
			<input type="radio" name="state" value="0" checked="1"/><fmt:message key="label.state.0"/> <span class="help_state">-- <fmt:message key="help.state.0"/></span><br/>
			<%
				Object _fm = pageContext.getAttribute("flowmask", PageContext.REQUEST_SCOPE);
				int flowmask = Integer.parseInt(_fm.toString());
				for(int s=0; s<7; s++) {
					if ((flowmask & (1<<s)) == 0)
						continue;

					pageContext.setAttribute("stnum", s+1);
					%><input type="radio" name="state" value="${stnum}"/><fmt:message key="label.state.${stnum}"/> <span class="help_state">-- <fmt:message key="help.state.${stnum}"/></span><br/><%
				}
			%>
		</td>
	</tr>

	<tr>
		<th><fmt:message key="label.description"/></th>
		<td><textarea name="content" rows="10" cols="70">${content}</textarea></td>
	</tr>

	<tr>
		<th>Attachment</th>
		<td><input type="file" name="attach1"></td>
	</tr>
</table>

<input type="submit" name="sbm" value="<fmt:message key="label.submit"/>"/>
</form>
</c:if>

<script type="text/javascript">
<c:if test="${bug.section == 0}">
function reply()
{
	document.location.href = '/bugs/reply/${bug.id}#form';
}
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

	if (trim(f1.content.value) == '')
	{
		alert("<fmt:message key="alert.form.filldesc"/>");
		f1.content.focus();
		return false;
	}

	return true;
}
</c:if>
<c:if test="${canMoveSection}">
function move2prev()
{
	if (confirm('Are you sure to move to the Previous Bugs section?'))
		document.location.href = "/bugs/move2prev/${bug.id}";
}
</c:if>
function showImg(n,pk)
{
	t = document.getElementById('i'+n);
	t.innerHTML = '<br/><img src="/bugs/getfile/'+pk+'">';
}

jQuery(".vt_content").each(function() {
	$(this).html($(this).html()
		.replace(/([^"'=]|^)(https?:\/\/[^ "'<>\n\r\)]+)/g, "$1<a href=\"$2\" target=\"_blank\">$2</a>")
	);
});
</script>
<br/><div id="copyright">
<fmt:message key="copyright"/>
</div>
</body>
</html>
