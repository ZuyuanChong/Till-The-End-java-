package TillTheEnd;

public class Season{
    Citizen citizen = new Citizen();
    Tower tower = citizen.tower;
    Wall wall = citizen.wall;
    
    
    private String season = null;
    private String event = null;
    private String event_hard_1 = null;
    private String event_hard_2 = null;
    private int gold=0;
    private int time=0;
    private int season_time;
    private int year;
    
    // accessor
    public String getEvent() {
        return event;
    }

    public int getGold() {
        return gold;
    }
    
    public String getSeason(){
        return season;
    }
    
    public int getTime(){
        return time;
    }
    
    public int getSeason_time(){
        return season_time;
    }
    
    public int getYear(){
        return year;
    }
    
    public String getEvent_hard_1(){
        return event_hard_1;
    }
    
    public String getEvent_hard_2(){
        return event_hard_2;
    }
    
    // mutator
    public void setEvent_hard_1(String event_hard_1){
        this.event_hard_1 = event_hard_1;
    }
    
    public void setEvent_hard_2(String event_hard_2){
        this.event_hard_2 = event_hard_2;
    }
    
    public void setTime(int time){
        this.time = time;
    }
    
    
    /* these are the events that can occur in Spring */
    public void spring_event(){
        int event_generator = new java.util.Random().nextInt(3);
        
        if(event_generator==0){
            event = "Reinforcement";
            tower.setAttackpoint(tower.getAttackpoint()+1);
        }
        else if(event_generator==1){
            event = "Visitors";
            gold += 100;
        }
        else if(event_generator==2){
            event = "Festival";
            citizen.setBerserk(citizen.getBerserk()+50);
            citizen.setDiligent(citizen.getDiligent()+50);
            citizen.setFearless(citizen.getFearless()+50);
        }
    }
    
    /* these are the events that can occur in Sumemr*/
    public void summer_event(){
        int event_generator = new java.util.Random().nextInt(3);
        
        if(event_generator==0){
            event = "Drought";
            wall.setHealthpoint(wall.getHealthpoint()-50);
        }
        else if(event_generator==1){
            event = "Outing";
            citizen.setBerserk(citizen.getBerserk()+50);
            citizen.setDiligent(citizen.getDiligent()+50);
            citizen.setFearless(citizen.getFearless()+50);
        }
        else if(event_generator==2){
            event = "Heatstroke";
            citizen.setEmotional(citizen.getEmotional()+50);
            citizen.setNervous(citizen.getNervous()+50);
            citizen.setLazy(citizen.getLazy()+50);
        }
    }
    
    public void autumn_event(){
        int event_generator = new java.util.Random().nextInt(3);
                
        if(event_generator==0){
            event = "Rainy";
            tower.setAccuracy(tower.getAccuracy()-20);
        }
        else if(event_generator==1){
            event = "Flood";
            wall.setHealthpoint(wall.getHealthpoint()-50);
        }
        else if(event_generator==2){
            event = "Harvest";
            gold += 100;
        }
    }
    
    public void winter_event(){
        int event_generator = new java.util.Random().nextInt(4);
        
        if(event_generator==0){
            event = "Blizzard";
            wall.setHealthpoint(wall.getHealthpoint()-50);
        }
        else if(event_generator==1){
            event = "Avalanche";
            citizen.setEmotional(citizen.getEmotional()+50);
            citizen.setNervous(citizen.getNervous()+50);
            citizen.setLazy(citizen.getLazy()+50);
        }
        else if(event_generator==2){
            event = "Hunger";
            tower.setAccuracy(tower.getAccuracy()-20);
        }
        else if(event_generator==3){
            event = "Tour Group";
            gold += 100;
        }
    }
    
    public void event_selector(Tax tax){
        System.out.println("NEW SEASON!\n");
        switch(season_time){
            case 0:
                season = "Spring";
                
                System.out.println("                    _");
                System.out.println("                  ()_                          wWWWw   _");
                System.out.println("      @@@@       ()@()   vVVVv     _     @@@@  (_) ()_");
                System.out.println("     @@()@@ wWWWw  ()\\    (_)   _()_  @@()@@   Y  ()@()");
                System.out.println("      @@@@  (_)     `|/    Y    ()@()  @@@@   \\|/   (_)\\");
                System.out.println("       /      Y       \\|    \\|/    /(_)    \\|      |/      |");
                System.out.println("    \\ |     \\ |/       | / \\ | /  \\|/       |/    \\|     \\|/");
                System.out.println("jgs \\\\|//   \\\\|///  \\\\\\|//\\\\\\|/// \\|///  \\\\\\|// \\\\|//  \\\\\\|// ");
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                
                spring_event();
                break;
            case 1:
                season = "Summer";
                
                System.out.println("           '");
                System.out.println("          .      '      .");
                System.out.println("    .      .     :     .      .");
                System.out.println("     '.        __       .'");
                System.out.println("       '  .-\"`      `\"-. '");
                System.out.println("        .'                '.");
                System.out.println("`'--. /                      \\ .--'`");
                System.out.println("     /                         \\");
                System.out.println("    ;                           ;");
                System.out.println("- --|                            | -- -");
                System.out.println("    |     _.                     |");
                System.out.println("    ;    /_`A   ,               ;");
                System.out.println(" .-' \\   |= |;..}{_            //  '-.");
                System.out.println("    .-\"\"-|.' # '. `  `.-\"{}<.");
                System.out.println("          / 1938  \\     \\  x   `\"");
                System.out.println("     ----/         \\_.-'|--X----");
                System.out.println("     -=_ |         |    |- X.  =_");
                System.out.println("    - _ |___|.-'|_X-X##");
                System.out.println("    jgs `'-.||;:;_.-'` '::.  `\"-");
                System.out.println("   .:;.      .:.   ::.     '::.");
                
                summer_event();
                break;
            case 2:
                season = "Autumn";
                
                System.out.println("     .\\^/.          ");
                System.out.println("   . |`|/| .         ");
                System.out.println("   |\\|\\|'|/|       ");
                System.out.println(".--'-\\`|/-''--.     ");
                System.out.println(" \\`-._\\|./.-'/     ");
                System.out.println("  >`-._|/.-'<        ");
                System.out.println(" '|/|\\|'      ");
                System.out.println("       |");
                
                autumn_event();
                break;
            case 3:
                season = "Winter";
                
                System.out.println("                ()");
                System.out.println("                /\\");
                System.out.println("               //\\\\");
                System.out.println("              <<  >>");
                System.out.println("          ()   \\\\//   ()");
                System.out.println("()._____   /\\   \\\\   /\\   _____.()");
                System.out.println("   \\.--.\\ //\\\\ //\\\\ //\\\\ /.--./");
                System.out.println("    \\\\__\\\\/__\\//__\\//__\\\\/__//");
                System.out.println("     '--/\\\\--//\\--//\\--/\\\\--'");
                System.out.println("        \\\\\\\\///\\\\//\\\\\\////");
                System.out.println("    ()-= >>\\\\< <\\\\> >\\\\<< =-()");
                System.out.println("        ////\\\\\\//\\\\///\\\\\\\\");
                System.out.println("     .--\\\\/--\\//--\\//--\\//--.");
                System.out.println("    //\"\"/\\\\\"\"//\\\"\"//\\\"\"//\\\"\"\\\\");
                System.out.println("   /'--'/ \\\\// \\\\// \\\\// \\'--'\\");
                System.out.println(" ()`\"\"\"`   \\/   //   \\/   `\"\"\"\"`()");
                System.out.println("          ()   //\\\\   ()");
                System.out.println("              <<  >>");
                System.out.println("        jgs    \\\\//");
                System.out.println("                \\/");
                System.out.println("                ()");
                
                
                winter_event();
                break;
        }
        
        tax.setCurrent_gold(tax.getCurrent_gold()+gold);
    }
    
    public void nextSesaon(){
        time+=1;
    }
    
    public void updateTime(){
        season_time = time%4;
        year  = (time/4)+1;
    }
    
    
    // diversity
    public void spring_event_diverse(int event_generator){
        
        if(event_generator==0){
            event = "Reinforcement";
            tower.setAttackpoint(tower.getAttackpoint()+1);
        }
        else if(event_generator==1){
            event = "Visitors";
            gold += 100;
        }
        else if(event_generator==2){
            event = "Festival";
            citizen.setBerserk(citizen.getBerserk()+50);
            citizen.setDiligent(citizen.getDiligent()+50);
            citizen.setFearless(citizen.getFearless()+50);
        }
        else if(event_generator==3){
            event = "Thunderstorm";
            citizen.setEmotional(citizen.getEmotional()+50);
            citizen.setNervous(citizen.getNervous()+50);
            citizen.setLazy(citizen.getLazy()+50);
        }
        else if(event_generator==4){
            event = "Hurricane";
            wall.setHealthpoint(wall.getHealthpoint()-50);
        }
    }
    
    public void summer_event_diverse(int event_generator){
        
        if(event_generator==0){
            event = "Drought";
            wall.setHealthpoint(wall.getHealthpoint()-50);
        }
        else if(event_generator==1){
            event = "Outing";
            citizen.setBerserk(citizen.getBerserk()+50);
            citizen.setDiligent(citizen.getDiligent()+50);
            citizen.setFearless(citizen.getFearless()+50);
        }
        else if(event_generator==2){
            event = "Heatstroke";
            citizen.setEmotional(citizen.getEmotional()+50);
            citizen.setNervous(citizen.getNervous()+50);
            citizen.setLazy(citizen.getLazy()+50);
        }
        else if(event_generator==3){
            event = "Volcanic Explosion";
            tower.setAccuracy(tower.getAccuracy()-20);
        }
        else if(event_generator==4){
            event = "Wildfire";
            citizen.setEmotional(citizen.getEmotional()+50);
            citizen.setNervous(citizen.getNervous()+50);
            citizen.setLazy(citizen.getLazy()+50);
        }
    }
    
    public void autumn_event_diverse(int event_generator){
                
        if(event_generator==0){
            event = "Rainy";
            tower.setAccuracy(tower.getAccuracy()-20);
        }
        else if(event_generator==1){
            event = "Flood";
            wall.setHealthpoint(wall.getHealthpoint()-50);
        }
        else if(event_generator==2){
            event = "Harvest";
            gold += 100;
        }
        else if(event_generator==3){
            event = "Earthquake";
            citizen.setEmotional(citizen.getEmotional()+50);
            citizen.setNervous(citizen.getNervous()+50);
            citizen.setLazy(citizen.getLazy()+50);
        }
        else if(event_generator==4){
            event = "Tsunami";
            wall.setHealthpoint(wall.getHealthpoint()-50);
        }
    }
    
    public void winter_event_diverse(int event_generator){
        
        if(event_generator==0){
            event = "Blizzard";
            wall.setHealthpoint(wall.getHealthpoint()-50);
        }
        else if(event_generator==1){
            event = "Avalanche";
            citizen.setEmotional(citizen.getEmotional()+50);
            citizen.setNervous(citizen.getNervous()+50);
            citizen.setLazy(citizen.getLazy()+50);
        }
        else if(event_generator==2){
            event = "Hunger";
            tower.setAccuracy(tower.getAccuracy()-20);
        }
        else if(event_generator==3){
            event = "Tour Group";
            gold += 100;
        }
        else if(event_generator==4){
            event = "Christmas";
            gold += 100;
        }
        else if(event_generator==5){
            event = "Snow Festival";
            citizen.setBerserk(citizen.getBerserk()+50);
            citizen.setDiligent(citizen.getDiligent()+50);
            citizen.setFearless(citizen.getFearless()+50);
        }
    }
    
    public void event_selector_hard(Tax tax){
        int event_number = 0;
        int event_generator_1=0; 
        int event_generator_2=0;
        if(season_time==3){
            event_generator_1 = new java.util.Random().nextInt(6);
            event_generator_2 = new java.util.Random().nextInt(6);
        }
        else{
            event_generator_1 = new java.util.Random().nextInt(5);
            event_generator_2 = new java.util.Random().nextInt(5);
        }
        while(event_generator_1==event_generator_2) event_generator_2 = new java.util.Random().nextInt(6);
        
        System.out.println("NEW SEASON!\n");
        
        switch(season_time){
            case 0:
                season = "Spring";
                
                System.out.println("                    _");
                System.out.println("                  ()_                          wWWWw   _");
                System.out.println("      @@@@       ()@()   vVVVv     _     @@@@  (_) ()_");
                System.out.println("     @@()@@ wWWWw  ()\\    (_)   _()_  @@()@@   Y  ()@()");
                System.out.println("      @@@@  (_)     `|/    Y    ()@()  @@@@   \\|/   (_)\\");
                System.out.println("       /      Y       \\|    \\|/    /(_)    \\|      |/      |");
                System.out.println("    \\ |     \\ |/       | / \\ | /  \\|/       |/    \\|     \\|/");
                System.out.println("jgs \\\\|//   \\\\|///  \\\\\\|//\\\\\\|/// \\|///  \\\\\\|// \\\\|//  \\\\\\|// ");
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                
                
                event_number = event_generator_1;
                spring_event_diverse(event_number);
                setEvent_hard_1(getEvent());
                
                event_number = event_generator_2;
                spring_event_diverse(event_number);
                setEvent_hard_2(getEvent());
                break;
            case 1:
                season = "Summer";
                
                System.out.println("           '");
                System.out.println("          .      '      .");
                System.out.println("    .      .     :     .      .");
                System.out.println("     '.        __       .'");
                System.out.println("       '  .-\"`      `\"-. '");
                System.out.println("        .'                '.");
                System.out.println("`'--. /                      \\ .--'`");
                System.out.println("     /                         \\");
                System.out.println("    ;                            ;");
                System.out.println("- --|                            | -- -");
                System.out.println("    |     _.                     |");
                System.out.println("    ;    /_`A   ,               ;");
                System.out.println(" .-' \\   |= |;..}{_           //  '-.");
                System.out.println("    .-\"\"-|.' # '. `  `.-\"{}<.");
                System.out.println("          / 1938  \\     \\  x   `\"");
                System.out.println("     ----/         \\_.-'|--X----");
                System.out.println("     -=_ |         |    |- X.  =_");
                System.out.println("    - _ |___|.-'|_X-X##");
                System.out.println("    jgs `'-.||;:;_.-'` '::.  `\"-");
                System.out.println("   .:;.      .:.   ::.     '::.");
                
                
                event_number = event_generator_1;
                summer_event_diverse(event_number);
                setEvent_hard_1(getEvent());
                
                event_number = event_generator_2;
                summer_event_diverse(event_number);
                setEvent_hard_2(getEvent());
                break;
            case 2:
                season = "Autumn";
                
                System.out.println("     .\\^/.          ");
                System.out.println("   . |`|/| .         ");
                System.out.println("   |\\|\\|'|/|       ");
                System.out.println(".--'-\\`|/-''--.     ");
                System.out.println(" \\`-._\\|./.-'/     ");
                System.out.println("  >`-._|/.-'<        ");
                System.out.println(" '|/|\\|'      ");
                System.out.println("       |");
                
                event_number = event_generator_1;
                autumn_event_diverse(event_number);
                setEvent_hard_1(getEvent());
                
                event_number = event_generator_2;
                autumn_event_diverse(event_number);
                setEvent_hard_2(getEvent());
                break;
            case 3:
                season = "Winter";
                
                System.out.println("                ()");
                System.out.println("                /\\");
                System.out.println("               //\\\\");
                System.out.println("              <<  >>");
                System.out.println("          ()   \\\\//   ()");
                System.out.println("()._____   /\\   \\\\   /\\   _____.()");
                System.out.println("   \\.--.\\ //\\\\ //\\\\ //\\\\ /.--./");
                System.out.println("    \\\\__\\\\/__\\//__\\//__\\\\/__//");
                System.out.println("     '--/\\\\--//\\--//\\--/\\\\--'");
                System.out.println("        \\\\\\\\///\\\\//\\\\\\////");
                System.out.println("    ()-= >>\\\\< <\\\\> >\\\\<< =-()");
                System.out.println("        ////\\\\\\//\\\\///\\\\\\\\");
                System.out.println("     .--\\\\/--\\//--\\//--\\//--.");
                System.out.println("    //\"\"/\\\\\"\"//\\\"\"//\\\"\"//\\\"\"\\\\");
                System.out.println("   /'--'/ \\\\// \\\\// \\\\// \\'--'\\");
                System.out.println(" ()`\"\"\"`   \\/   //   \\/   `\"\"\"\"`()");
                System.out.println("          ()   //\\\\   ()");
                System.out.println("              <<  >>");
                System.out.println("        jgs    \\\\//");
                System.out.println("                \\/");
                System.out.println("                ()");
                
                
                event_number = event_generator_1;
                winter_event_diverse(event_number);
                setEvent_hard_1(getEvent());
                
                event_number = event_generator_2;
                winter_event_diverse(event_number);
                setEvent_hard_2(getEvent());
                break;
        }
        
        tax.setCurrent_gold(tax.getCurrent_gold()+gold);
    }
    
}
