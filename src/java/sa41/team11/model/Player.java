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
public class Player {

    private Session session;
    private String name;
    private List<Card> cardInHand;
    private int Score;

    public Card dropCardPlayer(String color, String value) {
        for (Card c : cardInHand) {
            if (c.getClass().getName().equals("sa41.team11.model.NumberCard")) {
                NumberCard nc = (NumberCard) c;
                if (nc.getColor().equals(color) && nc.getValue().equals(value)) {
                    cardInHand.remove(nc);
                    return nc;
                }
            } else if (c.getClass().getName().equals("sa41.team11.model.ActionCard")) {
                ActionCard ac = (ActionCard) c;
                if (ac.getColor().equals(color) && ac.getValue().equals(value)) {
                    cardInHand.remove(ac);
                    return ac;
                }
            } else if (c.getClass().getName().equals("sa41.team11.model.WildCard")) {
                WildCard wc = (WildCard) c;
                if (wc.getValue().equals(value)) {
                    cardInHand.remove(wc);
                    return wc;
                }
            }
        }
        return null;
    }

    public String drawCard(Card c) {
        cardInHand.add(c);
        return "Success";
    }

    public Player(Session session, String name, List<Card> cardInHand) {
        this.session = session;
        this.name = name;
        this.cardInHand = cardInHand;
        this.Score = 0;
    }

    public Player(Session session, String name) {
        this.session = session;
        this.name = name;
        this.cardInHand = new LinkedList<Card>();
        this.Score = 0;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int Score) {
        this.Score = Score;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Card> getCardInHand() {
        return cardInHand;
    }

    public void setCardInHand(List<Card> cardInHand) {
        this.cardInHand = cardInHand;
    }

}
