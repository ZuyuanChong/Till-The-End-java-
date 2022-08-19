package TillTheEnd;

public abstract class SameBehaviour {
    private int attackpoint;
    private int criticalChance;
    private int accuracy;
    
    public abstract void level_up(Tax tax, int input);
    public abstract void attack_or_miss(int accuracy, int critical_total, int critical_chance);
    public abstract String toString(Season season, Tax tax);
}

