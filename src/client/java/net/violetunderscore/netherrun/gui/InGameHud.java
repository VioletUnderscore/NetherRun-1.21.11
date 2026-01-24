package net.violetunderscore.netherrun.gui;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class InGameHud {
    public void renderInGameHud(DrawContext context, boolean active) {
        if (active) {
            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    Identifier.of("netherrun", "textures/gui/scores_display.png"),
                    0, 0,
                    0, 0,
                    160, 40,
                    160, 40
            );
        }
    }
}
