package net.violetunderscore.netherrun.gui.data;

public class HudData {
    private static boolean active = false;
    private static int team1Timer = 0;
    private static int team2Timer = 0;
    private static int targetTimer = 0;

    public void setActive(boolean b) { active = b; }
    public void setTeam1Timer(int i) { team1Timer = i; }
    public void setTeam2Timer(int i) { team2Timer = i; }
    public void setTargetTimer(int i) { targetTimer = i; }

    public boolean getActive() { return active; }
    public int getTeam1Timer() { return team1Timer; }
    public int getTeam2Timer() { return team2Timer; }
    public int getTargetTimer() { return targetTimer; }
}
