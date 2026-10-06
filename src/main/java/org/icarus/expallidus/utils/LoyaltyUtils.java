package org.icarus.expallidus.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class LoyaltyUtils {

    private static final Map<UUID, Boolean> PLAYER_TARGETING = new HashMap<>();

    private LoyaltyUtils() {
    }

    public static boolean togglePlayerTargeting(Player player) {
        boolean enabled = !PLAYER_TARGETING.getOrDefault(player.getUUID(), false);
        PLAYER_TARGETING.put(player.getUUID(), enabled);
        return enabled;
    }

    public static boolean canTargetPlayers(Player shooter) {
        return PLAYER_TARGETING.getOrDefault(shooter.getUUID(), false);
    }

    public static Entity findTarget(ServerLevel level, Entity projectile, Player shooter, int loyaltyLevel) {
        double radius = loyaltyLevel * 2.5;
        AABB bounds = projectile.getBoundingBox().inflate(radius);
        Entity target = null;
        double nearest = Double.MAX_VALUE;
        for (Entity entity : level.getEntities(projectile, bounds, entity -> isTargetable(entity, shooter))) {
            double distance = entity.distanceToSqr(projectile);
            if (distance > radius * radius || distance >= nearest) {
                continue;
            }
            nearest = distance;
            target = entity;
        }
        return target;
    }

    public static void redirect(Entity projectile, Entity target, int loyaltyLevel, double yOffset) {
        Vec3 targetLocation = target.position().add(0.0, yOffset, 0.0);
        Vec3 direction = targetLocation.subtract(projectile.position());
        if (direction.lengthSqr() < 1.0E-6) {
            return;
        }
        Vec3 velocity = direction.normalize().scale(loyaltyLevel * 0.9);
        Vec3 targetVelocity = target.getDeltaMovement();
        if (targetVelocity.length() > 0.1) {
            velocity = velocity.add(targetVelocity.scale(0.2));
        }
        projectile.setDeltaMovement(velocity);
    }

    private static boolean isTargetable(Entity entity, Player shooter) {
        if (entity == shooter || !entity.isAlive()) {
            return false;
        }
        if (entity instanceof Monster || entity instanceof Animal) {
            return true;
        }
        return entity instanceof Player && canTargetPlayers(shooter);
    }
}
