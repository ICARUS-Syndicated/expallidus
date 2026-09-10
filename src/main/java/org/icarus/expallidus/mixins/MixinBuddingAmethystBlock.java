package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AmethystBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;


@Mixin(BuddingAmethystBlock.class)
public abstract class MixinBuddingAmethystBlock extends AmethystBlock {
    public MixinBuddingAmethystBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BlockState playerWillDestroy(@NotNull Level level,
                                                 @NotNull BlockPos pos,
                                                 @NotNull BlockState state,
                                                 Player player) {
        // NSV Start - budding amethyst explosion
        if (!player.getAbilities().instabuild) {
            if (expallidus$hasSilkTouch(level, player.getMainHandItem())) {
                Block.popResource(level, pos, new ItemStack(Blocks.BUDDING_AMETHYST));
            } else {
                Vec3 center = Vec3.atCenterOf(pos);
                level.explode(player, center.x, center.y, center.z, 3.0F, Level.ExplosionInteraction.BLOCK);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Unique
    private static boolean expallidus$hasSilkTouch(Level level, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }
        Holder<Enchantment> silkTouch = level.registryAccess()
            .lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.SILK_TOUCH);
        return EnchantmentHelper.getItemEnchantmentLevel(silkTouch, itemStack) > 0;
    }
}
