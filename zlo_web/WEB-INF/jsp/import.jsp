<%@ page
        import="info.xonix.zlo.search.model.Site, info.xonix.zlo.search.db.DbAccessor, info.xonix.zlo.web.servlets.BaseServlet, info.xonix.zlo.web.servlets.test.TestLazy1, info.xonix.zlo.web.servlets.test.t.Obj, info.xonix.zlo.web.utils.RequestUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ taglib prefix="xonix" uri="http://xonix.info" %>

<fmt:setBundle basename="info.xonix.zlo.search.config.config" />
<fmt:setLocale value="ru_RU" scope="request" />