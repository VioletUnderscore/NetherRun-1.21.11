package net.violetunderscore.netherrun.math;

public class TimeConvert {
    public static int h2t(int i) { return i * 72000; }
    public static int m2t(int i) { return i * 1200; }
    public static int s2t(int i) { return i * 20; }
    public static int t2s(int i) { return i / 20; }
    public static int t2m(int i) { return i / 1200; }
    public static int t2h(int i) { return i / 72000; }
}
