package org.icarus.expallidus.utils;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class CoralUtils {

    private static final Map<Block, Block> REVIVAL = Map.ofEntries(
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

    private CoralUtils() {
    }

    public static boolean tryRevive(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, ItemStack stack) {
        Block target = REVIVAL.get(state.getBlock());
        if (target == null) {
            return false;
        }

        BlockState newState = target.defaultBlockState();
        if (newState.hasProperty(BlockStateProperties.WATERLOGGED)) {
            newState = newState.setValue(BlockStateProperties.WATERLOGGED, false);
        }
        level.setBlockAndUpdate(pos, newState);
        level.playSound(null, pos, SoundEvents.CORAL_BLOCK_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);
        level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 0.5F, 1.0F);
        player.swing(hand);
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
            stack.shrink(1);
        }
        return true;
    }
}
