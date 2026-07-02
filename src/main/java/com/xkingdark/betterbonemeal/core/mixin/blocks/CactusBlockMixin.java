package com.xkingdark.betterbonemeal.core.mixin.blocks;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;

@Mixin(CactusBlock.class)
public abstract class CactusBlockMixin extends Block implements BonemealableBlock {
    public CactusBlockMixin(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
    public boolean isValidBonemealTarget(LevelReader level, @NonNull BlockPos pos, @NonNull BlockState state) {
        ArrayList<BlockPos> list = this.getBlocks((Level) level, pos);
        BlockPos up = list.getLast().above();
        int maxY = level.getMaxY();

        if (!level.isEmptyBlock(up) || up.getY() >= maxY) {
            return false;
        }

        return list.size() < 3;
    }

    @Override
    public boolean isBonemealSuccess(@NonNull Level level, @NonNull RandomSource random, @NonNull BlockPos pos, @NonNull BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, @NonNull RandomSource random, BlockPos pos, @NonNull BlockState state) {
        CactusBlock block = (CactusBlock)(Object)this;

        int height = level.getHeight();
        for (int y = pos.getY(); y <= height; y++) {
            BlockPos up = pos.atY(y);
            BlockState blockState = level.getBlockState(up);
            if (blockState.is(block)) {
                continue;
            }

            if (!level.isEmptyBlock(up)) {
                break;
            }

            if (random.nextDouble() <= 0.25F) {
                level.setBlockAndUpdate(up, Blocks.CACTUS_FLOWER.defaultBlockState());
                return;
            }

            level.setBlockAndUpdate(up, this.defaultBlockState());

            BlockState newState = state.trySetValue(CactusBlock.AGE, 0);
            level.setBlock(up.below(), newState, 3);
            level.neighborChanged(newState, up, this, null, false);
            break;
        }
    }

    @Unique
    private ArrayList<BlockPos> getBlocks(Level level, BlockPos pos) {
        CactusBlock block = (CactusBlock)(Object)this;
        ArrayList<BlockPos> positions = new ArrayList<>();

        int x = 1;
        while (level.getBlockState(pos.below(x)).is(block)) {
            positions.add(pos.below(x));
            x++;
        }

        positions.add(pos);

        int z = 1;
        while (level.getBlockState(pos.above(z)).is(block)) {
            positions.add(pos.above(z));
            z++;
        }

        return positions;
    }
}