package org.icarus.expallidus.mixins;

import java.util.function.Consumer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.icarus.expallidus.utils.ItemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Inject(method = "applyDamage", at = @At("HEAD"))
    private void expallidus$dropBrokenItem(int damage, LivingEntity player, Consumer<Item> onBreak, CallbackInfo ci) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        ItemStack self = (ItemStack) (Object) this;
        if (!self.isDamageableItem() || self.getCount() != 1 || !self.isEnchanted()) {
            return;
        }
        if (damage < self.getMaxDamage()) {
            return;
        }
        ItemStack broken = self.copy();
        broken.setDamageValue(broken.getMaxDamage() - 1);
        ItemUtils.setBroken(broken, true);
        ItemUtils.setBrokenLore(broken);
        serverPlayer.spawnAtLocation(serverPlayer.level(), broken);
    }
}
