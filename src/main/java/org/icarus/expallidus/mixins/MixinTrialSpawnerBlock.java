package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.icarus.expallidus.utils.EnchantmentUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TrialSpawnerBlock.class)
public abstract class MixinTrialSpawnerBlock extends Block {

    protected MixinTrialSpawnerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BlockState playerWillDestroy(@NotNull Level level,
                                                 @NotNull BlockPos pos,
                                                 @NotNull BlockState state,
                                                 @NotNull Player player) {
        if (player instanceof ServerPlayer serverPlayer
            && serverPlayer.gameMode.getGameModeForPlayer() == GameType.SURVIVAL
            && expallidus$canPickUp(player.getMainHandItem(), level)) {
            Block.popResource(level, pos, new ItemStack(this.asItem()));
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Unique
    private static boolean expallidus$canPickUp(ItemStack tool, Level level) {
        return (tool.is(Items.DIAMOND_PICKAXE) || tool.is(Items.NETHERITE_PICKAXE))
            && EnchantmentUtils.hasEnchantment(level, Enchantments.SILK_TOUCH, tool);
    }
}
