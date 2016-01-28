/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sa41.team11.control;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.websocket.Session;
import sa41.team11.model.ActionCard;
import sa41.team11.model.Card;
import sa41.team11.model.GameDesk;
import sa41.team11.model.NumberCard;
import sa41.team11.model.Player;
import sa41.team11.model.Table;
import sa41.team11.model.WildCard;

/**
 *
 * @author HeinHtetZaw
 */
@ApplicationScoped
public class Game {

    private Map<String, GameDesk> gameDeskList = new HashMap<>();
    private List<Session> unconnectedlist = new LinkedList<>();
    private String choosencolor = "NA";

    public String noCallUnoGameFinished(String gameid, Session s) {
        System.out.println("in noCallUnoGameFinished function from Game.java");
        GameDesk gd = gameDeskList.get(gameid);
        Player p = null;
        for (Player p1 : gd.getPlayers()) {
            if (p1.getSession().equals(s)) {
                p = p1;
            }
        }
        if (null == p) {
            System.out.println("Player not found");
            return "Fail";
        }
        p.drawCard(gd.getTable().getDrawPile().remove(0));
        if (gd.getTable().getDrawPile().size() == 0) {
            gamefinish(gameid);
            return "";
        }

        p.drawCard(gd.getTable().getDrawPile().remove(0));
        if (gd.getTable().getDrawPile().size() == 0) {
            gamefinish(gameid);
            return "";
        }
        updateCardListsTable(gameid);
        UpdateCardListPlayer(gameid);
        return "Success";
    }

    public int calculatePoints(List<Card> clist) {
        int toRet = 0;
        for (Card c : clist) {
            toRet += c.getPoint();
        }
        return toRet;
    }

    public String disconnectclient(Session s) {
        GameDesk gd = null;
        for (String gameid : gameDeskList.keySet()) {
            gd = gameDeskList.get(gameid);
            if (s.getId().equals(gd.getTable().getSessionid().getId())) {
                return gameid;
            }
            for (Player p : gd.getPlayers()) {
                if (s.getId().equals(p.getSession().getId())) {
                    return gameid;
                }
            }
        }
        return "Fail";
    }

    public String gamefinish(String gameid) {
        System.out.println("in gamefinish function from Game.java");
        GameDesk gd = gameDeskList.get(gameid);
        int totalScore = 0;
        JsonObjectBuilder outerJson = Json.createObjectBuilder();
        JsonArrayBuilder jsarray = Json.createArrayBuilder();
        JsonObjectBuilder innerJson = null;

        outerJson.add("ConnectType", "finishGameList");
        outerJson.add("ConnectBy", "Server");

        for (Player p : gd.getPlayers()) {
            p.setScore(calculatePoints(p.getCardInHand()));//calculate and set points
            totalScore += p.getScore();
        }

        for (Player p : gd.getPlayers()) {
            innerJson = Json.createObjectBuilder();
            innerJson.add("playerName", p.getName());
            if (p.getScore() > 0) {
                innerJson.add("score", p.getScore());
            } else {
                innerJson.add("score", totalScore);
                p.setScore(totalScore);
            }
            jsarray.add(innerJson);
        }
        outerJson.add("data", jsarray);
        JsonObject outerjobj = outerJson.build();
        try {
            gd.getTable().getSessionid().getBasicRemote().sendText(outerjobj.toString());
            for (Player p : gd.getPlayers()) {
                p.getSession().getBasicRemote().sendText(outerjobj.toString());
            }
        } catch (Exception ex) {
            System.out.println("Exception in gamefinish");
        }
        return "Success";
    }

    public String PassingMessage(String Msg, String gameid) {
        System.out.println("in PassingMessage function from Game.java");
        GameDesk gd = gameDeskList.get(gameid);
        JsonObjectBuilder jsonbuilder = Json.createObjectBuilder();
        jsonbuilder.add("ConnectType", "PassingMessage");
        jsonbuilder.add("ConnectBy", "Server");
        String playerName = gd.getCurrentPlayer().getName();
        jsonbuilder.add("Data", playerName + ":" + Msg);
        JsonObject jobj = jsonbuilder.build();
        try {
            gd.getTable().getSessionid().getBasicRemote().sendText(jobj.toString());
            for (Player p : gd.getPlayers()) {
                p.getSession().getBasicRemote().sendText(jobj.toString());
            }
        } catch (Exception ex) {
            System.out.println("Exception in PassingMessage");
        }
        return "Success";
    }

    public String connectTable(String gameId, Session s) {
        System.out.println("in connectTable function from Game.java");
        System.out.println(">>>>>>>connectTable function");
        Player p = new Player(s, "PlayerName");
        String ret = addplayer(gameId, p);
        if (ret.equals("PlayerIsAdded")) {
            System.out.println("UnconnectedList size - " + unconnectedlist.size());
            unconnectedlist.remove(s);
            System.out.println(">>UnconnectedList size - " + unconnectedlist.size());
            JsonObjectBuilder JsonBuilder = Json.createObjectBuilder();
            JsonBuilder.add("ConnectType", "PlayerConnectRet");
            JsonBuilder.add("ConnectBy", "Server");
            JsonBuilder.add("Data", "Success");
            JsonObject json = JsonBuilder.build();
            try {
                s.getBasicRemote().sendText(json.toString());
                JsonObjectBuilder JsonBuilder1 = Json.createObjectBuilder();
                JsonBuilder1.add("ConnectType", "SendJoinedPlayerNo");
                JsonBuilder1.add("ConnectBy", "Server");
                JsonBuilder1.add("Data", gameDeskList.get(gameId).getPlayers().size());
                JsonObject json1 = JsonBuilder1.build();
                gameDeskList.get(gameId).getTable().getSessionid().getBasicRemote().sendText(json1.toString());
                return "Success";
            } catch (Exception ex) {
                System.out.println("Exception in connectTable");
            }
        }
        return null;
    }

    public String playerDrawCard(String playerid, String gameid) {
        System.out.println("in playerDrawCard function from Game.java");
        GameDesk gamedesk = gameDeskList.get(gameid);
        System.out.println("Turn - " + gamedesk.getTurn());
        System.out.println("playerid - " + playerid);
        if (gamedesk.getTurn() != Integer.parseInt(playerid)) {
            return "WrongTurn";
        }
        Card c = gamedesk.getTable().getDrawPile().remove(0);
        if (gamedesk.getTable().getDrawPile().size() == 0) {
            gamefinish(gameid);
            return "";
        }
        gamedesk.getPlayers().get(Integer.parseInt(playerid)).drawCard(c);
        updateCardListsTable(gameid);
        UpdateCardListPlayer(gameid);
        return "Success";
    }

    public String addplayer(String gameId, Player player) {
        System.out.println("in addplayer function from Game.java");
        System.out.println("in Addplayer");
        GameDesk gameList = gameDeskList.get(gameId);
        if (gameList != null) {
            System.out.print(">>> Player size" + gameList.getPlayers().size());
            System.out.print(">>> NoOfMaxPlayer" + gameList.getNoOfMaxPlayer());
            if (gameList.getPlayers().size() <= gameList.getNoOfMaxPlayer()) {
                gameList.getPlayers().add(player);
                gameDeskList.put(gameId, gameList);
                return "PlayerIsAdded";
            } else {
                return "GameDeskIsFull";
            }
        } else {
            return "GameDeskIsNotExist";
        }
    }

    public String createGame(String gameId, GameDesk gamedesk) {
        System.out.println("in createGame function from Game.java");
        GameDesk gameIdList = gameDeskList.get(gameId);
        if (gameIdList == null) {
            gameDeskList.put(gameId, gamedesk);
            return "GameIsCreated";
        }
        return "GameIsAlreadyExist";
    }

    public String sendGameDeskList() {
        System.out.println("in sendGameDeskList function from Game.java");
        int size = 0;

        JsonObjectBuilder innerJsonBuilder = Json.createObjectBuilder();
        JsonArrayBuilder arrayJsonBuilder = Json.createArrayBuilder();
        JsonObjectBuilder outerJsonBuilder = Json.createObjectBuilder();

        JsonObject innerJson = null;
        JsonArray arrayJson = null;
        JsonObject outerJson = null;

        System.out.println("gamedesklist " + gameDeskList.toString());
        for (String key : gameDeskList.keySet()) {
            if (gameDeskList.get(key).getPlayers().size() < gameDeskList.get(key).getNoOfMaxPlayer()) {
                if (gameDeskList.get(key).getGameStatus().equals("FindingPlayer")) {
                    System.out.println("now sending");
                    size = gameDeskList.get(key).getPlayers().size();
                    innerJsonBuilder.add("GameID", key);
                    System.out.println("Game ID : " + key);
                    innerJsonBuilder.add("Description", gameDeskList.get(key).getDescription());
                    innerJsonBuilder.add("NoOfPlayer", size);
                    System.out.println("No Of Player : " + size);
                    innerJson = innerJsonBuilder.build();
                    System.out.println("json - " + innerJson.toString());
                    arrayJsonBuilder.add(innerJson);
                }
            }
        }
        arrayJson = arrayJsonBuilder.build();
        System.out.println("builder - " + arrayJson.toString());
        outerJsonBuilder.add("ConnectType", "GameList");
        outerJsonBuilder.add("ConnectBy", "Server");
        if (arrayJson.isEmpty()) {
            outerJson = outerJsonBuilder.add("Data", "Empty").build();
        } else {
            outerJson = outerJsonBuilder.add("Data", arrayJson).build();
        }
        System.out.println("outerJson - " + outerJson.toString());
        try {
            System.out.println(">>unconnectedlist size - " + unconnectedlist.size());
            for (Session s : unconnectedlist) {
                s.getBasicRemote().sendText(outerJson.toString());
            }
        } catch (Exception ex) {
            System.out.println("Exception in sendGameDeskList");
            return "Exception";
        }
        System.out.println("JSON is sent to all unconnectList");
        return "Success";
    }

    public String addUnconnectedList(Session s) {
        System.out.println("in addUnconnectedList function from Game.java");
        unconnectedlist.add(s);
        return null;
    }

    public String gameStartMsgSend(String gameid) {
        System.out.println("in gameStartMsgSend function from Game.java");
        GameDesk gd = gameDeskList.get(gameid);
        List<Player> playerList = gd.getPlayers();

        //builder for table
        JsonObjectBuilder tableJsonBuilder = Json.createObjectBuilder();
        tableJsonBuilder.add("ConnectType", "GameStartSuccess");
        tableJsonBuilder.add("ConnectBy", "Server");
        tableJsonBuilder.add("Data", "Success");

        //send player list
        JsonArrayBuilder playerArray = Json.createArrayBuilder();

        //build for player
        JsonObjectBuilder playerJsonBuilder = Json.createObjectBuilder();
        playerJsonBuilder.add("ConnectType", "GameStartSuccess");
        playerJsonBuilder.add("ConnectBy", "Server");
        playerJsonBuilder.add("Data", "Success");

        JsonObject playerJson = playerJsonBuilder.build();

        try {
            JsonObjectBuilder innerBuilder = Json.createObjectBuilder();
            int i = 0;
            for (Player p : playerList) {
                innerBuilder.add("Name", p.getName());
                innerBuilder.add("playerIndex", i++);
                if (p.equals(playerList.get(0))) {
                    innerBuilder.add("turn", "true");
                } else {
                    innerBuilder.add("turn", "false");
                }
                playerArray.add(innerBuilder.build());
                p.getSession().getBasicRemote().sendText(playerJson.toString());
            }
            tableJsonBuilder.add("playerList", playerArray);
            gd.getTable().getSessionid().getBasicRemote().sendText(tableJsonBuilder.build().toString());

        } catch (Exception ex) {
            System.out.println("Exception in gameStart function");
        }
        return "Success";
    }

    public String UpdateCardListPlayer(String gameid) {
        System.out.println("in UpdateCardListPlayers function from Game.java");
        try {
            List<Player> playerList = gameDeskList.get(gameid).getPlayers();
            int i = 0;
            for (Player p : playerList) {
                JsonObjectBuilder jbuilder = Json.createObjectBuilder();
                jbuilder.add("ConnectType", "CardsList");
                jbuilder.add("ConnectBy", "Server");
                jbuilder.add("CardInHand", cardsToJson(p.getCardInHand()));
                if (i == gameDeskList.get(gameid).getTurn()) {
                    jbuilder.add("Turn", "yourTurn");
                } else {
                    jbuilder.add("Turn", "notYourTurn");
                }
                i++;

                p.getSession().getBasicRemote().sendText(jbuilder.build().toString());
            }
        } catch (Exception ex) {
            System.out.println("Exception in UpdateCardListPlayers");
        }
        return "Success";
    }

    public String startGame(String gameid) {
        System.out.println("in startGame function from Game.java");
        try {
            //forTable
            Table table = gameDeskList.get(gameid).getTable();
            gameDeskList.get(gameid).setGameStatus("StartGame");
            //forPlayer
            List<Player> playerList = gameDeskList.get(gameid).getPlayers();
            for (int i = 0; i < 7; i++) {
                for (Player p : playerList) {
                    p.drawCard(table.drawCard());
                    if (table.getDrawPile().size() == 0) {
                        gamefinish(gameid);
                    }
                }
            }
            table.getDiscardedPile().add(table.drawCard());
            if (table.getDrawPile().size() == 0) {
                gamefinish(gameid);
            }
            for (Player p : playerList) {
                JsonObjectBuilder jbuilder = Json.createObjectBuilder();
                jbuilder.add("ConnectType", "CardsList");
                jbuilder.add("ConnectBy", "Server");
                jbuilder.add("CardInHand", cardsToJson(p.getCardInHand()));
                jbuilder.add("Turn", "notYourTurn");
                p.getSession().getBasicRemote().sendText(jbuilder.build().toString());

            }
            //for first player
            JsonObjectBuilder jbuilder1 = Json.createObjectBuilder();
            jbuilder1.add("ConnectType", "nextPlayerTurn");
            jbuilder1.add("ConnectBy", "Server");
            jbuilder1.add("Turn", "yourTurn");
            playerList.get(0).getSession().getBasicRemote().sendText(jbuilder1.build().toString());
            updateCardListsTable(gameid);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Exception in startGame");
        }
        sendGameDeskList();
        return "Started";
    }

    public String updateCardListsTable(String gameid) {
        System.out.println("in updateCardListsTable function from Game.java");
        Table table = gameDeskList.get(gameid).getTable();

        JsonObjectBuilder jbuilder = Json.createObjectBuilder();
        jbuilder.add("ConnectType", "CardsList");
        jbuilder.add("ConnectBy", "Server");
        jbuilder.add("DrawPileList", cardsToJson(table.getDrawPile()));
        jbuilder.add("DiscardedList", cardsToJson(table.getDiscardedPile()));
        //============
        JsonArrayBuilder playerArray = Json.createArrayBuilder();
        JsonObjectBuilder innerBuilder = Json.createObjectBuilder();
        int i = 0;
        int turn = gameDeskList.get(gameid).getTurn();

        for (Player p : gameDeskList.get(gameid).getPlayers()) {
            innerBuilder.add("Name", p.getName());
            innerBuilder.add("playerIndex", i++);
            if (p.equals(gameDeskList.get(gameid).getPlayers().get(turn))) {
                innerBuilder.add("turn", "true");
            } else {
                innerBuilder.add("turn", "false");
            }
            playerArray.add(innerBuilder.build());
        }
        jbuilder.add("playerList", playerArray.build());
        //=========        
        try {
            System.out.println("Builder JSON");
            table.getSessionid().getBasicRemote().sendText(jbuilder.build().toString());
        } catch (Exception ex) {
            System.out.println("Exception in updateCardListsTable");
        }

        return "Success";
    }

    public JsonArray cardsToJson(List<Card> clist) {
        System.out.println("in cardsToJson function from Game.java");
        JsonObjectBuilder jbuilder = Json.createObjectBuilder();
        JsonArrayBuilder jArrayBuilder = Json.createArrayBuilder();
        NumberCard numcrd = null;
        ActionCard actcrd = null;
        WildCard widcrd = null;

        for (Card c : clist) {
            jbuilder.add("point", c.getPoint());
            jbuilder.add("backimg", c.getBackimg());
            jbuilder.add("frontimg", c.getFrontimg());
            if (c.getClass().getName().equals("sa41.team11.model.NumberCard")) {
                numcrd = (NumberCard) c;
                jbuilder.add("color", numcrd.getColor());
                jbuilder.add("value", numcrd.getValue());
            } else if (c.getClass().getName().equals("sa41.team11.model.ActionCard")) {
                actcrd = (ActionCard) c;
                jbuilder.add("color", actcrd.getColor());
                jbuilder.add("value", actcrd.getValue());
            } else if (c.getClass().getName().equals("sa41.team11.model.WildCard")) {
                widcrd = (WildCard) c;
                jbuilder.add("color", "NA");
                jbuilder.add("value", widcrd.getValue());
            }
            jArrayBuilder.add(jbuilder.build());
        }
        return jArrayBuilder.build();
    }

    public String nextPlayerTurn(String gameid) {
        System.out.println("in nextPlayerTurn function from Game.java");
        GameDesk gd = gameDeskList.get(gameid);
        Player p = gd.nextPlayer("NA");

        JsonObjectBuilder jbuilder = Json.createObjectBuilder();
        jbuilder.add("ConnectType", "nextPlayerTurn");
        jbuilder.add("ConnectBy", "Server");
        jbuilder.add("Turn", "yourTurn");
        try {
            p.getSession().getBasicRemote().sendText(jbuilder.build().toString());
        } catch (Exception ex) {
            System.out.println("Exception in nextPlayerTurn");
        }
        return "Success";
    }

    public String dropCard(String color, String val, String gameid, Session s, String choosencolor) {
        System.out.println("in dropCard function from Game.java");
        System.out.println("Color" + color);
        System.out.println("val" + val);
        System.out.println("choosencolor" + this.choosencolor);
        System.out.println("choosencolor" + choosencolor);

        try {
            GameDesk gd = gameDeskList.get(gameid);
            Player curPlayer = gd.getCurrentPlayer();
            String msg = gd.playerDropCard(color, val, s, choosencolor);
            if (msg.equals("EmptyDrawPile")) {
                gamefinish(gameid);
                return "";
            }
            if (msg.equals("Success") || msg.equals("Draw4Card")) {
                System.err.println("Current Player card list size - " + curPlayer.getCardInHand().size());
                if (curPlayer.getCardInHand().isEmpty()) {
                    gameFinish();
                }
                System.out.println("Drop Card is success");
                Player p = null;
                if (msg.equals("Draw4Card")) {
                    p = gd.nextPlayer("drawCard");
                } else {
                    p = gd.nextPlayer("NA");
                }
                for (Player p1 : gd.getPlayers()) {
                    JsonObjectBuilder jbuilder = Json.createObjectBuilder();
                    jbuilder.add("ConnectType", "CardsList");
                    jbuilder.add("ConnectBy", "Server");
                    if (p == p1) {
                        jbuilder.add("Turn", "yourTurn");
                    } else {
                        jbuilder.add("Turn", "notYourTurn");
                    }
                    jbuilder.add("CardInHand", cardsToJson(p1.getCardInHand()));

                    p1.getSession().getBasicRemote().sendText(jbuilder.build().toString());
                }
                updateCardListsTable(gameid);
                JsonObjectBuilder retDropMsgJson = Json.createObjectBuilder();
                retDropMsgJson.add("ConnectType", "CardDropMsgReturn");
                retDropMsgJson.add("ConnectBy", "Server");
                retDropMsgJson.add("Data", "Success");
                curPlayer.getSession().getBasicRemote().sendText(retDropMsgJson.build().toString());

            } else {
                System.out.println("Message in dropCard is" + msg);
            }
        } catch (Exception ex) {
            System.out.println("Exception in dropCard");
        }
        return "Success";
    }

    public String gameFinish() {
        System.out.println("Game is finish");
        return "";
    }
}
