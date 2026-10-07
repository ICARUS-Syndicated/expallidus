package org.icarus.expallidus.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpawnPlacements.class)
public abstract class MixinSpawnPlacements {

    @ModifyReturnValue(method = "checkSpawnRules", at = @At("RETURN"))
    private static boolean expallidus$preventGiantSuffocation(
        boolean original,
        EntityType<?> entityType,
        ServerLevelAccessor level,
        EntitySpawnReason spawnReason,
        BlockPos pos,
        RandomSource random
    ) {
        if (!original || entityType != EntityType.GIANT) {
            return original;
        }
        return level.noCollision(entityType.getSpawnAABB(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
    }
}
