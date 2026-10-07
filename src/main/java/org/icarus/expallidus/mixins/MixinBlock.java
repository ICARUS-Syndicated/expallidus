package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.icarus.expallidus.utils.ItemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class MixinBlock {

    @Inject(method = "playerWillDestroy", at = @At("HEAD"))
    private void expallidus$dropStoneNugget(
        Level level,
        BlockPos pos,
        BlockState state,
        Player player,
        CallbackInfoReturnable<BlockState> cir
    ) {
        if (state.is(Blocks.STONE) && !player.getAbilities().instabuild && level.getRandom().nextFloat() < 0.125F) {
            Block.popResource(level, pos, ItemUtils.createStoneNugget());
        }
    }
}
