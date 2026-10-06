package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBedBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import static org.icarus.expallidus.utils.ParticleUtils.addParticlesAroundBlock;

@Mixin(FlowerBedBlock.class)
public abstract class MixinFlowerBedBlock extends VegetationBlock {
    protected MixinFlowerBedBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hitResult) {
        boolean petals = state.is(Blocks.PINK_PETALS);
        if (!petals && !state.is(Blocks.WILDFLOWERS)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        if (stack.is(state.getBlock().asItem())) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        player.swing(hand);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        int amount = state.getValue(FlowerBedBlock.AMOUNT);
        if (amount > 1) {
            level.setBlockAndUpdate(pos, state.setValue(FlowerBedBlock.AMOUNT, amount - 1));
        } else {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
        level.playSound(null, pos, SoundEvents.GRASS_BREAK, SoundSource.BLOCKS, 0.5F, 1.0F);
        addParticlesAroundBlock(level, pos, new BlockParticleOption(ParticleTypes.BLOCK, state), 6, 0.2, 0.01);
        Item flower = expallidus$pick(player.getRandom(), petals);
        int dropAmount = 1 + player.getRandom().nextInt(2);
        for (int i = 0; i < dropAmount; i++) {
            Block.popResource(level, pos, new ItemStack(flower));
        }
        return InteractionResult.SUCCESS;
    }

    private static Item expallidus$pick(RandomSource random, boolean petals) {
        if (petals) {
            if (random.nextFloat() < 0.7F) {
                return Items.CHERRY_LEAVES;
            }
            return random.nextFloat() < 0.5F ? Items.PINK_TULIP : Items.CHERRY_SAPLING;
        }
        if (random.nextFloat() < 0.6F) {
            return switch (random.nextInt(5)) {
                case 0 -> Items.LILY_OF_THE_VALLEY;
                case 1 -> Items.WHITE_TULIP;
                case 2 -> Items.OXEYE_DAISY;
                case 3 -> Items.DANDELION;
                default -> Items.AZURE_BLUET;
            };
        }
        if (random.nextFloat() < 0.5F) {
            return switch (random.nextInt(4)) {
                case 0 -> Items.CORNFLOWER;
                case 1 -> Items.BLUE_ORCHID;
                case 2 -> Items.ALLIUM;
                default -> Items.PINK_TULIP;
            };
        }
        return switch (random.nextInt(3)) {
            case 0 -> Items.RED_TULIP;
            case 1 -> Items.SUNFLOWER;
            default -> Items.POPPY;
        };
    }
}
