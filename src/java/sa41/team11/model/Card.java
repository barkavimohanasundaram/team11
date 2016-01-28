/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sa41.team11.model;

/**
 *
 * @author HeinHtetZaw
 */
public class Card {
    private int point;
    private String backimg;
    private String frontimg;

    public Card(int point, String backimg, String frontimg) {
        this.point = point;
        this.backimg = backimg;
        this.frontimg = frontimg;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getBackimg() {
        return backimg;
    }

    public void setBackimg(String backimg) {
        this.backimg = backimg;
    }

    public String getFrontimg() {
        return frontimg;
    }

    public void setFrontimg(String frontimg) {
        this.frontimg = frontimg;
    }
    
}
