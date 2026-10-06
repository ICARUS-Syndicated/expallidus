package org.icarus.expallidus.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ServerLevelAccessor;
import org.icarus.expallidus.utils.EnchantmentUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MixinMob {

    @Inject(method = "dropCustomDeathLoot", at = @At("TAIL"))
    private void expallidus$extraDrops(
        ServerLevel level,
        DamageSource damageSource,
        boolean recentlyHit,
        CallbackInfo ci
    ) {
        Mob self = (Mob) (Object) this;
        EntityType<?> type = self.getType();
        Player killer = damageSource.getEntity() instanceof Player player ? player : null;
        int looting = killer == null ? 0 : EnchantmentUtils.getEnchantmentLevel(level, Enchantments.LOOTING, killer.getMainHandItem());
        if (killer != null && type == EntityType.BLAZE) {
            expallidus$dropExtra(level, self, Items.BLAZE_ROD, looting);
        } else if (killer != null && type == EntityType.SPIDER) {
            expallidus$dropExtra(level, self, Items.SPIDER_EYE, looting);
        } else if (type == EntityType.WITHER_SKELETON) {
            if (self.getRandom().nextFloat() * 100.0F < 5 + looting) {
                expallidus$drop(level, self, Items.WITHER_SKELETON_SKULL, 1);
            }
        } else if (killer != null && type == EntityType.BREEZE) {
            expallidus$drop(level, self, Items.BREEZE_ROD, looting + 1 + self.getRandom().nextInt(looting + 2));
        } else if (type == EntityType.GIANT) {
            expallidus$drop(level, self, Items.ROTTEN_FLESH, 16 + self.getRandom().nextInt(17));
            if (self.getRandom().nextFloat() < 0.5F) {
                expallidus$drop(level, self, Items.ENCHANTED_GOLDEN_APPLE, 1);
            }
        }
    }

    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    private void expallidus$giantInsteadOfZombie(
        ServerLevelAccessor level,
        DifficultyInstance difficulty,
        EntitySpawnReason spawnReason,
        SpawnGroupData spawnGroupData,
        CallbackInfo ci
    ) {
        Mob self = (Mob) (Object) this;
        if (self.getType() != EntityType.ZOMBIE || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (self.getRandom().nextFloat() >= 0.02F) {
            return;
        }
        if (EntityType.GIANT.spawn(serverLevel, self.blockPosition(), EntitySpawnReason.NATURAL) == null) {
            return;
        }
        self.discard();
    }

    @Unique
    private static void expallidus$dropExtra(ServerLevel level, Mob mob, Item item, int looting) {
        int amount = mob.getRandom().nextInt(3);
        if (mob.getRandom().nextFloat() < looting * 0.05F) {
            amount += mob.getRandom().nextInt(looting + 1);
        }
        expallidus$drop(level, mob, item, amount);
    }

    @Unique
    private static void expallidus$drop(ServerLevel level, Mob mob, Item item, int amount) {
        for (int i = 0; i < amount; i++) {
            mob.spawnAtLocation(level, new ItemStack(item));
        }
    }
}
