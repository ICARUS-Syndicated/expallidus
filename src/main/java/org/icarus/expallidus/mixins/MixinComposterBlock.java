package org.icarus.expallidus.mixins;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ComposterBlock.class)
public abstract class MixinComposterBlock extends Block {
    @Shadow
    private static void add(float chance, ItemLike item) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    public MixinComposterBlock(Properties properties) {
        super(properties);
    }

    @Inject(method = "bootStrap", at = @At("TAIL"))
    private static void expallidus$bootStrap(CallbackInfo ci) {
        add(1.0F, Items.ROTTEN_FLESH);
        add(1.0F, Items.SPIDER_EYE);
    }
}
