package org.icarus.expallidus.mixins;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ZombifiedPiglin.class)
public abstract class MixinZombifiedPiglin extends Zombie {

    @Unique
    private static final EntityDataAccessor<Boolean> EXPALLIDUS$DATA_CONVERTING =
        SynchedEntityData.defineId(ZombifiedPiglin.class, EntityDataSerializers.BOOLEAN);

    @Unique
    private static final int EXPALLIDUS$CONVERSION_WAIT_MIN = 3600;

    @Unique
    private static final int EXPALLIDUS$CONVERSION_WAIT_MAX = 6000;

    @Unique
    private int expallidus$conversionTime = -1;

    @Unique
    private @Nullable UUID expallidus$conversionStarter;

    protected MixinZombifiedPiglin(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(EXPALLIDUS$DATA_CONVERTING, false);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("ConversionTime", this.expallidus$isConverting() ? this.expallidus$conversionTime : -1);
        output.storeNullable("ConversionPlayer", UUIDUtil.CODEC, this.expallidus$conversionStarter);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        int conversionTime = input.getIntOr("ConversionTime", -1);
        if (conversionTime != -1) {
            UUID starter = input.<UUID>read("ConversionPlayer", UUIDUtil.CODEC).orElse(null);
            this.expallidus$startConverting(starter, conversionTime, false);
        }
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide() && this.isAlive() && this.expallidus$isConverting()) {
            if (--this.expallidus$conversionTime <= 0) {
                this.expallidus$finishConversion((ServerLevel) this.level());
            }
        }
        super.tick();
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (!itemInHand.is(Items.GOLDEN_APPLE)) {
            return super.mobInteract(player, hand);
        }
        if (!this.hasEffect(MobEffects.WEAKNESS)) {
            return InteractionResult.CONSUME;
        }
        itemInHand.consume(1, player);
        if (!this.level().isClientSide()) {
            this.expallidus$startConverting(player.getUUID(), this.getRandom().nextInt(EXPALLIDUS$CONVERSION_WAIT_MAX - EXPALLIDUS$CONVERSION_WAIT_MIN + 1) + EXPALLIDUS$CONVERSION_WAIT_MIN, true);
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 16) {
            if (!this.isSilent()) {
                this.level()
                    .playLocalSound(
                        this.getX(),
                        this.getEyeY(),
                        this.getZ(),
                        SoundEvents.ZOMBIE_VILLAGER_CURE,
                        this.getSoundSource(),
                        1.0F + this.getRandom().nextFloat(),
                        this.getRandom().nextFloat() * 0.7F + 0.3F,
                        false
                    );
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Unique
    private boolean expallidus$isConverting() {
        return this.getEntityData().get(EXPALLIDUS$DATA_CONVERTING);
    }

    @Unique
    private void expallidus$startConverting(@Nullable UUID conversionStarter, int conversionTime, boolean broadcastEntityEvent) {
        this.expallidus$conversionStarter = conversionStarter;
        this.expallidus$conversionTime = conversionTime;
        this.getEntityData().set(EXPALLIDUS$DATA_CONVERTING, true);
        this.removeEffect(MobEffects.WEAKNESS, EntityPotionEffectEvent.Cause.CONVERSION);
        this.addEffect(
            new MobEffectInstance(MobEffects.STRENGTH, conversionTime, Math.min(this.level().getDifficulty().getId() - 1, 0)),
            EntityPotionEffectEvent.Cause.CONVERSION
        );
        if (broadcastEntityEvent) {
            this.level().broadcastEntityEvent(this, (byte) 16);
        }
    }

    @Unique
    private void expallidus$finishConversion(ServerLevel level) {
        Piglin piglin = this.convertTo(
            EntityType.PIGLIN,
            ConversionParams.single(this, true, false),
            mob -> {
                mob.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 200, 0), EntityPotionEffectEvent.Cause.CONVERSION);
                if (!this.isSilent()) {
                    level.levelEvent(null, 1027, this.blockPosition(), 0);
                }
            },
            EntityTransformEvent.TransformReason.CURED,
            CreatureSpawnEvent.SpawnReason.CURED
        );
        if (piglin == null) {
            this.expallidus$conversionTime = -1;
        }
    }
}
