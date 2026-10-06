package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.icarus.expallidus.utils.ItemUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class MixinServerPlayerGameMode {

    @Shadow
    @Final
    protected ServerPlayer player;

    @Inject(method = "destroyBlock(Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
    private void expallidus$preventBrokenToolBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        ItemStack tool = this.player.getMainHandItem();
        if (tool.isEmpty() || tool.is(Items.BUCKET)) {
            return;
        }
        if (!ItemUtils.isBroken(tool)) {
            return;
        }
        cir.setReturnValue(false);
        ItemUtils.playBrokenFeedback(this.player, "该工具已损坏。");
    }
}
