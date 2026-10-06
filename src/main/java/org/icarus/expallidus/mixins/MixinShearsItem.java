package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShearsItem.class)
public abstract class MixinShearsItem {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void expallidus$useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!level.getBlockState(pos).is(Blocks.MOSSY_COBBLESTONE)) {
            return;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        BlockState newState = Blocks.COBBLESTONE.defaultBlockState();
        if (!CraftEventFactory.callEntityChangeBlockEvent(player, pos, newState)) {
            return;
        }

        cir.setReturnValue(InteractionResult.SUCCESS);

        player.swing(context.getHand());
        level.playSound(player, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 0.5F, 1.0F);
        level.setBlockAndUpdate(pos, newState);
        Block.popResource(level, pos, new ItemStack(Items.VINE));
        context.getItemInHand().hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
    }
}
