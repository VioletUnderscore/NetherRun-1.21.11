package net.violetunderscore.netherrun.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.violetunderscore.netherrun.gui.data.HudData;

public class InGameHud {
    public void renderInGameHud(DrawContext context, HudData hudData) {
        if (hudData.getActive()) {
            hudFor1v1(context, hudData);
        }
    }

    public void hudFor1v1(DrawContext context, HudData hudData) {
        MinecraftClient client = MinecraftClient.getInstance();

        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                Identifier.of("netherrun", "textures/gui/scores_display.png"),
                0, 0,
                0, 0,
                160, 40,
                160, 40
        );
        context.drawTextWithShadow(
                client.textRenderer,
                formatTime(hudData.getTeam1Timer()),
                26, 4,
                0xFFFFFFFF
        );
        context.drawTextWithShadow(
                client.textRenderer,
                formatTime(hudData.getTeam2Timer()),
                109, 4,
                0xFFFFFFFF
        );
        context.drawTextWithShadow(
                client.textRenderer,
                formatTime(hudData.getTargetTimer()),
                85, 21,
                0xFFFFFFFF
        );
    }



    private String formatTime(int ticks) {
        int totalSeconds = ticks / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
