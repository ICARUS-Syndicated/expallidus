package org.icarus.expallidus.mixins;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public abstract class MixinBoneMealItem {
    private static final Map<Block, Block> EXPALLIDUS$CORAL_REVIVAL = Map.ofEntries(
        Map.entry(Blocks.DEAD_HORN_CORAL_BLOCK, Blocks.HORN_CORAL_BLOCK),
        Map.entry(Blocks.DEAD_BRAIN_CORAL_BLOCK, Blocks.BRAIN_CORAL_BLOCK),
        Map.entry(Blocks.DEAD_BUBBLE_CORAL_BLOCK, Blocks.BUBBLE_CORAL_BLOCK),
        Map.entry(Blocks.DEAD_FIRE_CORAL_BLOCK, Blocks.FIRE_CORAL_BLOCK),
        Map.entry(Blocks.DEAD_TUBE_CORAL_BLOCK, Blocks.TUBE_CORAL_BLOCK),
        Map.entry(Blocks.DEAD_HORN_CORAL_FAN, Blocks.HORN_CORAL_FAN),
        Map.entry(Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.BRAIN_CORAL_FAN),
        Map.entry(Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.BUBBLE_CORAL_FAN),
        Map.entry(Blocks.DEAD_FIRE_CORAL_FAN, Blocks.FIRE_CORAL_FAN),
        Map.entry(Blocks.DEAD_TUBE_CORAL_FAN, Blocks.TUBE_CORAL_FAN),
        Map.entry(Blocks.DEAD_HORN_CORAL, Blocks.HORN_CORAL),
        Map.entry(Blocks.DEAD_BRAIN_CORAL, Blocks.BRAIN_CORAL),
        Map.entry(Blocks.DEAD_BUBBLE_CORAL, Blocks.BUBBLE_CORAL),
        Map.entry(Blocks.DEAD_FIRE_CORAL, Blocks.FIRE_CORAL),
        Map.entry(Blocks.DEAD_TUBE_CORAL, Blocks.TUBE_CORAL)
    );

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void expallidus$useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Block target = EXPALLIDUS$CORAL_REVIVAL.get(level.getBlockState(pos).getBlock());
        if (target == null) {
            return;
        }
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        BlockState newState = target.defaultBlockState();
        if (newState.hasProperty(BlockStateProperties.WATERLOGGED)) {
            newState = newState.setValue(BlockStateProperties.WATERLOGGED, false);
        }
        level.setBlockAndUpdate(pos, newState);
        level.playSound(null, pos, SoundEvents.CORAL_BLOCK_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);
        level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 0.5F, 1.0F);
        player.swing(context.getHand());
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, newState),
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                20,
                0.0,
                0.0,
                0.0,
                0.0
            );
        }
        if (!player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        cir.setReturnValue(InteractionResult.SUCCESS);
        cir.cancel();
    }
}
