package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrindstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;

import static org.icarus.expallidus.utils.ParticleUtils.addParticlesAroundBlock;

@Mixin(GrindstoneBlock.class)
public abstract class MixinGrindstoneBlock extends Block {
    protected MixinGrindstoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack,
                                          BlockState state,
                                          Level level,
                                          BlockPos pos,
                                          Player player,
                                          InteractionHand hand,
                                          BlockHitResult hitResult) {
        boolean cobblestone = stack.is(Items.COBBLESTONE);
        boolean gravel = stack.is(Items.GRAVEL);
        boolean granite = stack.is(Items.GRANITE);
        if (!cobblestone && !gravel && !granite) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        int amount = player.isShiftKeyDown() ? stack.getCount() : 1;
        if (cobblestone) {
            Block.popResource(level, pos, new ItemStack(Items.GRAVEL, amount));
        } else if (gravel) {
            Block.popResource(level, pos, new ItemStack(Items.SAND, amount));
            if (player.getRandom().nextFloat() < 0.5F) {
                Block.popResource(level, pos, new ItemStack(Items.SAND, amount + player.getRandom().nextInt(amount + 1)));
            }
        } else {
            for (int i = 0; i < amount; i++) {
                if (player.getRandom().nextFloat() < 0.75F) {
                    Item nugget = player.getRandom().nextBoolean() ? Items.RAW_COPPER : Items.RAW_IRON;
                    Block.popResource(level, pos, new ItemStack(nugget, 1 + player.getRandom().nextInt(3)));
                } else {
                    Block.popResource(level, pos, new ItemStack(Items.RAW_GOLD, 1 + player.getRandom().nextInt(2)));
                }
            }
        }

        level.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 0.5F, 1.0F);
        addParticlesAroundBlock(level, pos, ParticleTypes.WHITE_ASH, 20, 0.0, 0.0);
        if (!player.getAbilities().instabuild) {
            stack.shrink(amount);
        }
        return InteractionResult.SUCCESS;
    }
}
