package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public abstract class MixinAxeItem {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void expallidus$useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(Blocks.BAMBOO)
            || state.getValue(BambooStalkBlock.AGE) != BambooStalkBlock.AGE_THICK_BAMBOO
            || state.getValue(BambooStalkBlock.LEAVES) != BambooLeaves.NONE
            || state.getValue(BambooStalkBlock.STAGE) != BambooStalkBlock.STAGE_GROWING) {
            return;
        }
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        player.swing(context.getHand());
        level.setBlock(pos, Blocks.BAMBOO_FENCE.defaultBlockState(), Block.UPDATE_NONE);
        level.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 0.5F, 1.0F);
        if (!player.getAbilities().instabuild) {
            context.getItemInHand().hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
        }
        cir.setReturnValue(InteractionResult.SUCCESS);
        cir.cancel();
    }
}
