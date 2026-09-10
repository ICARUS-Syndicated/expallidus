package org.icarus.expallidus.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ParticleUtils {
    protected ParticleUtils() {
    }

    public static void addParticlesAroundEntity(Entity entity, ParticleOptions options, int amount) {
        RandomSource randomSource = entity.getRandom();
        for (int i = 0; i < amount; ++i) {
            double deltaX = randomSource.nextGaussian() * 0.02;
            double deltaY = randomSource.nextGaussian() * 0.02;
            double deltaZ = randomSource.nextGaussian() * 0.02;
            entity.level().addParticle(options,
                entity.getRandomX((double) 1.0F),
                entity.getRandomY() + (double) 1.0F,
                entity.getRandomZ((double) 1.0F), deltaX, deltaY, deltaZ);
        }

    }

    public static void addParticlesAroundBlock(Player player,
                                               Level level,
                                               BlockPos blockPos,
                                               ParticleOptions options,
                                               int amount) {
        RandomSource randomSource = player.getRandom();
        for (int i = 0; i < amount; ++i) {
            double deltaX = randomSource.nextGaussian() * 0.02;
            double deltaY = randomSource.nextGaussian() * 0.02;
            double deltaZ = randomSource.nextGaussian() * 0.02;
            Vec3 center = blockPos.getCenter();
            level.addParticle(options,
                getRandomDouble(center.x, randomSource, 1.0),
                getRandomDouble(center.y, randomSource, 1.0),
                getRandomDouble(center.z, randomSource, 1.0), deltaX, deltaY, deltaZ);
        }
    }

    private static double getRandomDouble(double origin, RandomSource randomSource, double scale) {
        return origin + (2.0F * randomSource.nextDouble() - (double) 1.0F) * scale;
    }
}
