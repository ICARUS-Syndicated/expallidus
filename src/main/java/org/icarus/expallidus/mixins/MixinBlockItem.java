package org.icarus.expallidus.mixins;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.icarus.expallidus.utils.ItemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class MixinBlockItem {

    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void expallidus$preventStoneNuggetPlacement(
        BlockPlaceContext context,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (ItemUtils.isStoneNugget(context.getItemInHand())) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
