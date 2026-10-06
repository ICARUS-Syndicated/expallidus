package org.icarus.expallidus.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
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

    public static void addParticlesAroundEntity(Entity entity,
                                                ParticleOptions options,
                                                int amount,
                                                double spread,
                                                double extra) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(options,
                entity.getX(),
                entity.getY() + (double) 1.0F,
                entity.getZ(),
                amount,
                spread,
                spread,
                spread,
                extra);
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

    public static void addParticlesAroundBlock(Level level,
                                               BlockPos blockPos,
                                               ParticleOptions options,
                                               int amount,
                                               double spread,
                                               double extra) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(options,
                blockPos.getX() + 0.5,
                blockPos.getY() + 0.5,
                blockPos.getZ() + 0.5,
                amount,
                spread,
                spread,
                spread,
                extra);
        }
    }

    private static double getRandomDouble(double origin, RandomSource randomSource, double scale) {
        return origin + (2.0F * randomSource.nextDouble() - (double) 1.0F) * scale;
    }
}
