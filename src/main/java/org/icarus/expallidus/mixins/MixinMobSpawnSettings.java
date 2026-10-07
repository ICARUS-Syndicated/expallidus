package org.icarus.expallidus.mixins;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MobSpawnSettings.class)
public abstract class MixinMobSpawnSettings {

    @Unique
    private static final int EXPALLIDUS$GIANT_WEIGHT = 1;

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static Map<MobCategory, WeightedList<MobSpawnSettings.SpawnerData>> expallidus$addGiantSpawn(
        Map<MobCategory, WeightedList<MobSpawnSettings.SpawnerData>> spawners
    ) {
        WeightedList<MobSpawnSettings.SpawnerData> monsters = spawners.get(MobCategory.MONSTER);
        if (spawners.isEmpty() || monsters == null || !expallidus$spawnsZombies(monsters)) {
            return spawners;
        }

        WeightedList.Builder<MobSpawnSettings.SpawnerData> builder = WeightedList.builder();
        for (Weighted<MobSpawnSettings.SpawnerData> entry : monsters.unwrap()) {
            builder.add(entry.value(), entry.weight());
        }
        builder.add(new MobSpawnSettings.SpawnerData(EntityType.GIANT, 1, 1), EXPALLIDUS$GIANT_WEIGHT);

        Map<MobCategory, WeightedList<MobSpawnSettings.SpawnerData>> modified = new EnumMap<>(spawners);
        modified.put(MobCategory.MONSTER, builder.build());
        return modified;
    }

    @Unique
    private static boolean expallidus$spawnsZombies(WeightedList<MobSpawnSettings.SpawnerData> monsters) {
        for (Weighted<MobSpawnSettings.SpawnerData> entry : monsters.unwrap()) {
            if (entry.value().type() == EntityType.ZOMBIE) {
                return true;
            }
        }
        return false;
    }
}
