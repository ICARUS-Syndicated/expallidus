package org.icarus.expallidus.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import org.icarus.expallidus.utils.EnchantmentUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Sheep.class)
public abstract class MixinSheep {

    @WrapOperation(
        method = "mobInteract",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/sheep/Sheep;generateDefaultDrops(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"
        )
    )
    private List<ItemStack> expallidus$extraWool(ServerLevel level,
                                                 ItemStack shears,
                                                 Operation<List<ItemStack>> original) {
        List<ItemStack> drops = original.call(level, shears);
        if (drops.isEmpty()) {
            return drops;
        }
        Sheep self = (Sheep) (Object) this;
        ItemStack wool = drops.getFirst();
        int looting = EnchantmentUtils.getEnchantmentLevel(level, Enchantments.LOOTING, shears);
        int amount = self.getRandom().nextInt(3);
        if (self.getRandom().nextFloat() < looting * 0.05F) {
            amount += self.getRandom().nextInt(looting + 1);
        }
        for (int i = 0; i < amount; i++) {
            drops.add(wool.copy());
        }
        return drops;
    }
}
