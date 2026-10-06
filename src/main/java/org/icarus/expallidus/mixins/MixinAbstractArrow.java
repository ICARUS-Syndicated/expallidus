package org.icarus.expallidus.mixins;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import org.icarus.expallidus.utils.EnchantmentUtils;
import org.icarus.expallidus.utils.LoyaltyUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class MixinAbstractArrow {

    @Unique
    private int expallidus$loyaltyLevel;

    @Unique
    private int expallidus$timer;

    @Unique
    private int expallidus$attempts;

    @Unique
    private boolean expallidus$initialized;

    @Inject(method = "tick", at = @At("HEAD"))
    private void expallidus$trackLoyaltyTarget(CallbackInfo ci) {
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (!this.expallidus$initialized) {
            this.expallidus$initialized = true;
            if (self.tickCount == 0) {
                this.expallidus$loyaltyLevel = expallidus$getLoyaltyLevel(self);
                this.expallidus$timer = 2;
            }
        }
        if (this.expallidus$loyaltyLevel <= 0 || !(self.level() instanceof ServerLevel level)) {
            return;
        }
        if (!self.isAlive()) {
            this.expallidus$loyaltyLevel = 0;
            return;
        }
        if (--this.expallidus$timer > 0) {
            return;
        }
        this.expallidus$timer = 2;
        boolean trident = self instanceof ThrownTrident;
        if (trident) {
            expallidus$spawnParticles(level, self, 0.2);
        }
        if (++this.expallidus$attempts > 30) {
            this.expallidus$loyaltyLevel = 0;
            return;
        }
        if (!(self.getOwner() instanceof Player shooter)) {
            return;
        }
        Entity target = LoyaltyUtils.findTarget(level, self, shooter, this.expallidus$loyaltyLevel);
        if (target == null) {
            return;
        }
        LoyaltyUtils.redirect(self, target, this.expallidus$loyaltyLevel, trident ? 0.0 : 0.75);
        if (!trident) {
            expallidus$spawnParticles(level, self, 0.0);
        }
    }

    @Unique
    private static int expallidus$getLoyaltyLevel(AbstractArrow self) {
        if (!(self.getOwner() instanceof Player)) {
            return 0;
        }
        ItemStack weapon = self.getWeaponItem();
        if (weapon == null || weapon.isEmpty()) {
            return 0;
        }
        if (!weapon.is(Items.BOW) && !weapon.is(Items.TRIDENT)) {
            return 0;
        }
        return EnchantmentUtils.getEnchantmentLevel(self.level(), Enchantments.LOYALTY, weapon);
    }

    @Unique
    private static void expallidus$spawnParticles(ServerLevel level, AbstractArrow self, double spread) {
        level.sendParticles(ParticleTypes.END_ROD, self.getX(), self.getY(), self.getZ(), 6, spread, spread, spread, 0.01);
    }
}
