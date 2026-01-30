package net.violetunderscore.netherrun.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.violetunderscore.netherrun.NetherRun;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At("RETURN"))
    private void place(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> ci) {
        if (ci.getReturnValue().isAccepted() && !context.getWorld().isClient()) {
            PlayerEntity player = context.getPlayer();
            if (player == null) return;

            if (context.getHand() == Hand.MAIN_HAND) {
                NetherRun.getIm().decrementSlot(player.getUuid(), player.getInventory().getSelectedSlot());
            }
        }
    }
}
