<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
    <head>
        <title>TODO supply a title</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <style> 

    </style>
    <body>
        <h1>REGISTER HERE</h1>

        <form method="POST" action="add">
            <table>
                <tr>
                    <td>Player Name:</td>
                    <td>
                        <input type="text" size="30" name="name"/>
                    <td>
                </tr>
                <tr>
                    <td>Password</td>
                    <td>
                        <input type="text" size="30" name="password"/>
                    <td>
                </tr>

                <tr>
                    <td colspan="2">
                        <button type="submit">Register</button>
                    </td>

                </tr>
            </table>
            <div style="color:red;">${message}</div><br><br>
        </form>

    </body>
</html>
