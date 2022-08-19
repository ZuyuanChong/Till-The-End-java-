package TillTheEnd;

public class Tower extends SameBehaviour{
    private int attackpoint = 5;
    private int criticalChance = 20;
    private int accuracy = 80;
    
    private int tower_damage;
    private int game_gold;
    private int input_tower;
    
    // accessor
    public int getAttackpoint() {
        return attackpoint;
    }

    public int getCriticalChance() {
        return criticalChance;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public int getTower_damage() {
        return tower_damage;
    }

    public int getGame_gold() {
        return game_gold;
    }
    
    public int getInput_tower(){
        return input_tower;
    }
    
    // mutator
    public void setAttackpoint(int attackpoint) {
        this.attackpoint = attackpoint;
    }

    public void setCriticalChance(int criticalChance) {
        this.criticalChance = criticalChance;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public void setTower_damage(int tower_damage) {
        this.tower_damage = tower_damage;
    }

    public void setGame_gold(int game_gold) {
        this.game_gold = game_gold;
    }
    
    // feature
    @Override
    public void level_up(Tax tax, int input){
        if(input == 1){
            attackpoint += 1;
            tax.setCurrent_gold(tax.getCurrent_gold()-100);
            System.out.println("New Attack Point: "+ attackpoint);
            System.out.println("Remainding gold: " + tax.getCurrent_gold() );
            System.out.println();
        }else if(input == 2){
            if(criticalChance <49){
                criticalChance +=5;
                tax.setCurrent_gold(tax.getCurrent_gold()-100);
                System.out.println("New Critical Chance: " + criticalChance + "%");
                System.out.println("Remainding gold: " + tax.getCurrent_gold() );
                System.out.println();
            }else{
                System.out.println("The Critical Chance Percentage is CAPPED at 50%");
                System.out.println("Please enter other number");
                System.out.println();
            }
        }else if(input == 3){
            if(accuracy<99){
                accuracy += 4;
                tax.setCurrent_gold(tax.getCurrent_gold()-100);
                System.out.println("New Accuracy: " + accuracy + "%");
                System.out.println("Remainding gold: " + tax.getCurrent_gold() );
                System.out.println();
            }else {    
                System.out.println("The accuracy percentage is CAPPED at 100%");
                System.out.println("Please choose other number.");
                System.out.println();
            }
        }
    }        
    
    @Override
    public void attack_or_miss(int tower_acc, int tower_critical_total, int tower_critical_chance){
        tower_damage = attackpoint;
        if(tower_acc!=0 && tower_acc!=9){
            //normal attack
            if(tower_critical_total<=tower_critical_chance){
                tower_damage*=1.5;
                System.out.println("Tower attacked dragon with critical attack!");
            }
            else{
                System.out.println("Tower attacked dragon!");
                tower_damage=attackpoint;
            }
        }
        else{
            System.out.println("Tower missed attack!");
            tower_damage=0;
        }
    }
    
    public String toString(Season season, Tax tax){
        return ("Year : "+season.getYear()+
                "\nSeason : "+season.getSeason()+
                "\nGold : "+tax.getCurrent_gold()+
                "\nTower's AttackPoint: "+attackpoint+
                "\nTower's Critical Chance : "+criticalChance+"%"+
                "\nTower's Accuracy: "+accuracy+"%"+
                "\n1. Upgrade Attack (100 Gold -> 1 AttackPoint)"+
                "\n2. Upgrade Critical Chance (100 Gold -> 5 Ctrical chance %)"+
                "\n3. Upgrade Accuracy (100 Gold -> 4% Accuracy)"+
                "\n4. Back to menu"
                );
    }
    
    public void input_tower(){
        System.out.print("Please enter your command: ");
        input_tower = new java.util.Scanner(System.in).nextInt();
        while(input_tower>4){
            System.out.println("You enter an invalid command.");
            System.out.print("Please enter you command:");
            input_tower = new java.util.Scanner(System.in).nextInt();
        }
    }
    
    public void tower_choice(Tax tax){
        System.out.println();
        if(tax.getCurrent_gold()<100){
            System.out.println("Not enough gold");
        } 
        else{
            level_up(tax, input_tower);                //level up for Tower
        }
    }
}
