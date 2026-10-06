package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import static org.icarus.expallidus.utils.ParticleUtils.addParticlesAroundBlock;

@Mixin(BellBlock.class)
public abstract class MixinBellBlock extends BaseEntityBlock {
    protected MixinBellBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!stack.is(Items.ECHO_SHARD)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        player.swing(hand);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        level.playSound(null, pos, SoundEvents.WARDEN_TENDRIL_CLICKS, SoundSource.BLOCKS, 0.5F, 1.0F);
        addParticlesAroundBlock(level, pos, ParticleTypes.SCRAPE, 20, 0.5, 0.02);
        Vec3 center = pos.getCenter();
        for (Monster monster : level.getEntitiesOfClass(Monster.class, new AABB(pos).inflate(50.0))) {
            if (monster.position().distanceTo(center) > 50.0) {
                continue;
            }
            monster.addEffect(new MobEffectInstance(MobEffects.GLOWING, 900, 1));
            monster.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 900, 1));
        }
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
