package org.icarus.expallidus.mixins;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Creeper.class)
public abstract class MixinCreeper {

    @Inject(method = "killedEntity", at = @At("HEAD"))
    private void expallidus$playerHead(
        ServerLevel level,
        LivingEntity entity,
        DamageSource damageSource,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (!(entity instanceof ServerPlayer player) || !((Creeper) (Object) this).isPowered()) {
            return;
        }
        ItemStack skull = new ItemStack(Items.PLAYER_HEAD);
        skull.set(DataComponents.PROFILE, ResolvableProfile.createResolved(player.getGameProfile()));
        entity.spawnAtLocation(level, skull);
    }
}
