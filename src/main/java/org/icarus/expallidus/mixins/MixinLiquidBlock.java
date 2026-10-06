package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.*;
import org.bukkit.craftbukkit.event.CraftEventFactory;

import static net.minecraft.world.level.block.LiquidBlock.POSSIBLE_FLOW_DIRECTIONS;

@Mixin(LiquidBlock.class)
public abstract class MixinLiquidBlock extends Block {
    public MixinLiquidBlock(Properties properties, FlowingFluid fluid) {
        super(properties);
        this.fluid = fluid;
    }

    @Final
    @Mutable
    @Shadow
    protected final FlowingFluid fluid;

    @Shadow
    private void fizz(LevelAccessor level, BlockPos pos) {
        throw new UnsupportedOperationException("Implemented via Mixin");
    }

    /**
     * @author none
     * @reason none
     */
    @Overwrite
    private boolean shouldSpreadLiquid(Level level, BlockPos pos, BlockState state) {
        if (this.fluid.is(FluidTags.LAVA)) {
            boolean isSoulSoil = level.getBlockState(pos.below()).is(Blocks.SOUL_SOIL);

            for (Direction direction : POSSIBLE_FLOW_DIRECTIONS) {
                BlockPos blockPos = pos.relative(direction.getOpposite());
                if (level.getFluidState(blockPos).is(FluidTags.WATER)) {
                    Block block = level.getFluidState(pos).isSource() ?
                        (level.getRandom().nextDouble() <= 0.125F ? Blocks.CRYING_OBSIDIAN : Blocks.OBSIDIAN) :
                        expallidus$cobblestonePicker(level, pos);
                    // CraftBukkit start
                    if (CraftEventFactory.handleBlockFormEvent(level,
                        pos,
                        block.defaultBlockState(),
                        Block.UPDATE_ALL)) {
                        this.fizz(level, pos);
                    }
                    // CraftBukkit end
                    return false;
                }

                if (isSoulSoil && level.getBlockState(blockPos).is(Blocks.BLUE_ICE)) {
                    // CraftBukkit start
                    if (CraftEventFactory.handleBlockFormEvent(level,
                        pos,
                        expallidus$basaltPicker(level).defaultBlockState(),
                        Block.UPDATE_ALL)) {
                        this.fizz(level, pos);
                    }
                    // CraftBukkit end
                    return false;
                }
            }
        }
        return true;
    }

    @Unique
    private Block expallidus$cobblestonePicker(Level level, BlockPos pos) {
        Block[] endSelectableCobbles = {Blocks.PURPUR_BLOCK, Blocks.END_STONE};
        Block[] selectableCobbles = {Blocks.ANDESITE, Blocks.DIORITE, Blocks.CALCITE, Blocks.TUFF, Blocks.GRANITE};
        RandomSource random = level.getRandom();
        if (level.dimension().equals(Level.END)) {
            int index = random.nextInt(2);
            return endSelectableCobbles[index];
        }

        if (random.nextDouble() >= 0.5) {
            if(pos.getY() <= 0 ) return Blocks.DEEPSLATE;
            return Blocks.COBBLESTONE;
        }

        if (random.nextDouble() <= 0.98) {
            int index = random.nextInt(5);
            return selectableCobbles[index];
        } else {
            return Blocks.IRON_ORE;
        }
    }

    @Unique
    private Block expallidus$basaltPicker(Level level) {
        Block[] selectableBasalts = {Blocks.ANDESITE, Blocks.DIORITE, Blocks.CALCITE, Blocks.BLACKSTONE, Blocks.GRANITE};
        RandomSource random = level.getRandom();
        if (random.nextDouble() >= 0.50) {
            return Blocks.BASALT;
        }

        if (random.nextDouble() <= 0.80) {
            int index = random.nextInt(5);
            return selectableBasalts[index];
        } else {
            return Blocks.QUARTZ_BLOCK;
        }
    }
}
