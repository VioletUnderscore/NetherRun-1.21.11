package net.violetunderscore.netherrun.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BaseKits {
    private static final ItemStack NOTHING = Items.AIR.getDefaultStack();
    private static final ItemStack BLOCKS = Items.WHITE_CONCRETE.getDefaultStack().copyWithCount(64);
    private static final ItemStack SWORD = Items.DIAMOND_SWORD.getDefaultStack();
    private static final ItemStack PICKAXE = Items.NETHERITE_PICKAXE.getDefaultStack();
    private static final ItemStack BOAT = Items.OAK_BOAT.getDefaultStack().copyWithCount(5);


    private static final ItemStack[] baseKit = {
            SWORD,
            BLOCKS,
            NOTHING,
            BOAT,
            PICKAXE,
            NOTHING
    };

    public static ItemStack[] getBaseKitRunner() {
        return new ItemStack[] {
                SWORD,
                baseKit[1],
                baseKit[2],
                baseKit[3],
                baseKit[4],
                baseKit[5]
        };
    }

    public static ItemStack[] getBaseKitHunter() {
        return new ItemStack[] {
                baseKit[0],
                baseKit[1],
                baseKit[2],
                baseKit[3],
                baseKit[4],
                baseKit[5]
        };
    }
}
