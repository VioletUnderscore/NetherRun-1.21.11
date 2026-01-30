package net.violetunderscore.netherrun.inventory;

import net.violetunderscore.netherrun.NetherRun;
import net.violetunderscore.netherrun.math.TimeConvert;

public class NetherRunCooldown {
    private int cd;
    private int stack;
    private final int cdl;
    private final int maxStack;

    public NetherRunCooldown(int cd, int stack) {
        this.maxStack = stack;
        this.cdl = cd;
        reset();
    }

    public void reset() {
        cd = cdl;
        stack = maxStack;
    }

    public void decrement() {
        stack--;
    }

    public void tick() {
        if (stack < maxStack) {
            if (cd > 0) {
                cd--;
            } else {
                stack++;
                cd = cdl;
            }
        }
    }

    public int stackCount() {
        return stack;
    }
}
