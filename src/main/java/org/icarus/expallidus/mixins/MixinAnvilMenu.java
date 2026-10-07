package org.icarus.expallidus.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class MixinAnvilMenu {

    @Unique
    private static final int EXPALLIDUS$MAX_REPAIR_COST = 63;

    @ModifyExpressionValue(
        method = "createResult",
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/inventory/AnvilMenu;maximumRepairCost:I", opcode = Opcodes.GETFIELD)
    )
    private int expallidus$raiseMaxRepairCost(int original) {
        AnvilMenu self = (AnvilMenu) (Object) this;
        return self.cost.get() > 40 ? Integer.MAX_VALUE : original;
    }

    @ModifyExpressionValue(
        method = "createResult",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AnvilMenu;calculateIncreasedRepairCost(I)I")
    )
    private int expallidus$clampNewRepairCost(int original) {
        return Math.min(original, EXPALLIDUS$MAX_REPAIR_COST);
    }

    @Inject(method = "createResult", at = @At("RETURN"))
    private void expallidus$truncateRepairCost(CallbackInfo ci) {
        AnvilMenu self = (AnvilMenu) (Object) this;
        for (int slot = 0; slot < 2; slot++) {
            ItemStack stack = self.getSlot(slot).getItem();
            Integer repairCost = stack.get(DataComponents.REPAIR_COST);
            if (repairCost != null && repairCost > EXPALLIDUS$MAX_REPAIR_COST) {
                stack.set(DataComponents.REPAIR_COST, EXPALLIDUS$MAX_REPAIR_COST);
                self.getSlot(slot).setChanged();
            }
        }
        if (self.cost.get() > 40) {
            self.maximumRepairCost = Integer.MAX_VALUE;
            self.cost.set(39);
        }
    }
}
