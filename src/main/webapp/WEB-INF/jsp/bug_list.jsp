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
<body><%--<fmt:setLocale scope="session" value="${sessionScope['javax.servlet.jsp.jstl.fmt.locale.session']}"/>--%>

<jsp:include page="inc_bughead.jsp" flush="true">
<jsp:param name="contiUrl" value="/bugs/list" />
<jsp:param name="seltab" value="${group+1}" />
</jsp:include>

<form id="filterbox" name="fb" action="list">
<b>Filter</b>:

&nbsp;Priority
<select name="pr" onchange="applyFilter()">
	<option value="0"><fmt:message key="label.all"/></option>
<c:forEach var="prnum" begin="1" end="4">
	<option value="${prnum}" class="pr_${prnum}"<c:if test="${priority==prnum}"> selected</c:if>><fmt:message key="label.priority.${prnum}"/></option>
</c:forEach>
</select>

&nbsp;Status
<select name="st" onchange="applyFilter()">
	<option value="0"><fmt:message key="label.all"/></option>
<c:forEach var="stnum" begin="1" end="7">
	<option value="${stnum}" class="st_${stnum}"<c:if test="${state==stnum}"> selected</c:if>><fmt:message key="label.state.${stnum}"/></option>
</c:forEach>
</select>
<%--
&nbsp; &nbsp; Keyword
<select name="kt">
	<option value="0"><fmt:message key="label.subject"/></option>
	<option value="1"<c:if test="${ktype==1}"> selected</c:if>><fmt:message key="label.nickname"/></option>
	<option value="2"<c:if test="${ktype==2}"> selected</c:if>>Tags</option>
</select>
<input type="text" name="q" value="${keyword}"/>
<input type="submit" value="Search"/>--%>
</form>

<table class="bt">
	<tr>
		<th class="head" width="50">Bug ID</th>
		<th class="head" width="320"><fmt:message key="label.subject"/></th>
		<th class="head" width="100"><fmt:message key="label.nickname"/></th>
		<th class="head" width="80"><fmt:message key="label.date"/></th>
	</tr>

<c:forEach var="bug" items="${bugList}">
<tr onmouseover="on_hover(this)" onmouseout="on_out(this)">
	<th class="st_bg${bug.state} bugid">${bug.id}</th>
	<td class="bt_subject"><a href="view/${bug.id}"><c:choose>
		<c:when test="${bug.priority==1}"><span class="pr_1">↑↑</span></c:when>
		<c:when test="${bug.priority==2}"><span class="pr_2">↑</span></c:when>
		<c:when test="${bug.priority==3}"><span class="pr_3">↕</span></c:when>
		<c:when test="${bug.priority==4}"><span class="pr_4">↓</span></c:when>
		</c:choose>${bug.subject}</a></td>
	<td class="bt_writer">${bug.nicknameWithoutHostname}</td>
	<td class="bt_date">${bug.regDateTillDay}</td>
</tr>
</c:forEach>
<c:if test="${pagerInfo.totalCount == 0}">
<tr>
	<th colspan="4" class="bt_noresult">
		No Results
	</th>
</tr>
</c:if>

	<tr><td class="tail" colspan="2">
	<c:if test="${group==0}">
		<input type="button" value="Report a new bug" onclick="reportNew()"/>
	</c:if>
	</td>
	<td class="tail pager" colspan="2"><c:if test="${pagerInfo.totalCount > 0}">
		<c:if test="${pagerInfo.hasPrevPage}"><a href="javascript:movePage('p')" class="pagelink">&lt; Newer</a> &nbsp;</c:if>
		${pagerInfo.itemBegin}-${pagerInfo.itemEnd} of ${pagerInfo.totalCount}
		<c:if test="${pagerInfo.hasNextPage}">&nbsp; <a href="javascript:movePage('n')" class="pagelink">Older &gt;</a></c:if>
	</c:if>&nbsp;</td>
	</tr>
</table>

<form name="mp" action="list?pr=${priority}&st=${state}" method="post">
<input type="hidden" name="cp" value="${pagerInfo.currentPage}"/>
<input type="hidden" name="idf" value="${pagerInfo.idForward}"/>
<input type="hidden" name="dtf" value="${pagerInfo.dateForward}"/>
<input type="hidden" name="idb" value="${pagerInfo.idBackward}"/>
<input type="hidden" name="dtb" value="${pagerInfo.dateBackward}"/>
<input type="hidden" name="dir"/>
</form>

<script>
function applyFilter()
{
	document.fb.submit();
}
function on_hover(row)
{
	row.style.backgroundColor='#eeeeff';
}
function on_out(row)
{
	row.style.backgroundColor='#ffffff';
}
function movePage(d)
{
	document.mp.dir.value=d;
	document.mp.submit();
}
function reportNew()
{
<%
UserService userService = UserServiceFactory.getUserService();
if (userService.isUserLoggedIn()) { %>
	document.location.href = 'report';
<% } else { %>
	alert('<fmt:message key="alert.needsign.towrite"/>');
	document.location.href = '<%=userService.createLoginURL(request.getContextPath() + "/bugs/report")%>';
<% } %>
}
</script>

<div id="copyright">
<fmt:message key="copyright"/>
</div>
</body>
</html>
