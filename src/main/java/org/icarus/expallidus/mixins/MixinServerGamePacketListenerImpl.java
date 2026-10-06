package org.icarus.expallidus.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import org.icarus.expallidus.utils.EnchantmentUtils;
import org.icarus.expallidus.utils.LoyaltyUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerGamePacketListenerImpl {

    @Inject(method = "handlePlayerAction", at = @At("HEAD"), cancellable = true)
    private void expallidus$togglePlayerTargeting(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
        if (packet.getAction() != ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND) {
            return;
        }
        ServerPlayer player = ((ServerGamePacketListenerImpl) (Object) this).player;
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!stack.is(Items.BOW) && !stack.is(Items.TRIDENT)) {
            return;
        }
        if (!EnchantmentUtils.hasEnchantment(player.level(), Enchantments.LOYALTY, stack)) {
            return;
        }
        ci.cancel();
        boolean enabled = LoyaltyUtils.togglePlayerTargeting(player);
        player.displayClientMessage(
            Component.literal(enabled ? "友军锁定已开启！" : "友军锁定已关闭！"),
            true
        );
    }
}
