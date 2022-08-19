package TillTheEnd;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class Dragon extends SameBehaviour{
    private int level = 1;
    private int healthpoint = 100;
    private int attackpoint = 7;
    private int criticalChance = 20;
    private int accuracy = 80;
    private int dg_damage;
    
    private int full_hp = healthpoint;
    private boolean game_condition = true;
    
    
    // accessor
    public int getLevel() {
        return level;
    }

    public int getHealthpoint() {
        return healthpoint;
    }

    public int getAttackpoint() {
        return attackpoint;
    }

    public int getCriticalChance() {
        return criticalChance;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public int getDg_damage() {
        return dg_damage;
    }

    public int getFull_hp() {
        return full_hp;
    }
    
    public boolean getGame_condition(){
        return game_condition;
    }
    
    // mutator
    public void setLevel(int level) {
        this.level = level;
    }

    public void setHealthpoint(int healthpoint) {
        this.healthpoint = healthpoint;
    }

    public void setAttackpoint(int attackpoint) {
        this.attackpoint = attackpoint;
    }

    public void setCriticalChance(int criticalChance) {
        this.criticalChance = criticalChance;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public void setDg_damage(int dg_damage) {
        this.dg_damage = dg_damage;
    }

    public void setFull_hp(int full_hp) {
        this.full_hp = full_hp;
    }
    
    public void setGame_condition(boolean game_condition){
        this.game_condition = game_condition;
    }
    
    @Override
    public void level_up(Tax tax, int input){
        healthpoint = full_hp;      //recovery
        level++;
        healthpoint+=15;
        attackpoint+=1;
        criticalChance+=2;
    }
    
    public void level_up_hard(){
        // for hard mode, the increase in stats for every level up is twice of that for normal mode
        healthpoint = full_hp;      //recovery
        level++;
        healthpoint+=30;
        attackpoint+=2;
        criticalChance+=4;
    }
    
    @Override
    public void attack_or_miss(int dg_acc, int dg_critical_total, int dg_critical_chance){
        dg_damage = attackpoint;
        if(dg_acc!=0 && dg_acc!=9){
            //normal attack
            if(dg_critical_total<=dg_critical_chance){
                dg_damage*=1.5;
                
                System.out.println("   (  )   /\\   _                 (     ");
                System.out.println("    \\ |  (  \\ ( \\.(               )                      ___");
                System.out.println(" \\  \\ \\  `  `   ) \\             (  _                 / _   \\");
                System.out.println("(`    \\+   . x  ( .\\            \\/   \\_-----------/ (o)   \\_");
                System.out.println("- .-               \\+  ;          (  O                           \\__");
                System.out.println("                          )        \\_____  `              \\  /");
                System.out.println("(__                +- .( -'.- <. - _  VVVVVVV VV V\\                 \\/");
                System.out.println("(__            ..: < - <- _  (--  AAAAAAAA/                  |");
                System.out.println("  .    /./.+-  . .- /  +--  - .     \\_____//              \\___");
                System.out.println("  .    /./.+-  . .- /  +--  - .     \\_____//              \\___");
                System.out.println("  (_ ' /x  / x _/ (                                  \\__'          \\     /");
                System.out.println(" , x / ( '  . / .  /                                      |           \\   /");
                System.out.println("    /  /  _/ /    +                                      /              \\/");
                System.out.println("   '  (__/                                             /                  \\");
                System.out.println("Dragon attacked our wall with critical attack!");
                try{
                    FileInputStream fileInputStream = new FileInputStream("Dragon.mp3"); 
                    Player player = new Player(fileInputStream);
                    player.play();
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }catch(JavaLayerException e){
                    e.printStackTrace();
                }
                
            }
            else{
                System.out.println("                   ,  ,                                                    ");
                System.out.println("                   \\\\ \\\\                                               ");
                System.out.println("                   ) \\\\ \\\\    p                                          ");
                System.out.println("                   )^\\))\\))  /  *\\                                         ");
                System.out.println("                    \\_|| || / /^`-'                                        ");
                System.out.println("           __       -\\ \\\\--/ /                                             ");
                System.out.println("         <'  \\\\__/   __. )'                                              ");
                System.out.println("              `====\\ )_/\\\\                                               ");
                System.out.println("                   //     `\"                                               ");
                System.out.println("                   \\\\    /  \\                                              ");
                System.out.println("                   `\" +========+                                           ");
                System.out.println("                       |Dragon  |                                           ");
                System.out.println("                       |attacked|                                           ");
                System.out.println("                       |the wall|                                           ");
                System.out.println("                       \"\"\"\"\"\"\"\"\"\"");
                System.out.println("Dragon attacked our wall!");
                
                dg_damage=attackpoint;
            }
        }
        else{
            System.out.println("                             -|             |-");
            System.out.println("         -|                  [-_-_-_-_-_-_-_-]                  |-");
            System.out.println("         [-_-_-_-_-]          |             |          [-_-_-_-_-]");
            System.out.println("          | o   o |           [  0   0   0  ]           | o   o |");
            System.out.println("           |     |    -|       |           |       |-    |     |");
            System.out.println("           |     |_-___-___-___-|         |-___-___-___-_|     |");
            System.out.println("           |  o  ]              [    0    ]              [  o  |");
            System.out.println("           |     ]   o   o   o  [ _______ ]  o   o   o   [     | ----__________");
            System.out.println("_____----- |     ]              [ ||||||| ]              [     |");
            System.out.println("           |     ]              [ ||||||| ]              [     |");
            System.out.println("       _-_-|_____]--------------[_|||||||_]--------------[_____|-_-_");
            System.out.println("      ( (__________------------_____________-------------_________) )");
            System.out.println("Dragon missed attack!");
            try{
                FileInputStream fileInputStream = new FileInputStream("miss_attack.mp3"); 
                Player player = new Player(fileInputStream);
                player.play();
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch(JavaLayerException e){ 
                e.printStackTrace();
            }
            
            dg_damage=0;
        }
    }
    
    public String toString(Season season, Tax tax){
        return ("A dragon performs a sudden attack to your city!"+
                "\nDragon's level: "+level+
                "\nDragon's health points: "+healthpoint+
                "\nDragn's attack points: "+attackpoint+
                "\nDragon's cirtical chance: "+criticalChance+"%"+
                "\nDragon's accuracy percentage: 80%\n"
                );
    }
    
    
    public void dragon_round(Tower tower, Wall wall){
        for(int i=0; i<10; i++){
            //dragon's round
            int wall_block_total = new java.util.Random().nextInt(100);
                
            if(wall_block_total<=wall.getBlockPercentage()){
                System.out.println("                             -|             |-");
                System.out.println("         -|                  [-_-_-_-_-_-_-_-]                  |-");
                System.out.println("         [-_-_-_-_-]          |             |          [-_-_-_-_-]");
                System.out.println("          | o   o |           [  0   0   0  ]           | o   o |");
                System.out.println("           |     |    -|       |           |       |-    |     |");
                System.out.println("           |     |_-___-___-___-|         |-___-___-___-_|     |");
                System.out.println("           |  o  ]              [    0    ]              [  o  |");
                System.out.println("           |     ]   o   o   o  [ _______ ]  o   o   o   [     | ----__________");
                System.out.println("_____----- |     ]              [ ||||||| ]              [     |");
                System.out.println("           |     ]              [ ||||||| ]              [     |");
                System.out.println("       _-_-|_____]--------------[_|||||||_]--------------[_____|-_-_");
                System.out.println("      ( (__________------------_____________-------------_________) )");
                System.out.println("Wall succesfully blocked dragon's attack");
                try{
                    FileInputStream fileInputStream = new FileInputStream("block.mp3"); 
                    Player player = new Player(fileInputStream);
                    player.play();
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }catch(JavaLayerException e){
                    e.printStackTrace();
                }
            }
            else{
                int dg_acc = new java.util.Random().nextInt(10);
                int dg_critical_total = new java.util.Random().nextInt(100);
                int dg_critical_chance = new java.util.Random().nextInt(criticalChance);
                attack_or_miss(dg_acc, dg_critical_total, dg_critical_chance);    //dragon attacks or misses
                System.out.println("Wall's HealthPoint minus "+dg_damage);
                wall.setHealthpoint(wall.getHealthpoint()-dg_damage);
            }
            System.out.println("Current Wall's HealthPoint: "+wall.getHealthpoint());
                
            new java.util.Scanner(System.in).nextLine();
                        
            //dragon wins
            if(wall.getHealthpoint()<=0){
                System.out.println("You failed to protect the city!\n");
                game_condition = false;
                break;
            }
                        
            //tower's round
            int tower_acc = new java.util.Random().nextInt(10);
            int tower_critical_total = new java.util.Random().nextInt(100);
            int tower_critical_chance = new java.util.Random().nextInt(criticalChance);
            tower.attack_or_miss(tower_acc, tower_critical_total, tower_critical_chance);
            System.out.println("Dragon's HealthPoint minus "+tower.getTower_damage());
            healthpoint -= tower.getTower_damage();
            System.out.println("Current Dragon's HealthPoint: "+healthpoint);
                
            new java.util.Scanner(System.in).nextLine();
                        
            //player wins
            if(healthpoint<=0){
                System.out.println("You killed the dragon! You protected the city!\n");
                
                System.out.println("                                 __                                         ");
                System.out.println("                              .-\"    `-.      ,                               ");
                System.out.println("                            .'          '.   /j\\                              ");
                System.out.println("                           /              \\,/:/#\\                /\\           ");
                System.out.println("                          ;              ,//' '/#\\              //#\\          ");
                System.out.println("                          |             /' :   '/#\\            /  /#\\         ");
                System.out.println("                          :         ,  /' /'    '/#\\_..--\"\"\"\"/    /#\\_      ");
                System.out.println("                           \\       /'\\'-.:_    '/#\\        ;      /#, \"\"\"---");
                System.out.println("                            `-.   / ;#\\']\" ; \"\"\"--./#J       ':__...!       ");
                System.out.println("                               `-/   /#\\  J  [;[;[;Y]         |      ;        ");
                System.out.println("\"\"\"\"\"\"---....             __.--\"/    '/#\\ ;   \" \"  |     !    |   #! |        ");
                System.out.println("             \"\"--.. .--\"\"     /      ,/#\\'-.._.;_,   |    |   '  |        ");
                System.out.println("                   \"-.        :...._,/#} \"####\" | '.-\",   | #['  |        ");
                System.out.println("                      '-._      |[;[;[;[;|         |.;'  /;\\  |      |        ");
                System.out.println("                      ,   `-.   |        :     _   .;'    /;\\ |   #\" |        ");
                System.out.println("                      !      `._:      _  ;   ##' .;'      /;\\|  _,  |        ");
                System.out.println("                     .#\\\"\"\"---..._    ';, |      .;{_     /;\\  ]#' |__....--");
                System.out.println("          .--.      ;'/#\\         \\    ]! |       \"| , \"\"\"--./_J    /         ");
                System.out.println("         /  '%;    /  '/#\\         \\   !' :        |!# #! #! #|    :`.__      ");
                System.out.println("        i_..'%] _:   ;##J         \\      :\"#....!   '  \"  \"|_  |    `--.._");
                System.out.println("         | .--\"\"\" !|\"\"\"\"  |\"\"\"----...J     | '##\"\" `-._       |  \"\"\"---.._    ");
                System.out.println("     _: |      #|      |         #|     |          \"]      ;   __...-\"T,  ");
                System.out.println("    /   :  :      !|      |   __!    |           |__..--;\"\"\"     ,;MM;  ");
                System.out.println("   :__| :    .-.#|      |  /\\      /#\\   |          /'               ''MM;  ");
                System.out.println("    |\"\"\": |   /   \\|   .----+  ;      /#\\  :_..--\"\";                  ,'MM; ");
                System.out.println("   Y--:  |  ;     ;.-'      ;  \\___/#: /         ;                  ''MM; ");
                System.out.println("  /    |  | ;__;     ___!  |\"##\"\"\"MM!         ;                    ,'MM;");
                System.out.println(" !__|  |  |\"#\"#\"|_.'\"\"##\"  |       :         ;                     ''MM  ");
                System.out.println("  | \"\"\"\"--!.|     |##\"\"         !       !         :_.....-------\"\"\"\"\"\" |'");
                System.out.println("  |          :     |__                        _!_ \"#\"\"#\"\"#\"\"\"#\"\"\"#\"\"\"|  ");
                System.out.println("_|          ;     |MM\"MM\"\"\"\"\"---...__...--\"\"MM\"MM]                   |   ");
                System.out.println("  \"\\-.      :      |#                                  :                   |  ");
                System.out.println("    /#'.    |      /##,                                !                   |  ");
                System.out.println("   .',/'\\   |       #:#,                                ;       .==.       |  ");
                System.out.println("  /\"\\'#\"\\',.|       ##;#,                               !     ,'||||',     |  ");
                System.out.println("        /;/`:       ######,          __             _ :     M||||||M     |  ");
                System.out.println("       ###          /;\"\\._\"-.   \"\"\"                   |===..M!!!!!!M___|  ");
                System.out.println("                           `--..`--..__             _!                    ");
                System.out.println("                                          `--..._,=\".'`-.__        fsc");
                try{
                    FileInputStream fileInputStream = new FileInputStream("wall.mp3"); 
                    Player player = new Player(fileInputStream);
                    player.play();
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }catch(JavaLayerException e){
                    e.printStackTrace();
                }
                
                game_condition = false;
                break;
            }
        }
    }
    
    public void dragon_round_end(Tax tax, int input_command){
        //statements to be printed after the dragon's attacks
        if(game_condition){System.out.println("                             ______________                               ");
            System.out.println("                       ,===:'.,            `-._                           ");
            System.out.println("                             `:.`---.__         `-._                       ");
            System.out.println("                               `:.     `--.         `.                     ");
            System.out.println("                                \\.        `.         `.                   ");
            System.out.println("                        (,,(,    \\.         `.   ____,-`.,                ");
            System.out.println("                     (,'     `/   \\.   ,--.___`.'                         ");
            System.out.println("                 ,  ,'  ,--.  `,   \\.;'         `                         ");
            System.out.println("                   `{D, {    \\  :    \\;                                    ");
            System.out.println("                     V,,'    /  /    //                                    ");
            System.out.println("                     j;;    /  ,' ,-//.    ,---.      ,                    ");
            System.out.println("                     \\;'   /  ,' /  _  \\  /  _  \\   ,'/                    ");
            System.out.println("                           \\   `'  / \\  `'  / \\  `.' /                     ");
            System.out.println("                            `.___,'   `.__,'   `.__,'    ");
            System.out.println("Dragon flew away!\n");
            try{
                FileInputStream fileInputStream = new FileInputStream("fly away.mp3"); 
                Player player = new Player(fileInputStream);
                player.play();
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch(JavaLayerException e){
                e.printStackTrace();
            }
            
            //dragon flies away
            level_up(tax, input_command);                      //level up for Dragon
            System.out.println("-----------------------------------------------------------------------");
        }
        else{
            // only will be printed after the game ends
            System.out.println("<THE END>");
            try{
                FileInputStream fileInputStream = new FileInputStream("GameOver.mp3"); 
                Player player = new Player(fileInputStream);
                player.play();
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch(JavaLayerException e){
                e.printStackTrace();
            }
        }
    }
    
    public void dragon_round_end_hard(){
        //statements to be printed after the dragon's attacks
        if(game_condition){
            System.out.println("                             ______________                               ");
            System.out.println("                       ,===:'.,            `-._                           ");
            System.out.println("                             `:.`---.__         `-._                       ");
            System.out.println("                               `:.     `--.         `.                     ");
            System.out.println("                                \\.        `.         `.                   ");
            System.out.println("                        (,,(,    \\.         `.   ____,-`.,                ");
            System.out.println("                     (,'     `/   \\.   ,--.___`.'                         ");
            System.out.println("                 ,  ,'  ,--.  `,   \\.;'         `                         ");
            System.out.println("                   `{D, {    \\  :    \\;                                    ");
            System.out.println("                     V,,'    /  /    //                                    ");
            System.out.println("                     j;;    /  ,' ,-//.    ,---.      ,                    ");
            System.out.println("                     \\;'   /  ,' /  _  \\  /  _  \\   ,'/                    ");
            System.out.println("                           \\   `'  / \\  `'  / \\  `.' /                     ");
            System.out.println("                            `.___,'   `.__,'   `.__,'    ");
            System.out.println("Dragon flew away!\n");
            try{
                FileInputStream fileInputStream = new FileInputStream("fly away.mp3"); 
                Player player = new Player(fileInputStream);
                player.play();
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch(JavaLayerException e){
                e.printStackTrace();
            }
            
            //dragon flies away
            level_up_hard();                       //level up for Dragon
            System.out.println("-----------------------------------------------------------------------");
        }
        else{
            // only will be printed after the game ends
            System.out.println("<THE END>");
            try{
                FileInputStream fileInputStream = new FileInputStream("GameOver.mp3"); 
                Player player = new Player(fileInputStream);
                player.play();
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch(JavaLayerException e){
                e.printStackTrace();
            }
        }
    }
}
