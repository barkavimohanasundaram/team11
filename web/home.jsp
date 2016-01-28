<%-- 
    Document   : homw
    Created on : Jan 25, 2016, 2:02:05 PM
    Author     : BarkaviMohan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="lib/jquery-2.2.0.js"></script>
        <script src="index.js"></script>
        <title>JSP Page</title>
    </head>

    <body>
        <%
            Object value = request.getAttribute("value");
        %>
        <h1>Hi!! <%=value%> Welcome to the unogame.... </h1>
        <a href="createGame.html"><button id="table">Table</button></a>
    <a href="connectGame.html"><button id = "player">Player</button></a>
</html>
