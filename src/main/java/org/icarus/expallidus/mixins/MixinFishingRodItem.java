package org.icarus.expallidus.mixins;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.icarus.expallidus.utils.ItemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingRodItem.class)
public abstract class MixinFishingRodItem {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void expallidus$preventBrokenRodCast(
        Level level,
        Player player,
        InteractionHand hand,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (player.fishing != null) {
            return;
        }
        ItemStack rod = player.getItemInHand(hand);
        if (!ItemUtils.isBroken(rod)) {
            return;
        }
        cir.setReturnValue(InteractionResult.PASS);
        if (player instanceof ServerPlayer serverPlayer) {
            ItemUtils.playBrokenFeedback(serverPlayer, "该工具已损坏。");
        }
    }
}
