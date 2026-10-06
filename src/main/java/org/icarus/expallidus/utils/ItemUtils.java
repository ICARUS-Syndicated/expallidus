package org.icarus.expallidus.utils;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;

public final class ItemUtils {

    public static final String STONE_NUGGET_KEY = "custom_registery";
    public static final String STONE_NUGGET_VALUE = "stone_nugget";
    public static final String BROKEN_KEY = "isBroken";

    private ItemUtils() {
    }

    public static String getCustomString(ItemStack stack, String key) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        return customData == null ? "" : customData.copyTag().getStringOr(key, "");
    }

    public static void setCustomString(ItemStack stack, String key, String value) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString(key, value));
    }

    public static boolean isStoneNugget(ItemStack stack) {
        return stack.is(Items.STONE_BUTTON)
            && getCustomString(stack, STONE_NUGGET_KEY).equals(STONE_NUGGET_VALUE);
    }

    public static ItemStack createStoneNugget() {
        ItemStack stack = new ItemStack(Items.STONE_BUTTON);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal("石粒").withStyle(ChatFormatting.WHITE));
        stack.set(
            DataComponents.LORE,
            new ItemLore(List.of(Component.literal("！？强强？！").withStyle(ChatFormatting.GRAY)))
        );
        setCustomString(stack, STONE_NUGGET_KEY, STONE_NUGGET_VALUE);
        return stack;
    }

    public static boolean isBroken(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        return customData != null && customData.copyTag().contains(BROKEN_KEY);
    }

    public static void setBroken(ItemStack stack, boolean broken) {
        if (broken) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putByte(BROKEN_KEY, (byte) 1));
        } else {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.remove(BROKEN_KEY));
        }
    }

    public static void setBrokenLore(ItemStack stack) {
        stack.set(
            DataComponents.LORE,
            new ItemLore(List.of(Component.literal("已损坏。").withStyle(ChatFormatting.RED)))
        );
    }

    public static void playBrokenFeedback(ServerPlayer player, String message) {
        player.level().playSound(
            null,
            player.getX(),
            player.getY(),
            player.getZ(),
            SoundEvents.VILLAGER_NO,
            SoundSource.PLAYERS,
            0.5F,
            1.0F
        );
        player.displayClientMessage(Component.literal(message).withStyle(ChatFormatting.RED), true);
    }
}
