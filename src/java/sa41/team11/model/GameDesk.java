/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sa41.team11.model;

import java.util.LinkedList;
import java.util.List;
import javax.websocket.Session;

/**
 *
 * @author HeinHtetZaw
 */
public class GameDesk {

    private String createdBy;
    private String Description;
    private Table table;
    private int NoOfMaxPlayer;
    private int turn = 0;
    private int direction = 0; // non-inverse
    private List<Player> players;
    private String choosenColor;
    private int wildCount;
    private String GameStatus;

    //this msg have to call before "Turn" change
    //before playerDropCard
    public Player getCurrentPlayer() {
        return players.get(turn);
    }

    //Return "Success" ==> Drop Card Success
    //Return "WrongTurn" ==> Player Drop Turn Wrong
    //Return "WrongCard" ==> Card is not found in player in hand
    //Return "Fail" ==> Smth Wrong in function
    //Return "EmptyDrawPile" ==> Draw Pile is now empty
    public String playerDropCard(String color, String val, Session s, String ccolor) {
        System.out.println("in playerDropCard function from GameDesk.java");

        //get player
        Player player = null;
        if (players.get(turn).getSession().getId().equals(s.getId())) {
            System.out.println("Found Player");
            player = players.get(turn);
        } else {
            System.out.println("Wrong Turn");
            return "WrongTurn";
        }

        //get card from player
        Card pc = player.dropCardPlayer(color, val);
        if (null == pc) {
            System.out.println("Cant found card from player");
            player.getCardInHand().add(pc);
            return "WrongCard";
        }

        //get last card from table
        Card tc = table.getDiscardedPile().get(table.getDiscardedPile().size() - 1);
        if (pc.getClass().getName().equals("sa41.team11.model.WildCard")) {
            //if player drop wild card
            System.out.println("Player card is wild card");
            WildCard wc = (WildCard) pc;
            System.out.println("Wilde Card value is " + wc.getValue());
            System.out.println("Wilde Card color is " + ccolor);
            choosenColor = ccolor;
            if (wc.getValue().equals("W4")) {
                this.wildCount += 4;
                this.table.getDiscardedPile().add(pc);
                return "Draw4Card";
            }
            this.table.getDiscardedPile().add(pc);
            return "Success";
        } else if (pc.getClass().getName().equals("sa41.team11.model.ActionCard")) {
            //player drop action card        
            System.out.println("Player Card is action Card");
            ActionCard pac = (ActionCard) pc;

            if (tc.getClass().getName().equals("sa41.team11.model.WildCard")) {
                //in table is Wilde Card
                System.out.println("Table card is Wild card");
                if (!this.choosenColor.equals(pac.getColor())) {
                    return "Fail";
                }
                this.choosenColor = "NA";
                this.wildCount = 0;
                if (pac.getValue().equals("Rev")) {//Player Card is Rev
                    changeDirection();
                } else if (pac.getValue().equals("Plus2")) {//player card is plus 2
                    Player p = nextPlayer("NA");
                    p.drawCard(table.drawCard());
                    if(table.getDrawPile().size()==0){
                        return "EmptyDrawPile";
                    }
                    p.drawCard(table.drawCard());
                    if(table.getDrawPile().size()==0){
                        return "EmptyDrawPile";
                    }
                } else {//player card is skip
                    Player p = nextPlayer("NA");
                }
                table.getDiscardedPile().add(pc);
                return "Success";
            } else {
                // in table is color card
                System.out.println("table card is color");
                ColorCard tcc = (ColorCard) tc;
                System.out.println("Table card - " + tcc.getColor());
                if (tcc.getColor().equals(pac.getColor())) {
                    table.getDiscardedPile().add(pac);
                    if (pac.getValue().equals("Rev")) {
                        //if player card value is Reverse
                        System.out.println("Player card is Reverse");
                        changeDirection();
                    } else if (pac.getValue().equals("Plus2")) {
                        System.out.println("Player card is Plus2");
                        Player p = nextPlayer("NA");
                        p.drawCard(table.drawCard());
                        if(table.getDrawPile().size()==0){
                        return "EmptyDrawPile";
                    }
                        p.drawCard(table.drawCard());
                        if(table.getDrawPile().size()==0){
                        return "EmptyDrawPile";
                    }
                    } else if (pac.getValue().equals("Skip")) {
                        System.out.println("Player card is Skip");
                        Player p = nextPlayer("NA");
                    }
                    return "Success";
                }
            }
        } else if (pc.getClass().getName().equals("sa41.team11.model.NumberCard")) {
//player dropped Number card
            System.out.println("Player card is number card");
            NumberCard pnc = (NumberCard) pc;
            System.out.println("Player card - " + pnc.getColor() + "," + pnc.getValue());
            if (tc.getClass().getName().equals("sa41.team11.model.WildCard")) {
//on table is wild card
                System.out.println("ChoosenColor" + this.choosenColor);
                System.out.println("wildCount" + this.wildCount);
                System.out.println("Table cared is wild card");
                if (!this.choosenColor.equals(pnc.getColor())) {
                    return "Fail";
                }
                this.choosenColor = "NA";
                this.wildCount = 0;
                table.getDiscardedPile().add(pc);
                return "Success";
            } else if (tc.getClass().getName().equals("sa41.team11.model.NumberCard")) {
                System.out.println("Table card is number card");
                NumberCard tnc = (NumberCard) tc;
                if (tnc.getColor().equals(pnc.getColor()) || tnc.getValue().equals(pnc.getValue())) {
                    table.getDiscardedPile().add(pc);
                    return "Success";
                }
            } else if (tc.getClass().getName().equals("sa41.team11.model.ActionCard")) {
                System.out.println("table card is action card");
                ActionCard tac = (ActionCard) tc;
                if (tac.getColor().equals(pnc.getColor())) {
                    table.getDiscardedPile().add(pc);
                    return "Success";
                }
            }
        }
        System.out.println("Card is Fail");
        return "Fail";
    }

    public Player nextPlayer(String drawCard) {
        System.out.println("in nextPlayer function from GameDesk.java");
        System.out.println("Direction is " + direction);
        System.out.println("Turn is " + turn);
        if (direction == 0) {
            if (turn == players.size() - 1) {
                turn = 0;
            } else {
                turn += 1;
            }
        } else if (direction == 1) {
            if (turn == 0) {
                turn = players.size() - 1;
            } else {
                turn -= 1;
            }
        }
        System.out.println("Turn - " + turn);
        Player p = players.get(turn);
        if (drawCard.equals("drawCard")) {
            p.drawCard(table.drawCard());
            p.drawCard(table.drawCard());
            p.drawCard(table.drawCard());
            p.drawCard(table.drawCard());
        }
        return p;
    }

    public void changeDirection() {
        System.out.println("in changeDirection function from GameDesk.java");
        if (direction == 0) {
            this.direction = 1;
        } else {
            this.direction = 0;
        }
    }

    public GameDesk(String createdBy, String Description, Table table, int NoOfMaxPlayer, List<Player> players) {
        this.createdBy = createdBy;
        this.Description = Description;
        this.table = table;
        this.NoOfMaxPlayer = NoOfMaxPlayer; //can't b more than 10player
        this.players = players;
        this.choosenColor = "NA";
        this.wildCount = 0;
        GameStatus = "FindingPlayer";
    }

    public GameDesk(String createdBy, String Description, Table table, int NoOfMaxPlayer) {
        this.createdBy = createdBy;
        this.Description = Description;
        this.table = table;
        this.players = new LinkedList<Player>();
        this.NoOfMaxPlayer = NoOfMaxPlayer;
        this.choosenColor = "NA";
        this.wildCount = 0;
        GameStatus = "FindingPlayer";
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getChoosenColor() {
        return choosenColor;
    }

    public void setChoosenColor(String choosenColor) {
        this.choosenColor = choosenColor;
    }

    public int getWildCount() {
        return wildCount;
    }

    public void setWildCount(int wildCount) {
        this.wildCount = wildCount;
    }

    public String getGameStatus() {
        return GameStatus;
    }

    public void setGameStatus(String GameStatus) {
        this.GameStatus = GameStatus;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public int getNoOfMaxPlayer() {
        return NoOfMaxPlayer;
    }

    public void setNoOfMaxPlayer(int NoOfMaxPlayer) {
        this.NoOfMaxPlayer = NoOfMaxPlayer;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

}
