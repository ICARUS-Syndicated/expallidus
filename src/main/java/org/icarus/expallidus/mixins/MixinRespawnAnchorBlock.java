package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SpellParticleOption;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.icarus.expallidus.utils.ParticleUtils.addParticlesAroundBlock;

@Mixin(RespawnAnchorBlock.class)
public abstract class MixinRespawnAnchorBlock {
    @Unique
    private static final Item[] EXPALLIDUS$MEAT = {Items.BEEF, Items.PORKCHOP, Items.MUTTON, Items.CHICKEN, Items.RABBIT};
    @Unique
    private static final Item[] EXPALLIDUS$CROP = {Items.WHEAT, Items.CARROT, Items.BEETROOT, Items.POTATO};

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void expallidus$useItemOn(ItemStack stack,
                                      BlockState state,
                                      Level level,
                                      BlockPos pos,
                                      Player player,
                                      InteractionHand hand,
                                      BlockHitResult hitResult,
                                      CallbackInfoReturnable<InteractionResult> cir) {
        if (stack.is(Items.BUDDING_AMETHYST)) {
            cir.setReturnValue(expallidus$ripenWart(level, pos, player, stack));
            cir.cancel();
            return;
        }
        if (!stack.is(Items.NETHER_WART) || !player.getOffhandItem().is(Items.GLOWSTONE_DUST) || state.getValue(RespawnAnchorBlock.CHARGE) != RespawnAnchorBlock.MAX_CHARGES || !level.getBlockState(pos.below()).is(Blocks.LODESTONE) || !level.getBlockState(pos.above()).is(Blocks.REDSTONE_BLOCK)) {
            return;
        }
        cir.setReturnValue(expallidus$transmute(level, pos, player, stack));
        cir.cancel();
    }

    @Unique
    private static InteractionResult expallidus$ripenWart(Level level,
                                                          BlockPos pos,
                                                          Player player,
                                                          ItemStack stack) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        level.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 0.5F, 1.0F);
        addParticlesAroundBlock(level, pos, ParticleTypes.SOUL_FIRE_FLAME, 25, 0.5, 0.02);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        for (BlockPos wartPos : BlockPos.betweenClosed(pos.offset(-7, -7, -7), pos.offset(7, 7, 7))) {
            BlockState wartState = level.getBlockState(wartPos);
            if (!wartState.is(Blocks.NETHER_WART) || player.getRandom().nextFloat() >= 0.75F) {
                continue;
            }
            int age = wartState.getValue(NetherWartBlock.AGE);
            if (age >= 3) {
                continue;
            }
            level.setBlockAndUpdate(wartPos, wartState.setValue(NetherWartBlock.AGE, age + 1));
            addParticlesAroundBlock(level, wartPos.immutable(), ParticleTypes.SOUL, 3, 0.0, 0.01);
        }
        return InteractionResult.SUCCESS;
    }

    @Unique
    private static InteractionResult expallidus$transmute(Level level, BlockPos pos, Player player, ItemStack stack) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        int amount = player.isShiftKeyDown() ? stack.getCount() : 1;
        for (int i = 0; i < amount; i++) {
            Item item = player.getRandom().nextFloat() < 0.6F ? EXPALLIDUS$MEAT[player.getRandom().nextInt(EXPALLIDUS$MEAT.length)] : EXPALLIDUS$CROP[player.getRandom().nextInt(EXPALLIDUS$CROP.length)];
            Block.popResource(level, pos, new ItemStack(item, 1 + player.getRandom().nextInt(3)));
        }
        level.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.BLOCKS, 0.5F, 1.0F);
        addParticlesAroundBlock(level, pos, SpellParticleOption.create(ParticleTypes.EFFECT, 1.0F, 1.0F, 1.0F, 0.5F), 25, 0.5, 0.02);
        player.swing(InteractionHand.MAIN_HAND);
        if (!player.getAbilities().instabuild) {
            stack.shrink(amount);
            ItemStack offhand = player.getOffhandItem();
            offhand.shrink(Math.min(amount, offhand.getCount()));
        }
        return InteractionResult.SUCCESS;
    }
}
