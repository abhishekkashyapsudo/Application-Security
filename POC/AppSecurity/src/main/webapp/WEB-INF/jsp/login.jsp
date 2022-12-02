<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta
  http-equiv="Content-Security-Policy"
  content="default-src 'self'; img-src https://*; child-src 'none';" />
  <meta name="referrer" content="strict-origin" />
  
<title>Ecom Login Form</title>
</head>
<body>
<form action="/api/login" method="post">
<%String error = (String)request.getSession().getAttribute("error");
	if(error==null)
		error= "";
	
%>
		<span style="color:red"><%=error%></span>
		<table style="with: 50%">

			<tr>
				<td>UserName</td>
				<td><input type="text" name="username" /></td>
			</tr>
				<tr>
				<td>Password</td>
				<td><input type="password" name="password" /></td>
			</tr>
			<tr>
					<td>Please Enter Captcha</td>
					<td><img src="data:image/png;base64,<%= request.getSession().getAttribute("captcha") %>"/><br/>
					<input type="text" name="captcha" id="captcha"/>
					</td>
				</tr>
		</table>
		<input type="submit" value="Login" /></form>
		<a href="/ui/register">Register here</a>
</body>
</html>