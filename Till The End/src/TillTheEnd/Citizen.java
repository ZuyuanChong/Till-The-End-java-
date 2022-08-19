package TillTheEnd;

public class Citizen{
    Tower tower = new Tower();
    Wall wall = new Wall();
    
    private int emotional = 10;
    private int nervous = 10;
    private int lazy = 10;
    private int berserk = 10;
    private int diligent = 10;
    private int fearless = 10;
    
    private int game_gold;
    private int input_citizen;
    
    // accessor
    public int getEmotional() {
        return emotional;
    }

    public int getNervous() {
        return nervous;
    }

    public int getLazy() {
        return lazy;
    }

    public int getBerserk() {
        return berserk;
    }

    public int getDiligent() {
        return diligent;
    }

    public int getFearless() {
        return fearless;
    }

    public int getGame_gold() {
        return game_gold;
    }
    
    public int getInput_citizen(){
        return input_citizen;
    }
    
    // mutator
    public void setEmotional(int emotional) {
        this.emotional = emotional;
    }

    public void setNervous(int nervous) {
        this.nervous = nervous;
    }
    
    public void setLazy(int lazy) {
        this.lazy = lazy;
    }
    
    public void setBerserk(int berserk) {
        this.berserk = berserk;
    }

    public void setDiligent(int diligent) {
        this.diligent = diligent;
    }

    public void setFearless(int fearless) {
        this.fearless = fearless;
    }

    public void setGame_gold(int game_gold) {
        this.game_gold = game_gold;
    }
    
    
    
    public void level_up(int gold, int ans2){
        if (ans2 == 1 ){
            if(emotional>49){
                emotional -= 50;
                gold -=50;
                System.out.println("Citiizen's Emotional: "+ emotional);
                System.out.println("Remainding Gold: "+ gold);
                System.out.println();
            }else{
                System.out.println("Not enough emotional to decrease");
                System.out.println();
            }
        }else if (ans2 == 2 ){
            if(nervous>49){
                nervous-=50;
                gold -=50;
                System.out.println("Citizen's Nervous: " + nervous);
                System.out.println("Remainding Gold: " + gold);
                System.out.println();
            }else{
                System.out.println("Not enough nervous to decrease");
                System.out.println();
            }
        }else if (ans2 ==3){
            if(lazy>49){
                lazy -= 50;
                gold-=50;
                System.out.println("Citizen's Lazy: "+ lazy);
                System.out.println("Remainding Gold: "+ gold);
                System.out.println();
            }else{
                System.out.println("Not enough lazy to decrease");
                System.out.println();
            }
        }else if (ans2 == 4 ){
            berserk+=50;
            gold-=50;
            System.out.println("Citizen's Berserk: " + berserk);
            System.out.println("Remainding Gold: "+ gold);
            System.out.println();
        }else if (ans2 ==5 ){
            diligent +=50;
            gold-=50;
            System.out.println("Citizen's Diligent: "+ diligent);
            System.out.println("Reaminding Gold: "+ gold);
            System.out.println();
        }else if (ans2 == 6){
            fearless +=50;
            gold -=50;
            System.out.println("Citizen's Fearless: "+ fearless);
            System.out.println("Remainding Gold "+ gold);
            System.out.println();
        }
        
        
        /* conditions for situation whether stats exceeds 100 */
        if(emotional>=100){
            tower.setAttackpoint(tower.getAttackpoint()-1);
            emotional-=100;
            System.out.println("Citizen's emotional: "+ emotional);
            System.out.println("Tower's Attack Point:"+ tower.getAttackpoint());
            System.out.println();
        }
        if(nervous>=100){
            tower.setAccuracy(tower.getAccuracy()-5);
            nervous-=100;
            System.out.println("Tower's Accuracy: "+ tower.getAccuracy() + " %");
            System.out.println("Citizen's Nervous: "+ nervous +" %");
            System.out.println();
        }if(lazy >=100){
            wall.setHealthpoint(wall.getHealthpoint()-100);
            lazy -=100;
            System.out.println("Wall's Health Point: "+ wall.getHealthpoint() );
            System.out.println("Citizen's Lazy: " + lazy);
            System.out.println();
        }if (berserk >=100){
            tower.setAttackpoint(tower.getAttackpoint()+1);
            berserk -=100;
            System.out.println("Tower's Attack Point: "+ tower.getAttackpoint());
            System.out.println("Citizen's Berserk: "+ berserk);
            System.out.println();
        }if(diligent >=100){
            wall.setHealthpoint(wall.getHealthpoint()+75);
            diligent -=100;
            System.out.println("Wall's Health Point: "+ wall.getHealthpoint());
            System.out.println("Citizen's Diligent: "+ diligent);
            System.out.println();
        }
        if(fearless>=100){
            tower.setCriticalChance(tower.getCriticalChance()+5);
            fearless -=100;
            System.out.println("Tower's Critical Chance: "+ tower.getCriticalChance() +" %");
            System.out.println("Citizen's fearless: "+ fearless);
            System.out.println();
        } 
        
        game_gold = gold;
    }
    
    public String toString(Season season, Tax tax){
        return ("Year : " + season.getYear()+
                "\nSeason : " + season.getSeason()+
                "\nGold : " + tax.getCurrent_gold()+
                "\nCitizen's Emotional (Decrease Tower's AttackPoint by 1): "+ emotional+
                "\nCitizen's Nervous (Decrease Tower Accuracy Percentage by 5%): "+ nervous+
                "\nCitizen's Lazy (Decrease Wall's HealthPoint by 100): "+ lazy+
                "\nCitizen's Berserk (Increase Tower's AttackPoint by 1): "+ berserk+
                "\nCitizen's Diligent (Increase Wall's HealthPoint by 75): "+ diligent+
                "\nCitizen's Fearless (Increase Tower Critical Chance Percentage by 5%): "+ fearless+
                "\n1. Decrease Emotional (50 Gold -> 50 Emotional Point)"+
                "\n2. Decrease Nervous (50 Gold -> 50 Nervous Point)"+
                "\n3. Decrease Lazy ( 50 Gold -> 50 Lazy Point)"+
                "\n4. Increase Berserk (50 Gold -> 50 Berserk Point)"+
                "\n5. Increase Diligent (50 Gold -> 50 Diligent Point)"+
                "\n6. Increase Fearless (50 Gold -> 50 Fearless Point)"+
                "\n7. Back to menu"
                );
    }
    
    public void input_citizen(){
        System.out.print("Please enter your command: ");
        input_citizen = new java.util.Scanner(System.in).nextInt();
        while(input_citizen>7){
            System.out.println("You enter an invalid command.");
            System.out.print("Please enter you command:");
            input_citizen = new java.util.Scanner(System.in).nextInt();
        }
    }
    
    public void citizen_choice(Tax tax){
        System.out.println();
        if(tax.getCurrent_gold()<50){
            System.out.println("Not enough gold");
        } 
        else{
            level_up(tax.getCurrent_gold(), input_citizen);                     //level up for Citizen
            tax.setCurrent_gold(game_gold);                                          // updtae current_gold
        }
    }
    
    
}

