package org.icarus.expallidus.mixins;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.SkeletonHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.icarus.expallidus.utils.ParticleUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkeletonHorse.class)
public abstract class MixinSkeletonHorse {

    @Unique
    private static final int EXPALLIDUS_HORSE_CONVERSION_TICKS = 1000;

    @Unique
    private static final int EXPALLIDUS_HORSE_SLOWNESS_TICKS = 4000;

    @Unique
    private int expallidus$conversionTicks;

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void expallidus$tickConversion(CallbackInfo ci) {
        if (this.expallidus$conversionTicks <= 0) {
            return;
        }
        SkeletonHorse self = (SkeletonHorse) (Object) this;
        if (!self.isAlive() || !(self.level() instanceof ServerLevel level)) {
            this.expallidus$conversionTicks = 0;
            return;
        }
        if (this.expallidus$conversionTicks % 5 == 0) {
            ParticleUtils.addParticlesAroundEntity(self, ParticleTypes.POOF, 10, 1.0, 0.02);
        }
        this.expallidus$conversionTicks--;
        if (this.expallidus$conversionTicks <= 0) {
            expallidus$finishConversion(self, level);
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void expallidus$cureSkeletonHorse(
        Player player,
        InteractionHand hand,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        SkeletonHorse self = (SkeletonHorse) (Object) this;
        if (this.expallidus$conversionTicks > 0) {
            return;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(Items.GOLDEN_CARROT) || !self.hasEffect(MobEffects.WEAKNESS)) {
            return;
        }
        if (self.level() instanceof ServerLevel level) {
            stack.consume(1, player);
            self.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, EXPALLIDUS_HORSE_SLOWNESS_TICKS, 126));
            level.playSound(
                null,
                self.getX(),
                self.getY(),
                self.getZ(),
                SoundEvents.ZOMBIE_VILLAGER_CURE,
                self.getSoundSource(),
                1.0F,
                1.0F
            );
            this.expallidus$conversionTicks = EXPALLIDUS_HORSE_CONVERSION_TICKS;
        }
        cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
    }

    @Unique
    private static void expallidus$finishConversion(SkeletonHorse self, ServerLevel level) {
        double speed = self.getAttributeValue(Attributes.MOVEMENT_SPEED);
        double followRange = self.getAttributeValue(Attributes.FOLLOW_RANGE);
        double jumpStrength = self.getAttributeValue(Attributes.JUMP_STRENGTH);
        double maxHealth = self.getAttributeValue(Attributes.MAX_HEALTH);
        self.convertTo(EntityType.HORSE, ConversionParams.single(self, true, true), horse -> {
            horse.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
            horse.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(followRange);
            horse.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(jumpStrength);
            horse.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
            if (horse.getHealth() > maxHealth) {
                horse.setHealth((float) maxHealth);
            }
            horse.removeEffect(MobEffects.SLOWNESS);
            horse.setTamed(true);
            ParticleUtils.addParticlesAroundEntity(horse, ParticleTypes.HEART, 20, 1.0, 0.02);
        });
    }
}
