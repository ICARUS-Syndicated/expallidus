package org.icarus.expallidus.mixins;

import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HoeItem.class)
public abstract class MixinHoeItem {

    @Shadow
    @Final
    protected static Map<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> TILLABLES;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void expallidus$addSoulSoil(CallbackInfo ci) {
        TILLABLES.put(
            Blocks.SOUL_SOIL,
            Pair.of(HoeItem::onlyIfAirAbove, HoeItem.changeIntoState(Blocks.SOUL_SAND.defaultBlockState()))
        );
    }
}
