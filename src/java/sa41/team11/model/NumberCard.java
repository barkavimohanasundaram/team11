package sa41.team11.model;

/**
 *
 * @author HeinHtetZaw
 */
public class NumberCard extends ColorCard{
    private String value;

    public NumberCard(String value, String color, int point, String backimg, String frontimg) {
        super(color, point, backimg, frontimg);
        this.value = value;
    }
    @Override
    public String getColor(){
       return super.getColor();
    }
    
    @Override
    public void setColor(String value) {
        super.setColor(value);
    }
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}