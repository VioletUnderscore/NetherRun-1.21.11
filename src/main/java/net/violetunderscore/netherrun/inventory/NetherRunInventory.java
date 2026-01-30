package net.violetunderscore.netherrun.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class NetherRunInventory {
    private final ItemStack NOTHING = Items.AIR.getDefaultStack();

    private final int[] slots = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private final ItemStack[] abilities = {NOTHING, NOTHING, NOTHING};

    public void swapSlots(int a, int b) {
        int v = slots[a];
        slots[a] = slots[b];
        slots[b] = v;
    }

    public void setSlots(int[] newSlots) {
        System.arraycopy(newSlots, 0, slots, 0, 9);
    }

    public void newAbility(ItemStack item) {
        for (int v = 0; v <= 2; v++) {
            if (abilities[v].equals(NOTHING)) {
                abilities[v] = item;
                v = 99;
            }
        }
    }

    public void useAbility(int v) {
        int count = abilities[v].getCount();
        if (count == 1) {
            abilities[v] = NOTHING;
        } else {
            abilities[v].setCount(count - 1);
        }
    }
}