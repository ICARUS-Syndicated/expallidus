package org.icarus.expallidus.utils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public final class EnchantmentUtils {

    private EnchantmentUtils() {
    }

    public static int getEnchantmentLevel(Level level, ResourceKey<Enchantment> enchantment, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        return EnchantmentHelper.getItemEnchantmentLevel(
            level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment),
            stack
        );
    }

    public static boolean hasEnchantment(Level level, ResourceKey<Enchantment> enchantment, ItemStack stack) {
        return getEnchantmentLevel(level, enchantment, stack) > 0;
    }
}
