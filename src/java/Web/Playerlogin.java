package Web;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;

@RequestScoped
public class Playerlogin implements Serializable {

	private static final long serialVersionUID = 1L;

	private int playerId;
	private String name;
	private String password;
	
        public Playerlogin(int playerId, String name, String password) {
        this.playerId = playerId;
        this.name = name;
        this.password = password;
         }

    public Playerlogin() {}

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

   
    public Playerlogin copy() {
		Playerlogin m = new Playerlogin();
		m.playerId = playerId;
		m.name = name;
                m.password = password;
		return (m);
	}

    @Override
    public String toString() {
        return "Player{" + "playerId=" + playerId + ", name=" + name + ", password=" + password + '}';
    }

	
	
}
    
