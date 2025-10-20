<%-- 
    Document   : login
    Created on : Aug 12, 2025, 2:08:02 PM
    Author     : Anne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <form action="Login" method="POST">
            <input type="text" id="name" name="name"><!-- comment -->
            <input type="password" id="password" name="password">
            <button type="submit">Submit</button>
        </form>
    </body>
</html>
