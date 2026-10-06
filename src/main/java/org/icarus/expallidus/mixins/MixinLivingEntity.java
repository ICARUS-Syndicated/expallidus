package org.icarus.expallidus.mixins;

import java.util.Map;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.icarus.expallidus.utils.EnchantmentUtils;
import org.icarus.expallidus.utils.ItemUtils;
import org.icarus.expallidus.utils.ParticleUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Inject(method = "dropCustomDeathLoot", at = @At("TAIL"))
    private void expallidus$swiftSneakEchoShard(
        ServerLevel level,
        DamageSource damageSource,
        boolean recentlyHit,
        CallbackInfo ci
    ) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Monster) && !(self instanceof Animal)) {
            return;
        }
        if (!(damageSource.getEntity() instanceof Player player)) {
            return;
        }
        ItemStack tool = player.getMainHandItem();
        int swiftSneak = EnchantmentUtils.getEnchantmentLevel(level, Enchantments.SWIFT_SNEAK, tool);
        if (swiftSneak < 1) {
            return;
        }
        ParticleUtils.addParticlesAroundEntity(self, ParticleTypes.SCULK_CHARGE_POP, 12, 0.5, 0.02);
        int shardLevel = swiftSneak + 1;
        int amount = shardLevel + self.getRandom().nextInt(shardLevel + 1);
        for (int i = 0; i < amount; i++) {
            self.spawnAtLocation(level, new ItemStack(Items.ECHO_SHARD));
        }
        if (self.getRandom().nextFloat() < 0.67F) {
            tool.hurtAndBreak(13, player, EquipmentSlot.MAINHAND);
        }
    }

    @Inject(method = "handleEquipmentChanges", at = @At("HEAD"))
    private void expallidus$dropBrokenArmor(Map<EquipmentSlot, ItemStack> equipment, CallbackInfo ci) {
        if (!((Object) this instanceof ServerPlayer player)) {
            return;
        }
        for (EquipmentSlot slot : EquipmentSlot.VALUES) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
                continue;
            }
            ItemStack armor = equipment.get(slot);
            if (armor == null || armor.isEmpty() || !ItemUtils.isBroken(armor)) {
                continue;
            }
            equipment.put(slot, ItemStack.EMPTY);
            player.setItemSlot(slot, ItemStack.EMPTY);
            armor.forEachModifier(slot, (attribute, modifier) -> {
                AttributeInstance instance = player.getAttributes().getInstance(attribute);
                if (instance != null) {
                    instance.removeModifier(modifier.id());
                }
            });
            player.spawnAtLocation(player.level(), armor);
            ItemUtils.playBrokenFeedback(player, "该装备已损坏。");
        }
    }

    @Inject(method = "handleEquipmentChanges", at = @At("TAIL"))
    private void expallidus$swiftSneakSpeed(Map<EquipmentSlot, ItemStack> equipment, CallbackInfo ci) {
        if (!((Object) this instanceof ServerPlayer player)) {
            return;
        }
        if (!equipment.containsKey(EquipmentSlot.FEET)) {
            return;
        }
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        double walkSpeed = 0.2;
        if (boots.is(ItemTags.FOOT_ARMOR)) {
            int swiftSneak = EnchantmentUtils.getEnchantmentLevel(player.level(), Enchantments.SWIFT_SNEAK, boots);
            if (swiftSneak > 0) {
                int sneakLevel = Math.clamp(swiftSneak + 1, 1, 10);
                walkSpeed += 0.8 * (sneakLevel - 1) / 9.0;
            }
        }
        player.getAbilities().walkingSpeed = (float) (walkSpeed / 2.0);
        player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(walkSpeed / 2.0);
        player.onUpdateAbilities();
    }
}
