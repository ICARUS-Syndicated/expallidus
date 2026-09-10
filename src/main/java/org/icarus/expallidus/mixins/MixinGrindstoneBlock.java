package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.GrindstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GrindstoneBlock.class)
public abstract class MixinGrindstoneBlock extends FaceAttachedHorizontalDirectionalBlock {
    protected MixinGrindstoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack stack,
                                                   @NotNull BlockState state,
                                                   @NotNull Level level,
                                                   @NotNull BlockPos pos,
                                                   @NotNull Player player,
                                                   @NotNull InteractionHand hand,
                                                   @NotNull BlockHitResult hitResult) {
        if (stack.is(Items.COBBLESTONE)) {
            int amount = expallidus$getAmount(stack, player, hand);
            Entity item = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.GRAVEL, amount));
            expallidus$postProcess(level, pos, player, item);
            return InteractionResult.SUCCESS;
        } else if (stack.is(Items.GRAVEL)) {
            int amount = expallidus$getAmount(stack, player, hand);
            amount += player.getRandom().nextInt(amount);
            Entity item = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.SAND, amount));
            expallidus$postProcess(level, pos, player, item);
            return InteractionResult.SUCCESS;
        } else if (stack.is(Items.GRANITE)) {
            int amount = expallidus$getAmount(stack, player, hand);
            for (int i = 0; i < amount; i++) {
                double chance = player.getRandom().nextDouble();
                if (chance >= 0.75) {
                    Item[] items = {Items.RAW_IRON, Items.RAW_COPPER};
                    int index = player.getRandom().nextInt(255) % 2;
                    int itemAmount = player.getRandom().nextInt(3);
                    Entity item = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(items[index], itemAmount));
                    level.addFreshEntity(item);
                } else {
                    int itemAmount = player.getRandom().nextInt(2);
                    Entity item = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.RAW_GOLD, itemAmount));
                    level.addFreshEntity(item);
                }
            }
            level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
            level.addParticle(ParticleTypes.WHITE_ASH,
                (double) pos.getX() + 0.5F,
                (double) pos.getY() + 1.0F,
                (double) pos.getZ() + 0.5F,
                0.0F,
                0.0F,
                0.0F);
            return InteractionResult.SUCCESS;
        }
        return this.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Unique
    private static void expallidus$postProcess(@NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, Entity item) {
        level.addFreshEntity(item);
        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
        level.addParticle(ParticleTypes.WHITE_ASH,
            (double) pos.getX() + 0.5F,
            (double) pos.getY() + 1.0F,
            (double) pos.getZ() + 0.5F,
            0.0F,
            0.0F,
            0.0F);
    }

    @Unique
    private static int expallidus$getAmount(@NonNull ItemStack stack, @NonNull Player player, @NonNull InteractionHand hand) {
        int amount;
        if (player.isShiftKeyDown()) {
            amount = stack.getCount();
        } else {
            amount = 1;
        }
        stack.shrink(amount);
        if (stack.isEmpty()) {
            player.setItemInHand(hand, new ItemStack(Items.AIR));
        }
        return amount;
    }
}
