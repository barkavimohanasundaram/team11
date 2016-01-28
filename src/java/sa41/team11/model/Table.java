/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sa41.team11.model;

import java.util.ArrayList;
import java.util.Collections;
import javax.websocket.Session;

/**
 *
 * @author HeinHtetZaw
 */
public class Table {

    private Session sessionid;
    private ArrayList<Card> drawPile;
    private ArrayList<Card> discardedPile;

    public Table(Session sessionid) {
        this.sessionid = sessionid;
        drawPile = new ArrayList<Card>();
        discardedPile = new ArrayList<Card>();
        prepareCard();
        sheffleCard();
    }

    //draw cards
    public Card drawCard() {
        if (drawPile.size() == 0) {
            return null;
        }
        return drawPile.remove(0);
    }

    public String discardedCard(Card c) {
        discardedPile.add(c);
        return "Success";
    }

    //initialize cards
    public void prepareCard() {
        //add color card
        for (int j = 0; j < 2; j++) {
            for (int i = j; i < 10; i++) {
                drawPile.add(new NumberCard("" + i + "", "R", i, "back", "R" + i));
                drawPile.add(new NumberCard("" + i + "", "G", i, "back", "G" + i));
                drawPile.add(new NumberCard("" + i + "", "B", i, "back", "B" + i));
                drawPile.add(new NumberCard("" + i + "", "Y", i, "back", "Y" + i));
            }
            drawPile.add(new ActionCard("Rev", "R", 20, "back", "R" + "Rev"));
            drawPile.add(new ActionCard("Rev", "G", 20, "back", "G" + "Rev"));
            drawPile.add(new ActionCard("Rev", "B", 20, "back", "B" + "Rev"));
            drawPile.add(new ActionCard("Rev", "Y", 20, "back", "Y" + "Rev"));

            drawPile.add(new ActionCard("Plus2", "R", 20, "back", "R" + "Plus2"));
            drawPile.add(new ActionCard("Plus2", "G", 20, "back", "G" + "Plus2"));
            drawPile.add(new ActionCard("Plus2", "B", 20, "back", "B" + "Plus2"));
            drawPile.add(new ActionCard("Plus2", "Y", 20, "back", "Y" + "Plus2"));

            drawPile.add(new ActionCard("Skip", "R", 20, "back", "R" + "Skip"));
            drawPile.add(new ActionCard("Skip", "G", 20, "back", "G" + "Skip"));
            drawPile.add(new ActionCard("Skip", "B", 20, "back", "B" + "Skip"));
            drawPile.add(new ActionCard("Skip", "Y", 20, "back", "Y" + "Skip"));
        }

        //add wild cared
        for (int i = 0; i < 4; i++) {
            drawPile.add(new WildCard("W", 50, "back", "W"));
            drawPile.add(new WildCard("W4", 50, "back", "W4"));
        }
    }

    public Session getSessionid() {
        return sessionid;
    }

    public ArrayList<Card> getDrawPile() {
        return drawPile;
    }

    public ArrayList<Card> getDiscardedPile() {
        return discardedPile;
    }

    public void setSessionid(Session sessionid) {
        this.sessionid = sessionid;
    }

    public void setDrawPile(ArrayList<Card> drawPile) {
        this.drawPile = drawPile;
    }

    public void setDiscardedPile(ArrayList<Card> discardedPile) {
        this.discardedPile = discardedPile;
    }

    public void sheffleCard() {
        Collections.shuffle(drawPile);
    }
}
