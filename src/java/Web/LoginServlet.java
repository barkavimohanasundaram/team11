package Web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Inject
    private Playerlogin player;
    @EJB
    private LoginBean logbean;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            player = (Playerlogin) req.getAttribute("player");

            boolean q = logbean.find(player);
            if (q ==false) {
                req.setAttribute("message", "UnAuthorised User");
                req.getRequestDispatcher("Login.jsp")
                        .forward(req, resp);
            }
            else
            {
                req.setAttribute("value", player.getName());
                req.getRequestDispatcher("home.jsp")
                .forward(req, resp);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        
    }

}
