package Web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;

@Stateless
public class LoginBean {

private static final String validate = "select * from player where playername = ? and password = ?";
    
    @Resource(lookup = "jdbc/unogame") DataSource ds;

	@PostConstruct
	private void init() {
		System.out.println(">>> LoginBean created");
	}

	@PreDestroy
	private void cleanup() {
		System.out.println(">>> clean up");
	}

	public boolean find(Playerlogin p) throws SQLException
        {
            boolean status=true;
            try (Connection conn = ds.getConnection())
              {
    
                PreparedStatement ps = conn.prepareStatement(validate);
                ps.setString(1, p.getName());
	        ps.setString(2, p.getPassword());
                ResultSet rs = ps.executeQuery();
                if(rs.next())
                {
		   
                    status=true;
                }
                else
                {
                    status=false;
                }
                 
              }
             catch (SQLException ex) 
             {
		ex.printStackTrace();
	     }
            
            
          return status;
            
        }

}