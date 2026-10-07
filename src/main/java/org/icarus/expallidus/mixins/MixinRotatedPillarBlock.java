package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.icarus.expallidus.utils.EnchantmentUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RotatedPillarBlock.class)
public abstract class MixinRotatedPillarBlock extends Block {

    protected MixinRotatedPillarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BlockState playerWillDestroy(@NotNull Level level,
                                                 @NotNull BlockPos pos,
                                                 @NotNull BlockState state,
                                                 @NotNull Player player) {
        if (expallidus$isLog(state) && !player.getAbilities().instabuild) {
            int fortune = EnchantmentUtils.getEnchantmentLevel(level, Enchantments.FORTUNE, player.getMainHandItem());
            if (fortune >= 1 && fortune <= 3) {
                int amount = fortune + (level.getRandom().nextFloat() < 0.5F ? 1 : 0);
                Block.popResource(level, pos, new ItemStack(Items.STICK, amount));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Unique
    private static boolean expallidus$isLog(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.OAK_LOG
            || block == Blocks.SPRUCE_LOG
            || block == Blocks.BIRCH_LOG
            || block == Blocks.JUNGLE_LOG
            || block == Blocks.ACACIA_LOG
            || block == Blocks.DARK_OAK_LOG
            || block == Blocks.MANGROVE_LOG
            || block == Blocks.CHERRY_LOG;
    }
}
