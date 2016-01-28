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
public class ColorCard extends Card{
    private String color;

    public ColorCard(String color, int point, String backimg, String frontimg) {
        super(point, backimg, frontimg);
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    
}