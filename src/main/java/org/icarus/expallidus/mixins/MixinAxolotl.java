package org.icarus.expallidus.mixins;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Axolotl.class)
public abstract class MixinAxolotl extends Animal {
    protected MixinAxolotl(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    @Inject(method = "isFood", at = @At("HEAD"), cancellable = true)
    private void isFood(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        // NSV Start - make tropical fish feedable to axolotls
        if (stack.is(Items.TROPICAL_FISH)) cir.setReturnValue(true);
    }
}
