<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="GBK"%>
<%
	String path = request.getContextPath();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="Sun, 6 Mar 2005 01:00:00 GMT">
<link href="http://img3.douban.com/css/packed_douban6963692293.css"
	rel="stylesheet" type="text/css">
<style type="text/css">
.result-item {
	border-bottom: none;
	border-top: 1px dashed #ccc;
	margin: 0;
	padding: 10px 0
}

.result-item .pic {
	display: inline-block;
	zoom: 1;
	*display: inline;
	text-align: left;
	width: 100px
}

.result-item .content {
	color: #666;
	display: inline-block;
	zoom: 1;
	*display: inline;
	width: 460px;
	vertical-align: top;
}

.result-item .footer {
	padding-top: 10px;
}

.result-item em {
	font-style: normal;
	font-weight: normal
}

.result-item .content h3 {
	background: none;
	margin: 0 0 5px
}

.result-item .content h3 em {
	font-size: 12px;
	text-align: center;
	background: #82d6fe;
	color: #fff;
	display: inline-block;
	overflow: hidden;
	*display: inline;
	zoom: 1;
	border-radius: 3px;
	-webkit-border-radius: 3px;
	-moz-border-radius: 3px;
	padding: 0.1em 0.5em;
	margin-left: 5px;
	line-height: 16px
}

.result-item .gact {
	text-align: left;
	margin-top: 10px
}

.result-site .pic {
	width: 80px;
}

.content.musician ul {
	margin-top: 20px;
}

p.first {
	margin-top: 5
}

.followers {
	margin-top: 5px;
}

.followers .pl {
	color: #999;
}

a.start_radio_musician {
	background: url('/pics/radio_8_gray-1.jpg');
	display: inline-block;
	width: 43px;
	height: 16px;
	margin-left: 16px;
	margin-bottom: -2.5px;
}

@media screen and (-webkit-min-device-pixel-ratio:0) {
	a.start_radio_musician {
		margin-bottom: -4px;
	}
}

a.start_radio {
	background: url('/pics/radio_8_gray-1.jpg');
	display: inline-block;
	width: 43px;
	height: 16px;
	margin-left: 16px;
	margin-bottom: -2.5px;
}
</style>

<style type="text/css">
.nav-srh .inp {
	position: relative;
	z-index: 40
}

#search_suggest {
	background: #fff;
	border: 1px solid #eee;
	position: absolute;
	z-index: 99;
	top: 32px;
	width: 303px;
	box-shadow: 0 1px 2px rgba(0, 0, 0, .3);
	border-bottom: 0 none
}

#search_suggest li {
	border-bottom: 1px solid #eee;
	overflow: hidden
}

#search_suggest
      li.curr_item {
	background: #efefef
}

#search_suggest li
      a {
	color: #999;
	display: block;
	overflow: hidden;
	padding: 6px;
	zoom: 1
}

#search_suggest li
      a:hover {
	background: #f9f9f9;
	color: #999
}

#search_suggest li a em {
	font-style: normal;
	color: #369
}

#search_suggest li
      p {
	margin: 0;
	zoom: 1;
	overflow: hidden
}

#search_suggest li
      img {
	float: left;
	margin-right: 8px;
	margin-top: 3px
}

#search_suggest li.over {
	background: #fbfbfb
}

.searchtype {
	margin-left: 160px;
	margin-top: -35px;
	z-index: 40
}

input {
	height: 30px;
}

input[type=submit] {
	width: 50px;
}
</style>
<script type="text/javascript" src="<%=path%>/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript"
	src="<%=path%>/js/jquery-ui-1.8.18.custom.min.js"></script>
<script type="text/javascript"
	src="<%=path%>/js/jquery.extend.uilocker.js"></script>
<script type="text/javascript">
	function play(_movieId) {
		var href = "/cminer-trial/api/behavior/addBehavior/${userId}?type=0&itemId="
				+ _movieId + "&value=1.0";
		$
				.uiLock("<div style='height: 30%; width: 100%;'>&nbsp'</div>"
						+ "<div align='center' style='width: 100%; font-size: 40; opacity: 1.0;'><b>Saving behavior<br>and re-directing...</b></div>");
		$.ajax({
			url : href,
			cache : false,
			success : function() {
				$(".back").click();
			},
			error : function(data) {
				alert("Error: msg = " + data);
				$(".back").click();
			}
		});
	}
	function back() {
		window.location.href = "/cminer-trial/api/recommend/user/recommend?size=${param.size}&userId=${userId}";
	}
</script>
</head>
<body>
	<div class="top-nav-items">
		<ul>
			<li></li>
		</ul>
	</div>
	<div id="wrapper">
		<form name="ssform" method="post" action="view">
			<div id="header">
				<div class="bd">
					<div class="nav-srh">
						<div class="inp">
							<span>
								<input name="genre" type="text" title="类别" value="<c:out value='${genre}'/>" />
								&nbsp;&nbsp;&nbsp;&nbsp;<input name="downloadTimesFrom" type="text" title="下载下限" value="<c:out value='${downloadTimesFrom}'/>" />
								<b>-</b>&nbsp;&nbsp;<input name="downloadTimesTo" type="text" title="下载上限" value="<c:out value='${downloadTimesTo}'/>" />
								<input name="pageNo" type="hidden" value="<c:out value='${pageNo}'/>" />
							</span> <span> <input class="bn-srh" type="submit" value="搜索" />
							</span>
						</div>
					</div>
				</div>
				<div class="ext"></div>
			</div>
		</form>
		<div id="content">

			<div class="grid-16-8 clearfix">
				<div class="article">
					<p class="ul first"></p>

					<table width="100%">
						<tr class="item">
							<td width="100" valign="top">
								<div class="paginator">
									<c:choose>
										<c:when test="${pageNo==1}">
											<span class="prev"> &lt;前页 </span>
										</c:when>
										<c:otherwise>
											<span class="prev"> <a
												href="javascript:postAction('${pageNo-1}')"> &lt;前页 </a>
												<link rel="prev" href="#" />
											</span>
										</c:otherwise>
									</c:choose>
									<c:forEach var="pageNo" begin="1" end="${pageNum}" step="1">
										<c:choose>
											<c:when test="${pageNo==currentPageNo}">
												<span class="thispage"> <c:out value='${pageNo}' />
												</span>
											</c:when>
											<c:otherwise>
												<a href="javascript:postAction('${pageNo}')"> <c:out
														value='${pageNo}' />
												</a>
											</c:otherwise>
										</c:choose>
									</c:forEach>
									<c:choose>
										<c:when test="${pageNo==pageNum}">
											<span class="next"> 后页&gt; </span>
										</c:when>
										<c:otherwise>
											<span class="next"> <a
												href="javascript:postAction('${pageNo+1}')"> 后页&gt; </a>
												<link rel="next" href="#" />
											</span>
										</c:otherwise>
									</c:choose>
									<span class="count"> (共<c:out value='${appsNum}' />条)
									</span>
								</div>
							</td>
						</tr>
					</table>
					
					<c:forEach var="app" items="${apps}" varStatus="status">
						<table width="100%">
							<tr class="item">
								<td valign="top">
									<div class="pl2">
										<b>《<c:out value='${app.genre}' />》
										</b> &nbsp;&nbsp;&nbsp;&nbsp;
										<c:out value='${app.downloadTimes}' />
										&nbsp;&nbsp;&nbsp;&nbsp; <b><a href="${app.url}"
											target="_Blank"><c:out value='${app.name}' /></a></b>
									</div>
								</td>
							</tr>
						</table>
						<p class="ul"></p>
					</c:forEach>

					<table width="100%">
						<tr class="item">
							<td width="100" valign="top">
								<div class="paginator">
									<c:choose>
										<c:when test="${pageNo==1}">
											<span class="prev"> &lt;前页 </span>
										</c:when>
										<c:otherwise>
											<span class="prev"> <a
												href="javascript:postAction('${pageNo-1}')"> &lt;前页 </a>
												<link rel="prev" href="#" />
											</span>
										</c:otherwise>
									</c:choose>
									<c:forEach var="pageNo" begin="1" end="${pageNum}" step="1">
										<c:choose>
											<c:when test="${pageNo==currentPageNo}">
												<span class="thispage"> <c:out value='${pageNo}' />
												</span>
											</c:when>
											<c:otherwise>
												<a href="javascript:postAction('${pageNo}')"> <c:out
														value='${pageNo}' />
												</a>
											</c:otherwise>
										</c:choose>
									</c:forEach>
									<c:choose>
										<c:when test="${pageNo==pageNum}">
											<span class="next"> 后页&gt; </span>
										</c:when>
										<c:otherwise>
											<span class="next"> <a
												href="javascript:postAction('${pageNo+1}')"> 后页&gt; </a>
												<link rel="next" href="#" />
											</span>
										</c:otherwise>
									</c:choose>
									<span class="count"> (共<c:out value='${appsNum}' />条)
									</span>
								</div>
							</td>
						</tr>
					</table>
				</div>
			</div>
		</div>

        <form name="postForm" action="view" method="post">
          <input name="genre" type="hidden" value="<c:out value='${genre}'/>"/>
          <input name="downloadTimesFrom" type="hidden" value="<c:out value='${downloadTimesFrom}'/>" />
          <input name="downloadTimesTo" type="hidden" value="<c:out value='${downloadTimesTo}'/>" />
          <input name="pageNo" type="hidden" value="<c:out value='${pageNo}'/>" />
		</form>
		<div style="clear: both;"></div>
		<div align="center">Google Play</div>
</body>

<script type="text/javascript">
	function postAction(_pageNo) {
		document.postForm.pageNo.value = _pageNo;
		document.postForm.submit();
	}
</script>

</html>