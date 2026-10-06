package org.icarus.expallidus.mixins;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class MixinAbstractContainerMenu {

    @Inject(method = "clicked", at = @At("HEAD"))
    private void expallidus$clampRepairCost(
        int slotIndex,
        int button,
        ClickType clickType,
        Player player,
        CallbackInfo ci
    ) {
        if (!((Object) this instanceof AnvilMenu)) {
            return;
        }
        AbstractContainerMenu self = (AbstractContainerMenu) (Object) this;
        if (slotIndex < 0 || slotIndex > 2 || slotIndex >= self.slots.size()) {
            return;
        }
        Slot slot = self.getSlot(slotIndex);
        ItemStack stack = slot.getItem();
        Integer repairCost = stack.get(DataComponents.REPAIR_COST);
        if (repairCost != null && repairCost > 63) {
            stack.set(DataComponents.REPAIR_COST, 63);
            slot.setChanged();
        }
    }
}
