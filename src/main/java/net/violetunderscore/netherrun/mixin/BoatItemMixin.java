package net.violetunderscore.netherrun.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.violetunderscore.netherrun.NetherRun;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoatItem.class)
public abstract class BoatItemMixin {

    @Inject(method = "use", at = @At("RETURN"))
    private void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue().isAccepted() && !world.isClient()) {
            if (hand == Hand.MAIN_HAND) {
                NetherRun.getIm().decrementSlot(user.getUuid(), user.getInventory().getSelectedSlot());
            }
        }
    }
}
