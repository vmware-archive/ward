<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Ward</title>
<link rel="stylesheet"
	href="${commonUtils.webjarsResource('bootstrap', 'css/bootstrap.min.css')}">
<link rel="stylesheet" href="${commonUtils.rootContextPath}/styles.css">
<!--[if lt IE 9]>
<script src="${commonUtils.webjarsResource('html5shiv', 'html5shiv.min.js')}"></script>
<script src="${commonUtils.webjarsResource('respond', 'respond.min.js')}"></script>
<![endif]-->
</head>
<body>
	<header class="navbar navbar-inverse navbar-fixed-top" role="banner">
		<div class="container">
			<div class="navbar-header">
				<a href="${commonUtils.rootContextPath}" class="navbar-brand">Ward</a>
			</div>
			<nav role="navigation">
				<ul class="nav navbar-nav">
					<c:forEach var="application" items="${commonUtils.applications}">
						<c:choose>
							<c:when
								test="${application eq commonUtils.getApplication(pageContext.request)}">
								<li class="active"><a href="${application.contextPath}">${commonUtils.getMessage(application, 'title', null, pageContext.request.locale)}</a></li>
							</c:when>
							<c:otherwise>
								<li><a href="${application.contextPath}">${commonUtils.getMessage(application, 'title', null, pageContext.request.locale)}</a></li>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</ul>
			</nav>
		</div>
	</header>
	<div class="container">
		<div class="jumbotron">
			<h1>Ward</h1>
			<p>Ward is a simple library that enables runtime modularity
				for Java Servlet Conteiners. It's built on top of the Spring
				Framework and offers a lightweight alternative for scenarios that
				otherwise require an OSGi container.</p>
		</div>
	</div>
	<footer>
		<div class="container">
			<p class="text-muted">Copyright &copy; 2013 VMware, Inc. All
				rights reserved.</p>
		</div>
	</footer>
</body>
</html>
