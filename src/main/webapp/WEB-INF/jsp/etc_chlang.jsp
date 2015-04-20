<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><html><body>
<script><fmt:setLocale value="${lang}" scope="session"/>
window.location.href='${url}';
</script>
</body></html>