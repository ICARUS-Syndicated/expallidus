package org.icarus.expallidus.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkCatalystBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static org.icarus.expallidus.utils.ParticleUtils.addParticlesAroundBlock;

@Mixin(SculkCatalystBlock.class)
public abstract class MixinSculkCatalystBlock extends BaseEntityBlock {
    @Unique
    private static final Item[] EXPALLIDUS$SOIL = {Items.DIRT, Items.GRASS_BLOCK, Items.PODZOL, Items.COARSE_DIRT, Items.ROOTED_DIRT, Items.MYCELIUM};
    @Unique
    private static final Item[] EXPALLIDUS$MUD = {Items.MUD, Items.CLAY};
    @Unique
    private static final Item[] EXPALLIDUS$SCULK = {Items.SCULK_SHRIEKER, Items.SCULK, Items.SCULK_SENSOR};

    protected MixinSculkCatalystBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack stack,
                                                   @NotNull BlockState state,
                                                   Level level,
                                                   BlockPos pos,
                                                   Player player,
                                                   @NotNull InteractionHand hand,
                                                   @NotNull BlockHitResult hitResult) {
        ItemStack tool = player.getMainHandItem();
        if (!level.getBlockState(pos.below()).is(Blocks.LODESTONE)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        if (tool.is(Blocks.SCULK.asItem())) {
            return expallidus$transmuteSculk(level, pos, player, tool);
        }
        if (tool.is(Blocks.END_STONE.asItem())) {
            return expallidus$transmuteEndStone(level, pos, player, tool);
        }
        if (tool.is(Items.ECHO_SHARD)) {
            return expallidus$transmuteEchoShard(level, pos, player, tool);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Unique
    private static InteractionResult expallidus$transmuteSculk(Level level, BlockPos pos, Player player, ItemStack tool) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        int amount = player.isShiftKeyDown() ? tool.getCount() : 1;
        for (int i = 0; i < amount; i++) {
            Item item = player.getRandom().nextFloat() < 0.5F ? EXPALLIDUS$SOIL[player.getRandom().nextInt(EXPALLIDUS$SOIL.length)] : EXPALLIDUS$MUD[player.getRandom().nextInt(EXPALLIDUS$MUD.length)];
            Block.popResource(level, pos, new ItemStack(item));
        }
        level.playSound(null, pos, SoundEvents.SCULK_CATALYST_BREAK, SoundSource.BLOCKS, 0.5F, 1.0F);
        addParticlesAroundBlock(level, pos, ParticleTypes.SCULK_SOUL, 15, 0.5, 0.02);
        player.swing(InteractionHand.MAIN_HAND);
        if (!player.getAbilities().instabuild) {
            tool.shrink(amount);
        }
        return InteractionResult.SUCCESS;
    }

    @Unique
    private static InteractionResult expallidus$transmuteEndStone(Level level, BlockPos pos, Player player, ItemStack tool) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        int amount = player.isShiftKeyDown() ? tool.getCount() : 1;
        for (int i = 0; i < amount; i++) {
            if (player.getRandom().nextFloat() < 0.7F) {
                level.playSound(null, pos, SoundEvents.ENDER_EYE_DEATH, SoundSource.BLOCKS, 0.1F, 1.0F);
            } else {
                Item item = EXPALLIDUS$SCULK[player.getRandom().nextInt(EXPALLIDUS$SCULK.length)];
                Block.popResource(level, pos, new ItemStack(item));
                level.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 0.1F, 1.0F);
            }
        }
        addParticlesAroundBlock(level, pos, ParticleTypes.SCULK_SOUL, 6, 0.5, 0.02);
        addParticlesAroundBlock(level, pos, ParticleTypes.END_ROD, 7, 0.5, 0.02);
        player.swing(InteractionHand.MAIN_HAND);
        if (!player.getAbilities().instabuild) {
            tool.shrink(amount);
        }
        return InteractionResult.SUCCESS;
    }

    @Unique
    private static InteractionResult expallidus$transmuteEchoShard(Level level, BlockPos pos, Player player, ItemStack tool) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        int amount = player.isShiftKeyDown() ? expallidus$countEchoShards(player) : 1;
        for (int i = 0; i < amount; i++) {
            Item item = player.getRandom().nextFloat() < 0.6F ? Items.PRISMARINE_SHARD : Items.AMETHYST_SHARD;
            Block.popResource(level, pos, new ItemStack(item, 1 + player.getRandom().nextInt(5)));
        }
        level.playSound(null, pos, SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.BLOCKS, 5.0F, 1.0F);
        addParticlesAroundBlock(level, pos, ParticleTypes.SCULK_SOUL, 6, 0.5, 0.02);
        addParticlesAroundBlock(level, pos, ParticleTypes.WHITE_SMOKE, 7, 0.5, 0.02);
        player.swing(InteractionHand.MAIN_HAND);
        if (!player.getAbilities().instabuild) {
            expallidus$removeEchoShards(player, amount);
        }
        return InteractionResult.SUCCESS;
    }

    @Unique
    private static int expallidus$countEchoShards(Player player) {
        Inventory inventory = player.getInventory();
        int total = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack slot = inventory.getItem(i);
            if (slot.is(Items.ECHO_SHARD)) {
                total += slot.getCount();
            }
        }
        return total;
    }

    @Unique
    private static void expallidus$removeEchoShards(Player player, int amount) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize() && amount > 0; i++) {
            ItemStack slot = inventory.getItem(i);
            if (slot.is(Items.ECHO_SHARD)) {
                int removed = Math.min(amount, slot.getCount());
                slot.shrink(removed);
                amount -= removed;
            }
        }
    }
}
