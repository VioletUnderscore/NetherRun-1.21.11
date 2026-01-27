package net.violetunderscore.netherrun;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.violetunderscore.netherrun.gui.InGameHud;
import net.violetunderscore.netherrun.gui.data.HudData;
import net.violetunderscore.netherrun.network.payloads.AllTimersPayload;
import net.violetunderscore.netherrun.network.payloads.OneTimerPayload;
import net.violetunderscore.netherrun.network.payloads.ToggleBoardPayload;

public class NetherRunClient implements ClientModInitializer {
    private static HudData hudData = new HudData();

	@Override
	public void onInitializeClient() {
        registerPayloadRecievers();

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            new InGameHud().renderInGameHud(drawContext, hudData);
        });
	}



    private void registerPayloadRecievers() {
        ClientPlayNetworking.registerGlobalReceiver(ToggleBoardPayload.ID, (payload, context) -> {
            hudData.setActive(payload.on());
        });
        ClientPlayNetworking.registerGlobalReceiver(AllTimersPayload.ID, (payload, context) -> {
            hudData.setTeam1Timer(payload.team1());
            hudData.setTeam2Timer(payload.team2());
            hudData.setTargetTimer(payload.max());
        });
        ClientPlayNetworking.registerGlobalReceiver(OneTimerPayload.ID, (payload, context) -> {
            switch (payload.team()) {
                case 1:
                    hudData.setTeam1Timer(payload.score());
                    break;
                case 2:
                    hudData.setTeam2Timer(payload.score());
                    break;
            }
        });
    }
}