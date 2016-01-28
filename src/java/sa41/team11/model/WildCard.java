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
public class WildCard extends Card{
    private String value;

    public WildCard(String value, int point, String backimg, String frontimg) {
        super(point, backimg, frontimg);
        this.value = value;
    }    

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    
}
