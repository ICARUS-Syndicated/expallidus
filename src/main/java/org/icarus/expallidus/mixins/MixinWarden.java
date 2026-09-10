package org.icarus.expallidus.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;

import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Warden.class)
public abstract class MixinWarden extends Monster {
    protected MixinWarden(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @ModifyConstant(
        method = "<init>",
        constant = @Constant(intValue = 5)
    )
    private int modifyDropXp(int value) {
        return 3;
    }

    @WrapOperation(
        method = "createAttributes",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;")
    )
    private static AttributeSupplier.Builder expallidus$attributes(
        AttributeSupplier.Builder builder,
        Holder<Attribute> attribute,
        double value,
        Operation<AttributeSupplier.Builder> original
    ) {
        if (attribute == Attributes.MAX_HEALTH) {
            value = 1000.0;
        } else if (attribute == Attributes.MOVEMENT_SPEED) {
            value *= 3.0;
        } else if (attribute == Attributes.FOLLOW_RANGE) {
            value *= 1.5;
        } else if (attribute == Attributes.ATTACK_DAMAGE) {
            value += 25.0;
        } else if (attribute == Attributes.ATTACK_KNOCKBACK) {
            value *= 1.5;
        }
        return original.call(builder, attribute, value);
    }
}
