package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DropExperienceBlock.class)
public abstract class MixinDropExperienceBlock extends Block {

    protected MixinDropExperienceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BlockState playerWillDestroy(@NotNull Level level,
                                                 @NotNull BlockPos pos,
                                                 @NotNull BlockState state,
                                                 @NotNull Player player) {
        if (!player.getAbilities().instabuild) {
            float chance = expallidus$nuggetChance(state);
            if (chance > 0.0F && level.getRandom().nextFloat() < chance) {
                Block.popResource(level, pos, expallidus$nugget(state));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Unique
    private static float expallidus$nuggetChance(BlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) {
            return 0.25F;
        }
        if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
            return 0.25F;
        }
        if (block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE) {
            return 0.5F;
        }
        return 0.0F;
    }

    @Unique
    private static ItemStack expallidus$nugget(BlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) {
            return new ItemStack(Items.GOLD_NUGGET);
        }
        if (block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE) {
            return new ItemStack(Items.COPPER_NUGGET);
        }
        return new ItemStack(Items.IRON_NUGGET);
    }
}
