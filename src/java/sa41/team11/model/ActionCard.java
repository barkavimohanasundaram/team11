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
public class ActionCard extends ColorCard{
    private String value;

    public ActionCard(String value, String color, int point, String backimg, String frontimg) {
        super(color, point, backimg, frontimg);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    
}
