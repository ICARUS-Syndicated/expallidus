package org.icarus.expallidus.mixins;

import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.icarus.expallidus.utils.ItemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileWeaponItem.class)
public abstract class MixinProjectileWeaponItem {

    @Inject(method = "shoot", at = @At("HEAD"), cancellable = true)
    private void expallidus$preventBrokenWeaponShoot(
        ServerLevel level,
        LivingEntity shooter,
        InteractionHand hand,
        ItemStack weapon,
        List<ItemStack> projectileItems,
        float velocity,
        float inaccuracy,
        boolean isCrit,
        LivingEntity target,
        float drawStrength,
        CallbackInfo ci
    ) {
        if (weapon.isEmpty()) {
            return;
        }
        if (!ItemUtils.isBroken(weapon)) {
            return;
        }
        ci.cancel();
        if (shooter instanceof ServerPlayer serverPlayer) {
            ItemUtils.playBrokenFeedback(serverPlayer, "该工具已损坏。");
        }
    }
}
