package Web;

import java.io.IOException;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@WebServlet("/add")
public class AddPlayer extends HttpServlet {

    //  private static final String INSERT_PLAYER = "insert into player values (?, ?)";
    @Resource(lookup = "jdbc/unogame")
    private DataSource ds;

    @Inject
    private Playerlogin player;
    @EJB
    private PlayerBean playerbean;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {

           // if (!req.getParameter("playerid").isEmpty()) {
                if ((!req.getParameter("name").isEmpty()) && (!req.getParameter("password").isEmpty())) {
                    //player.setPlayerId(Integer.parseInt(req.getParameter("playerid")));
                    player.setPlayerId(0);
                    player.setName(req.getParameter("name"));
                    player.setPassword(req.getParameter("password"));
                    playerbean.createPlayer(player);
                //}
            }
            else
            {
                 req.setAttribute("message", "Enter valid playername and password");
               req.getRequestDispatcher("index.jsp")
                        .forward(req, resp);
            }

//int id=Integer.parseInt(req.getParameter("playerid"));
//            player.setPlayerId(Integer.parseInt(req.getParameter("playerid")));
//            player.setName( req.getParameter("name"));
//              player.setPassword( req.getParameter("password"));
//            if (req.getParameter("name").isEmpty() || req.getParameter("password").isEmpty()) {
//                req.setAttribute("message", "Enter valid playername and password");
//                req.getRequestDispatcher("index.jsp")
//                        .forward(req, resp);
//            }
            // player.setName(PlayerName);
            // player.setPassword(Password);
//            playerbean.createPlayer(player);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
       // req.setAttribute("message", "Registered succesfully!!!");
        req.getRequestDispatcher("Login.jsp")
                .forward(req, resp);
    }
}
