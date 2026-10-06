package org.icarus.expallidus.mixins;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.level.storage.LevelData;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer {

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void expallidus$avoidVoidDamage(
        ServerLevel level,
        DamageSource damageSource,
        float amount,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (!damageSource.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            return;
        }
        if (!level.dimension().identifier().getPath().equals("lobby")) {
            return;
        }
        ServerPlayer self = (ServerPlayer) (Object) this;
        LevelData.RespawnData respawnData = level.serverLevelData.getRespawnData();
        BlockPos spawnPos = respawnData.pos();
        self.teleportTo(
            level,
            spawnPos.getX() + 0.5,
            spawnPos.getY(),
            spawnPos.getZ() + 0.5,
            Set.of(),
            respawnData.yaw(),
            respawnData.pitch(),
            false,
            PlayerTeleportEvent.TeleportCause.UNKNOWN
        );
        cir.setReturnValue(false);
    }
}
