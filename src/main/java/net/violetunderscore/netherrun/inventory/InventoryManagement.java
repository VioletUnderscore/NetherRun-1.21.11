package net.violetunderscore.netherrun.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.*;

public class InventoryManagement {
    private final Map<UUID, NetherRunInventory> inventories = new HashMap<>();

    public void createInventory(UUID uuid) {
        if (!inventories.containsKey(uuid)) {
            inventories.put(uuid, new NetherRunInventory());
        }
    }

    public void deleteInventory(UUID uuid) {
        inventories.remove(uuid);
    }
}
