package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WebBlock.class)
public abstract class MixinWebBlock extends Block {
    public MixinWebBlock(Properties properties) {
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
        if (!stack.is(Items.GLASS_BOTTLE) || level.isClientSide()) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.5F, 1.0F);
        player.swing(hand);
        level.destroyBlock(pos, true, null, 512);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
            player.drop(PotionContents.createItemStack(Items.POTION, Potions.WATER), false);
        }
        return InteractionResult.SUCCESS;
    }
}
