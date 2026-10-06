package org.icarus.expallidus.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class MixinAnvilMenu {

    @ModifyExpressionValue(
        method = "createResult",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/inventory/AnvilMenu;maximumRepairCost:I")
    )
    private int expallidus$raiseMaxRepairCost(int original) {
        AnvilMenu self = (AnvilMenu) (Object) this;
        return self.cost.get() > 40 ? Integer.MAX_VALUE : original;
    }

    @Inject(method = "createResult", at = @At("RETURN"))
    private void expallidus$decreaseRepairCost(CallbackInfo ci) {
        AnvilMenu self = (AnvilMenu) (Object) this;
        if (self.cost.get() > 40) {
            self.maximumRepairCost = Integer.MAX_VALUE;
            self.cost.set(39);
        }
    }
}
