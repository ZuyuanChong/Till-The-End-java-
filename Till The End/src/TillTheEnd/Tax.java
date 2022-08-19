package TillTheEnd;

public class Tax{
    private int gold;
    private int current_gold;

    // accessor
    public int getGold() {
        return gold;
    }
    
    public int getCurrent_gold(){
        return current_gold;
    }
    
    // mutator
    public void setGold(int gold) {
        this.gold = gold;
    }
    
    public void setCurrent_gold(int current_gold){
        this.current_gold = current_gold;
    }
    
    // feature
    public void generateTax(){
        switch(new java.util.Random().nextInt(5)){
            case 0:
                gold = 200;
                break;
            case 1:
                gold = 250;
                break;
            case 2:
                gold = 300;
                break;
            case 3:
                gold = 350;
                break;
            case 4:
                gold = 400;
                break;
        }
        
        this.gold = gold;
    }
    
    public void generateTax_hard(){
        generateTax();
        gold /= 2;
    }
    
    public void receiveTax(){
        current_gold += gold;
    }
}
