package net.violetunderscore.netherrun.math;

public class TimeConvert {
    public static int hourToTick(int i) { return i * 72000; }
    public static int minuteToTick(int i) { return i * 1200; }
    public static int secondToTick(int i) { return i * 20; }
    public static int tickToSecond(int i) { return i / 20; }
    public static int tickToMinute(int i) { return i / 1200; }
    public static int tickToHour(int i) { return i / 72000; }
}
