package net.violetunderscore.netherrun.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.violetunderscore.netherrun.NetherRun;
import net.violetunderscore.netherrun.math.TimeConvert;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NetherRunInventory {
    private final ItemStack NOTHING = Items.AIR.getDefaultStack();

    private final int[] slots = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private final ItemStack[] abilities = {NOTHING, NOTHING, NOTHING};
    private final UUID uuid;
    private final MinecraftServer server;

    private final Map<Integer, NetherRunCooldown> cooldownMap = new HashMap<>();

    public NetherRunInventory(UUID uuid, MinecraftServer server) {
        this.uuid = uuid;
        this.server = server;
        cooldownMap.put(1, new NetherRunCooldown(TimeConvert.s2t(1), 64));
        cooldownMap.put(3, new NetherRunCooldown(TimeConvert.s2t(3), 5));
    }

    public void swapSlots(int a, int b) {
        int v = slots[a];
        slots[a] = slots[b];
        slots[b] = v;
    }

    public void setSlots(int[] newSlots) {
        System.arraycopy(newSlots, 0, slots, 0, 9);
    }

    public int getSlot(int v) {
        return slots[v];
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

    public ItemStack stackForSlot(int i, boolean runner) {
        if (i >= 6) {
            return abilities[i - 6];
        } else {
            ItemStack[] isa = BaseKits.getBaseKitHunter();
            if (runner) {
                isa = BaseKits.getBaseKitRunner();
            }

            if (cooldownMap.containsKey(i)) {
                NetherRunCooldown nrc = cooldownMap.get(i);
                if (nrc.stackCount() == 0) {
                    return NOTHING;
                } else {
                    return isa[i].copyWithCount(nrc.stackCount());
                }
            } else {
                return isa[i];
            }
        }
    }

    public void fillInventory(UUID ruuid) {
        ServerPlayerEntity p = server.getPlayerManager().getPlayer(uuid);
        if (p != null) {
            for (int v = 0; v <= 8; v++) {
                if (ruuid != null) {
                    p.getInventory().setStack(getSlot(v), stackForSlot(v, ruuid.equals(uuid)));
                } else {
                    p.getInventory().setStack(getSlot(v), stackForSlot(v, false));
                }
            }
        }
    }

    public void tickCooldowns() {
        for (NetherRunCooldown nrc : cooldownMap.values()) {
            nrc.tick();
        }
    }

    public void decrementSlot(int slot) {
        if (cooldownMap.containsKey(slot)) {
            cooldownMap.get(slot).decrement();
        }
    }
}