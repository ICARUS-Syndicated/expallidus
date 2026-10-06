package org.icarus.expallidus.mixins;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SpellParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.icarus.expallidus.utils.ParticleUtils.addParticlesAroundEntity;

@Mixin(Horse.class)
public class MixinHorse extends AbstractHorse {
    protected MixinHorse(EntityType<? extends AbstractHorse> type, Level level) {
        super(type, level);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"))
    private void expallidus$mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(Items.SUGAR) || !this.isTamed() || this.getHealth() != this.getMaxHealth()) {
            return;
        }

        player.swing(hand);
        this.level()
            .playSound(null,
                this.getX(),
                this.getY(),
                this.getZ(),
                SoundEvents.HORSE_EAT,
                this.getSoundSource(),
                0.5F,
                1.0F);
        if (this.level() instanceof ServerLevel) {
            addParticlesAroundEntity(this,
                SpellParticleOption.create(ParticleTypes.EFFECT, 1.0F, 1.0F, 1.0F, 0.5F),
                25,
                0.5,
                0.02);
        }
        this.addEffect(new MobEffectInstance(MobEffects.SPEED, 900, 1));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }
}
