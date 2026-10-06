package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CryingObsidianBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CryingObsidianBlock.class)
public abstract class MixinCryingObsidianBlock extends Block {
    public MixinCryingObsidianBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack stack,
                                                   @NotNull BlockState state,
                                                   @NotNull Level level,
                                                   @NotNull BlockPos pos,
                                                   @NotNull Player player,
                                                   @NotNull InteractionHand hand,
                                                   @NotNull BlockHitResult hitResult) {
        if (!stack.is(Items.WATER_BUCKET) || level.isClientSide()) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        level.playSound(null, pos, SoundEvents.GILDED_BLACKSTONE_FALL, SoundSource.BLOCKS, 0.5F, 1.0F);
        player.swing(hand);
        Block.popResource(level, pos.above(), new ItemStack(Items.GHAST_TEAR, 1 + player.getRandom().nextInt(4)));
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.DRIPPING_OBSIDIAN_TEAR,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 25, 0.5, 0.5, 0.5, 0.02);
        }

        if (!player.getAbilities().instabuild) {
            player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
            level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), Block.UPDATE_ALL);
        }
        return InteractionResult.SUCCESS;
    }
}
