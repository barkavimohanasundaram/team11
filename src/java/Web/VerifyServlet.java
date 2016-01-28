package Web;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/verify")
public class VerifyServlet extends HttpServlet {

    @Inject
    private Playerlogin player;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String PlayerName = req.getParameter("name");

        String Password = req.getParameter("password");

        if (PlayerName.isEmpty() || Password.isEmpty()) {
            req.setAttribute("message", "Enter valid Username and Password.");
                req.getRequestDispatcher("Login.jsp")
                        .forward(req, resp);
        }
        else{
       
        player.setName(req.getParameter("name"));
        player.setPassword(req.getParameter("password"));

        req.setAttribute("player", player);

        req.getRequestDispatcher("login")
                .forward(req, resp);
        }
    }

}