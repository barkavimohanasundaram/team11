<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>


<!DOCTYPE html>
<html>
    <head>
        <title>TODO supply a title</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <script>
        
    </script>
    <body>
        <h1>LOGIN HERE</h1>

        <form method="POST" action="verify">
            <table>

                <tr>
                    <td>Player Name:</td>
                    <td>
                        <input type="text" size="30" name="name" id="name"/>
                    <td>
                </tr>
                <tr>
                    <td>Password</td>
                    <td>
                        <input type="password" size="30"  name="password" id="password"/>
                    <td><br><br>
                </tr>

                <tr>
                    <td colspan="2">
                        <button type="submit">Login</button>

                        <button type="button" onclick="openPage('index.jsp')">Register</button>
                    </td>

                </tr>
            </table>
            <div style="color:red;">${message}</div><br><br>

        </form>
        <script type="text/javascript">
            document.getElementById("password").value="";
            function openPage(pageURL)
            {
            window.location.href = pageURL;
            }
            </script>
            </body>
                    </html>
