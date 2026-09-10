package org.icarus.expallidus.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import static org.icarus.expallidus.utils.ParticleUtils.addParticlesAroundEntity;

@Mixin(Horse.class)
public class MixinHorse extends AbstractHorse {
    protected MixinHorse(EntityType<? extends AbstractHorse> type, Level level) {
        super(type, level);
    }

    @WrapMethod(method = "mobInteract")
    public InteractionResult mobInteract(Player player,
                                         InteractionHand hand,
                                         Operation<InteractionResult> original) {
        if (player.getItemInHand(hand).getItem() == Items.SUGAR) {
            this.addEffect(new MobEffectInstance(MobEffects.SPEED, 45, 2));
            addParticlesAroundEntity(this, ParticleTypes.WHITE_SMOKE, 10);
            return InteractionResult.SUCCESS;
        }
        original.call(player, hand);
        return null;
    }
}
