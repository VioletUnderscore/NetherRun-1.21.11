package net.violetunderscore.netherrun;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.violetunderscore.netherrun.gui.InGameHud;
import net.violetunderscore.netherrun.network.payloads.ToggleBoardPayload;

public class NetherRunClient implements ClientModInitializer {
    public static boolean active = false;

	@Override
	public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ToggleBoardPayload.ID, (payload, context) -> {
            active = payload.on();
        });

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            new InGameHud().renderInGameHud(drawContext, active);
        });
	}
}