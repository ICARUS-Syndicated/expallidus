package org.icarus.expallidus.mixins;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.icarus.expallidus.utils.ParticleUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractPiglin.class)
public abstract class MixinAbstractPiglin {

    @Unique
    private int expallidus$cureTicks;

    @Inject(method = "customServerAiStep", at = @At("HEAD"))
    private void expallidus$tickCure(ServerLevel level, CallbackInfo ci) {
        if (this.expallidus$cureTicks <= 0) {
            return;
        }

        AbstractPiglin self = (AbstractPiglin) (Object) this;
        if (!self.isAlive()) {
            this.expallidus$cureTicks = 0;
            return;
        }

        if (this.expallidus$cureTicks % 5 == 0) {
            ParticleUtils.addParticlesAroundEntity(self, ParticleTypes.POOF, 10, 1.0, 0.02);
        }

        if (--this.expallidus$cureTicks <= 0) {
            self.removeEffect(MobEffects.SLOWNESS);
            ParticleUtils.addParticlesAroundEntity(self, ParticleTypes.GLOW, 25, 1.0, 0.02);
        }
    }

    @Inject(method = "finishConversion", at = @At("HEAD"), cancellable = true)
    private void expallidus$goldenAppleCure(ServerLevel level, CallbackInfo ci) {
        AbstractPiglin self = (AbstractPiglin) (Object) this;
        if (this.expallidus$cureTicks > 0) {
            self.setTimeInOverworld(0);
            ci.cancel();
            return;
        }

        ItemStack tool = self.getMainHandItem();
        if (!tool.is(Items.GOLDEN_APPLE)) {
            return;
        }

        self.setTimeInOverworld(0);
        ci.cancel();
        tool.consume(1, self);
        self.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 400, 127));
        level.playSound(null,
            self.getX(),
            self.getY(),
            self.getZ(),
            SoundEvents.ZOMBIE_VILLAGER_CURE,
            self.getSoundSource(),
            1.0F,
            1.0F);
        this.expallidus$cureTicks = 100;
    }
}
