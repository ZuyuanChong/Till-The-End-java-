package TillTheEnd;

public class Wall{
    private int healthpoint = 100;
    private int blockPercentage = 10;
    
    private int game_gold;
    private int input_wall;
    
    // accessor
    public int getHealthpoint() {
        return healthpoint;
    }

    public int getBlockPercentage() {
        return blockPercentage;
    }

    public int getGame_gold() {
        return game_gold;
    }
    
    public int getInput_wall(){
        return input_wall;
    }
    
    // mutator
    public void setHealthpoint(int healthpoint) {
        this.healthpoint = healthpoint;
    }

    public void setBlockPercentage(int blockPercentage) {
        this.blockPercentage = blockPercentage;
    }

    public void setGame_gold(int game_gold) {
        this.game_gold = game_gold;
    }
    
    public void level_up(int gold, int ans1){
        if(ans1 == 1){
            healthpoint +=75;
            gold -=100;
            System.out.println("New health point : " + healthpoint);
            System.out.println("Remainding gold : " + gold);
            System.out.println();
        }else if(ans1 == 2){
            if(blockPercentage<49){
                 blockPercentage += 5;
                 gold -= 100;
                 System.out.println("New Block Percentage: " + blockPercentage +"%");
                 System.out.println("Remainding Gold: "+ gold);
                 System.out.println();
            }else{
                 System.out.println("The Block Percentage at CAPPED at 50%");
                 System.out.println("Please enter other number.");
                 System.out.println();
            }
        }
        
        game_gold = gold;
    }
    
    public String toString(Season season, Tax tax){
        return ("Year : " + season.getYear()+
                "\nSeason : " + season.getSeason()+
                "\nGold : " + tax.getCurrent_gold()+
                "\nWall's Healthpoint: " + healthpoint+
                "\nWall's Block: " + blockPercentage + "%"+
                "\n1. Upgrade Health (100 Gold -> 75 HealthPoint)"+
                "\n2. Upgrade Block Chance (100 Gold -> 5 Block Chance %)"+
                "\n3. Back to menu"
                );
    }
    
    public void input_wall(){
        System.out.print("Please enter your command: ");
        input_wall = new java.util.Scanner(System.in).nextInt();
        while(input_wall>3){
            System.out.println("You enter an invalid command.");
            System.out.print("Please enter you command:");
            input_wall = new java.util.Scanner(System.in).nextInt();
        }
    }
    
    public void wall_choice(Tax tax){
        System.out.println();
        if(tax.getCurrent_gold()<100){
            System.out.println("Not enough gold");
        } 
        else{
            level_up(tax.getCurrent_gold(), input_wall);                      //level up for Wall
            tax.setCurrent_gold(game_gold);                                     //update current_gold
        }
    }
}
