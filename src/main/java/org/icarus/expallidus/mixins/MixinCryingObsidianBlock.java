package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CryingObsidianBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
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
        if (stack.is(Items.WATER_BUCKET)) {
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
            level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.GILDED_BLACKSTONE_FALL, SoundSource.BLOCKS, 1.0F, 1.0F);
            player.drop(new ItemStack(Items.GHAST_TEAR, player.random.nextInt(4)), false);
            level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
            level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
            level.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR,
                (double) pos.getX() + 0.5F,
                (double) pos.getY() + 1.0F,
                (double) pos.getZ() + 0.5F,
                0.0F,
                0.0F,
                0.0F);
            // player.swing(hand);
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
