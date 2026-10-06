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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.icarus.expallidus.utils.EnchantmentUtils;
import org.icarus.expallidus.utils.ItemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class MixinBlock {

    @Inject(method = "playerWillDestroy", at = @At("HEAD"))
    private void expallidus$extraDrops(
        Level level,
        BlockPos pos,
        BlockState state,
        Player player,
        CallbackInfoReturnable<BlockState> cir
    ) {
        ItemStack tool = player.getMainHandItem();
        if (expallidus$isTreeLog(state)) {
            expallidus$dropSticks(level, pos, player, tool);
        }
        if (!player.getAbilities().instabuild) {
            expallidus$dropNugget(level, pos, state);
        }
        if (state.is(Blocks.TRIAL_SPAWNER)) {
            expallidus$dropTrialSpawner(level, pos, player, tool);
        }
    }

    @Unique
    private static void expallidus$dropSticks(Level level, BlockPos pos, Player player, ItemStack tool) {
        if (player.getAbilities().instabuild) {
            return;
        }
        int fortune = EnchantmentUtils.getEnchantmentLevel(level, Enchantments.FORTUNE, tool);
        if (fortune < 1 || fortune > 3) {
            return;
        }
        boolean bonus = level.getRandom().nextFloat() < 0.5F;
        Block.popResource(level, pos, new ItemStack(Items.STICK, fortune + (bonus ? 1 : 0)));
    }

    @Unique
    private static void expallidus$dropNugget(Level level, BlockPos pos, BlockState state) {
        if (state.is(Blocks.STONE)) {
            if (level.getRandom().nextFloat() < 0.125F) {
                Block.popResource(level, pos, ItemUtils.createStoneNugget());
            }
        } else if (state.is(Blocks.DEEPSLATE_GOLD_ORE) || state.is(Blocks.GOLD_ORE)) {
            if (level.getRandom().nextFloat() < 0.25F) {
                Block.popResource(level, pos, new ItemStack(Items.GOLD_NUGGET));
            }
        } else if (state.is(Blocks.DEEPSLATE_IRON_ORE) || state.is(Blocks.IRON_ORE)) {
            if (level.getRandom().nextFloat() < 0.25F) {
                Block.popResource(level, pos, new ItemStack(Items.IRON_NUGGET));
            }
        } else if (state.is(Blocks.DEEPSLATE_COPPER_ORE) || state.is(Blocks.COPPER_ORE)) {
            if (level.getRandom().nextFloat() < 0.5F) {
                Block.popResource(level, pos, new ItemStack(Items.COPPER_NUGGET));
            }
        }
    }

    @Unique
    private static void expallidus$dropTrialSpawner(Level level, BlockPos pos, Player player, ItemStack tool) {
        if (!(player instanceof ServerPlayer serverPlayer) || serverPlayer.gameMode.getGameModeForPlayer() != GameType.SURVIVAL) {
            return;
        }
        if (!(tool.is(Items.DIAMOND_PICKAXE) || tool.is(Items.NETHERITE_PICKAXE))) {
            return;
        }
        if (!EnchantmentUtils.hasEnchantment(level, Enchantments.SILK_TOUCH, tool)) {
            return;
        }
        Block.popResource(level, pos, new ItemStack(Items.TRIAL_SPAWNER));
    }

    @Unique
    private static boolean expallidus$isTreeLog(BlockState state) {
        return state.is(Blocks.OAK_LOG)
            || state.is(Blocks.SPRUCE_LOG)
            || state.is(Blocks.BIRCH_LOG)
            || state.is(Blocks.JUNGLE_LOG)
            || state.is(Blocks.ACACIA_LOG)
            || state.is(Blocks.DARK_OAK_LOG)
            || state.is(Blocks.MANGROVE_LOG)
            || state.is(Blocks.CHERRY_LOG);
    }
}
