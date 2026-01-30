package net.violetunderscore.netherrun.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import net.violetunderscore.netherrun.NetherRun;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(at = @At("HEAD"), method = "onDeath", cancellable = true)
    public void onDeath(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity self = (ServerPlayerEntity)(Object)this;
        PlayerEntity rp = NetherRun.getGame().runningPlayer();
        if (rp != null && self.getUuid().equals(rp.getUuid())) {
            self.setHealth(20);
            self.changeGameMode(GameMode.SPECTATOR);
            ci.cancel();
            self.closeHandledScreen();
            NetherRun.getGame().endRound();
        }
    }

    @Inject(at = @At("HEAD"), method = "damage", cancellable = true)
    public void damage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity self = (ServerPlayerEntity)(Object)this;

        if (NetherRun.getGame().isPlayingPlayer(self.getUuid())) {
            if (!(self.getUuid().equals(NetherRun.getGame().runningPlayer().getUuid()))) {
                cir.setReturnValue(false);
            }
        }
    }
}
