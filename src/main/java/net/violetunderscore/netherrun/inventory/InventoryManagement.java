package net.violetunderscore.netherrun.inventory;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.violetunderscore.netherrun.NetherRun;

import java.util.*;

public class InventoryManagement {
    private final Map<UUID, NetherRunInventory> inventories = new HashMap<>();

    private final MinecraftServer server;

    public InventoryManagement(MinecraftServer server) {
        this.server = server;
    }

    public void createInventory(UUID uuid) {
        if (!inventories.containsKey(uuid)) {
            inventories.put(uuid, new NetherRunInventory(uuid, server));
        }
    }

    public void deleteInventory(UUID uuid) {
        inventories.remove(uuid);
    }

    public void tickAllCooldowns() {
        for (NetherRunInventory nri : inventories.values()) {
            nri.tickCooldowns();
        }
    }

    public void fillAllInventories() {
        for (NetherRunInventory nri : inventories.values()) {
            nri.fillInventory(NetherRun.getGame().runningPlayerID());
        }
    }

    public void decrementSlot(UUID uuid, int slot) {
        if (inventories.containsKey(uuid)) {
            inventories.get(uuid).decrementSlot(slot);
        }
    }
}
