package org.icarus.expallidus.mixins;

import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Axolotl.class)
public abstract class MixinAxolotl {

    @Inject(method = "isFood", at = @At("HEAD"), cancellable = true)
    private void expallidus$isFood(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(Items.TROPICAL_FISH)) {
            cir.setReturnValue(true);
        }
    }
}
