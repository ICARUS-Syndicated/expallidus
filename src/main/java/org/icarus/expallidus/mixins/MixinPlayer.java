package org.icarus.expallidus.mixins;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.icarus.expallidus.utils.ItemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class MixinPlayer {

    @Inject(method = "attack(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void expallidus$preventBrokenWeaponAttack(Entity target, CallbackInfo ci) {
        Player self = (Player) (Object) this;
        ItemStack tool = self.getMainHandItem();
        if (tool.isEmpty()) {
            return;
        }
        if (!ItemUtils.isBroken(tool)) {
            return;
        }
        ci.cancel();
        if (self instanceof ServerPlayer serverPlayer) {
            ItemUtils.playBrokenFeedback(serverPlayer, "该工具已损坏。");
        }
    }
}
