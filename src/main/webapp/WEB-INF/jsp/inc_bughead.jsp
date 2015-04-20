<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@ page import="com.google.appengine.api.users.UserService"
%><%@ page import="com.google.appengine.api.users.UserServiceFactory"
%>
<form name="chlang" action="/etc/chlang" id="authbox">
<%
	String nickname,signUrl;
	UserService userService = UserServiceFactory.getUserService();
	if (userService.isUserLoggedIn())
	{
		nickname = userService.getCurrentUser().getNickname();
		signUrl = userService.createLogoutURL("/bugs/list");
		pageContext.setAttribute("signLabel","label.signout");
	}
	else
	{
		nickname = null;
		signUrl = userService.createLoginURL(request.getContextPath() + request.getParameter("contiUrl"));
		pageContext.setAttribute("signLabel","label.signin");
	}

	if (nickname != null) { %><%=nickname%> | <% }
%>
<input type="hidden" name="url"/>
<img src="http://developer.android.com/assets/images/icon_world.jpg" alt="Language" style="vertical-align: middle"/>
<select name="lang" onchange="document.chlang.url.value=document.location.href;document.chlang.submit()">
	<option value="0">English</option>
	<option value="1"<c:if test="${sessionScope['javax.servlet.jsp.jstl.fmt.locale.session'] == 'ko_KR'}"> selected</c:if>>한국어</option>
</select>
| <a href="<%=signUrl%>"><fmt:message key="${signLabel}"/></a>
</form>
<div id="titlebox">
	<div class="page_title">ABCDSee Bug Tracker
	<img src="http://code.google.com/appengine/images/appengine-noborder-120x30.gif" alt="Powered by Google App Engine" style="vertical-align:top;"/>
	</div>
	<div id="tabbox">
		<c:choose><c:when test="${param.seltab == 0}">
			<span class="tab_current">Future TODOs</span>
		</c:when><c:otherwise>
			<a href="<c:url value='/todo/list'/>" class="tab_other">Future TODOs</a>
		</c:otherwise></c:choose>

		&nbsp;|&nbsp;

		<c:choose><c:when test="${param.seltab == 1}">
			<span class="tab_current">Current Bugs</span>
		</c:when><c:otherwise>
			<a href="<c:url value='/bugs/list'/>" class="tab_other">Current Bugs</a>
		</c:otherwise></c:choose>

		&nbsp;|&nbsp;

		<c:choose><c:when test="${param.seltab == 2}">
			<span class="tab_current">Previous Bugs</span>
		</c:when><c:otherwise>
			<a href="<c:url value='/bugs/prevlist'/>" class="tab_other">Previous Bugs</a>
		</c:otherwise></c:choose>
		<span id="tab_about"><a href="/etc/about">About ABCDSee</a></span>
	</div>
</div>
