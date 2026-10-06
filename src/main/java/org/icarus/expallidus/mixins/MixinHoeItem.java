package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoeItem.class)
public abstract class MixinHoeItem {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void expallidus$useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!level.getBlockState(pos).is(Blocks.SOUL_SOIL)) {
            return;
        }
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        player.swing(context.getHand());
        level.setBlockAndUpdate(pos, Blocks.SOUL_SAND.defaultBlockState());
        level.playSound(null, pos, SoundEvents.SOUL_SOIL_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SOUL_SAND.defaultBlockState()),
                pos.getX() + 0.5,
                pos.getY() + 1.5,
                pos.getZ() + 0.5,
                5,
                0.0,
                0.0,
                0.0,
                0.0
            );
        }
        if (!player.getAbilities().instabuild) {
            context.getItemInHand().hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
        }
        cir.setReturnValue(InteractionResult.SUCCESS);
        cir.cancel();
    }
}
