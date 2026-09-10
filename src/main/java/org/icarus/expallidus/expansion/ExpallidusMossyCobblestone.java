package org.icarus.expallidus.expansion;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.bukkit.craftbukkit.event.CraftEventFactory;

public class ExpallidusMossyCobblestone extends Block {
    public ExpallidusMossyCobblestone(Properties properties) {
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
        if(stack.is(Items.SHEARS)){
            ItemStack itemInHand =  player.getItemInHand(hand);
            itemInHand.hurtAndBreak(1, player, hand.asEquipmentSlot());
            Vec3 center = pos.getCenter();
            level.setBlockAndUpdate(pos, Blocks.COBBLESTONE.defaultBlockState());
            Entity item = new ItemEntity(level, center.x, center.y, center.z, new ItemStack(Items.VINE, 1));
            level.addFreshEntity(item);
            level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.SHEARS_SNIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            CraftEventFactory.callEntityChangeBlockEvent(player, pos, Blocks.COBBLESTONE.defaultBlockState());
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
