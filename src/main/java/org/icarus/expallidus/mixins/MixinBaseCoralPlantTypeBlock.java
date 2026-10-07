package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.icarus.expallidus.utils.CoralUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BaseCoralPlantTypeBlock.class)
public abstract class MixinBaseCoralPlantTypeBlock extends Block {

    protected MixinBaseCoralPlantTypeBlock(Properties properties) {
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
        if (!stack.is(Items.BONE_MEAL) || level.isClientSide()) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        return CoralUtils.tryRevive(level, pos, state, player, hand, stack)
            ? InteractionResult.SUCCESS
            : super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
