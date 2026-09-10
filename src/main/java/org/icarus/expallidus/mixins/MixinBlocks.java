package org.icarus.expallidus.mixins;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.icarus.expallidus.expansion.ExpallidusMossyCobblestone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Function;

@Mixin(Blocks.class)
public abstract class MixinBlocks {
    @Shadow
    private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    public static final Block MOSSY_COBBLESTONE = register(
        "mossy_cobblestone",
        ExpallidusMossyCobblestone::new,
        BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F)
    );
}
