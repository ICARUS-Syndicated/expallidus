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
import net.minecraft.world.entity.Mob;
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

@Mixin(Mob.class)
public abstract class MixinMobConversion {

    @Unique
    private static final int EXPALLIDUS_PIGLIN_CONVERSION_TICKS = 100;

    @Unique
    private static final int EXPALLIDUS_PIGLIN_SLOWNESS_TICKS = 400;

    @Unique
    private int expallidus$conversionTicks;

    @Inject(method = "tick", at = @At("HEAD"))
    private void expallidus$tickConversion(CallbackInfo ci) {
        if (this.expallidus$conversionTicks <= 0) {
            return;
        }
        Mob self = (Mob) (Object) this;
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
    private void expallidus$cureZombifiedPiglin(
        Player player,
        InteractionHand hand,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        Mob self = (Mob) (Object) this;
        if (self.getType() != EntityType.ZOMBIFIED_PIGLIN || this.expallidus$conversionTicks > 0) {
            return;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(Items.GOLDEN_APPLE) || !self.hasEffect(MobEffects.WEAKNESS)) {
            return;
        }
        if (self.level() instanceof ServerLevel level) {
            stack.consume(1, player);
            self.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, EXPALLIDUS_PIGLIN_SLOWNESS_TICKS, 126));
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
            this.expallidus$conversionTicks = EXPALLIDUS_PIGLIN_CONVERSION_TICKS;
        }
        cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
    }

    @Unique
    private static void expallidus$finishConversion(Mob self, ServerLevel level) {
        if (self.getType() != EntityType.ZOMBIFIED_PIGLIN) {
            return;
        }
        self.convertTo(EntityType.PIGLIN, ConversionParams.single(self, true, false), piglin -> {
            piglin.removeEffect(MobEffects.SLOWNESS);
            ParticleUtils.addParticlesAroundEntity(piglin, ParticleTypes.GLOW, 25, 1.0, 0.02);
        });
    }
}
