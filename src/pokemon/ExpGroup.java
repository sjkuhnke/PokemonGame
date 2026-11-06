package pokemon;

public final class ExpGroup {
    private ExpGroup() {}
    
    public static long total(int group, int n) {
        if (n <= 1) return 0;
        long c = (long) n * n * n;
        switch (group) {
            case 0: return c;
            case 4: return 4 * c / 5;
            case 5: return 5 * c / 4;
            case 3: return Math.max(0, 6 * c / 5 - 15L * n * n + 100L * n - 140);
            case 1:
                if (n < 50) return c * (100 - n) / 50;
                if (n < 68) return c * (150 - n) / 100;
                if (n < 98) return c * ((1911 - 10 * n) / 3) / 500;
                return c * (160 - n) / 100;
            case 2:
                if (n < 15) return c * ((n + 1) / 3 + 24) / 50;
                if (n < 36) return c * (n + 14) / 50;
                return c * (n / 2 + 32) / 50;
            default: return c; // unknown group -> Medium Fast
        }
    }
    
    public static int toNext(int group, int level) {
        if (level >= 100) return 0;
        return (int) (total(group, level + 1) - total(group, level));
    }
    
    public static int convertExp(int oldExp, int oldExpMax, int group, int level) {
        if (oldExpMax <= 0) return 0;
        return (int) ((long) oldExp * toNext(group, level) / oldExpMax);
    }

    /**
     * @param baseYield   species base exp yield b
     * @param foeLevel    L
     * @param winnerLevel Lp
     * @param participants s (Pokemon that fought + Exp Share holders)
     * @param trainer     true for trainer battles (x1.5)
     * @param luckyEgg    holder has Lucky Egg (x1.5)
     */
    public static int gain(int baseYield, int foeLevel, int winnerLevel, int participants, boolean trainer, boolean luckyEgg) {
        double base = baseYield * foeLevel / 5.0 / Math.max(1, participants);
        double scale = Math.pow((2.0 * foeLevel + 10) / (foeLevel + winnerLevel + 10), 2.5);
        double exp = base * scale + 1;
        if (trainer) exp *= 1.5;
        if (luckyEgg) exp *= 1.5;
        return Math.max(1, (int) exp);
    }
    
    public static String getExpGroupName(int group) {
    	switch (group) {
    	case 0: return "Medium Fast";
    	case 1: return "Erratic";
    	case 2: return "Fluctuating";
    	case 3: return "Medium Slow";
    	case 4: return "Fast";
    	case 5: return "Slow";
    	default: return "?";
    	}
    }
}