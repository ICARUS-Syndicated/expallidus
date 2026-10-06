package org.icarus.expallidus.mixins;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.icarus.expallidus.expansion.ExpallidusMossyCobblestone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(Blocks.class)
public abstract class MixinBlocks {

    @Inject(method = "register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", at = @At("HEAD"), cancellable = true)
    private static void expallidus$replaceMossyCobblestone(
        String name,
        Function<BlockBehaviour.Properties, Block> factory,
        BlockBehaviour.Properties properties,
        CallbackInfoReturnable<Block> cir
    ) {
        if (!"mossy_cobblestone".equals(name)) return;
        ResourceKey<Block> blockKey = ResourceKey.create(
            Registries.BLOCK,
            Identifier.withDefaultNamespace(name)
        );
        properties.setId(blockKey);

        Block block = new ExpallidusMossyCobblestone(properties);
        Block registered = Registry.register(
            BuiltInRegistries.BLOCK,
            name,
            block
        );
        cir.setReturnValue(registered);
    }
}
