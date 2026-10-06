package org.icarus.expallidus.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.warden.Warden;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Warden.class)
public abstract class MixinWarden {

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
            value = 600.0;
        } else if (attribute == Attributes.MOVEMENT_SPEED) {
            value += 0.45;
        } else if (attribute == Attributes.ATTACK_DAMAGE) {
            value += 25.0;
        } else if (attribute == Attributes.ATTACK_KNOCKBACK) {
            value += 0.8;
        }
        return original.call(builder, attribute, value);
    }

    @ModifyReturnValue(method = "createAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder expallidus$attackSpeed(AttributeSupplier.Builder builder) {
        return builder.add(Attributes.ATTACK_SPEED, 12.0);
    }
}
