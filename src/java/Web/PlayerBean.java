package Web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;

@Stateless
public class PlayerBean {
    
    private static final String INSERT_PLAYER = "insert into player values (?,?,?)";
    
    @Resource(lookup = "jdbc/unogame") DataSource ds;

	@PostConstruct
	private void init() {
		System.out.println(">>> playerBean created");
	}

	@PreDestroy
	private void cleanup() {
		System.out.println(">>> clean up");
	}

	public void createPlayer(Playerlogin p) throws SQLException
        {
            try (Connection conn = ds.getConnection())
              {
    
                PreparedStatement ps = conn.prepareStatement(INSERT_PLAYER);
                ps.setInt(1, p.getPlayerId());
		ps.setString(2, p.getName());
		ps.setString(3, p.getPassword());
		ps.executeUpdate();
	      }
             catch (SQLException ex) 
             {
		ex.printStackTrace();
	     }
         }
}
