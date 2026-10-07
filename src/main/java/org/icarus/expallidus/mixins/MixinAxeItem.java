package org.icarus.expallidus.mixins;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public abstract class MixinAxeItem {

    @Shadow
    @Final
    @Mutable
    protected static Map<Block, Block> STRIPPABLES;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void expallidus$addBambooStrippable(CallbackInfo ci) {
        Map<Block, Block> strippables = new HashMap<>(STRIPPABLES);
        strippables.put(Blocks.BAMBOO, Blocks.BAMBOO_FENCE);
        STRIPPABLES = strippables;
    }

    @Inject(method = "getStripped", at = @At("HEAD"), cancellable = true)
    private void expallidus$stripBamboo(BlockState unstrippedState,
                                        CallbackInfoReturnable<Optional<BlockState>> cir) {
        if (!unstrippedState.is(Blocks.BAMBOO)) {
            return;
        }
        boolean strippable = unstrippedState.getValue(BambooStalkBlock.AGE) == BambooStalkBlock.AGE_THICK_BAMBOO
            && unstrippedState.getValue(BambooStalkBlock.LEAVES) == BambooLeaves.NONE
            && unstrippedState.getValue(BambooStalkBlock.STAGE) == BambooStalkBlock.STAGE_GROWING;
        cir.setReturnValue(strippable ? Optional.of(Blocks.BAMBOO_FENCE.defaultBlockState()) : Optional.empty());
        cir.cancel();
    }
}
