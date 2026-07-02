package com.xkingdark.betterbonemeal.core.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;

@Mixin(SugarCaneBlock.class)
public abstract class SugarCaneBlockMixin extends Block implements BonemealableBlock {
    public SugarCaneBlockMixin(BlockBehaviour.Properties properties) {
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
        SugarCaneBlock block = (SugarCaneBlock)(Object)this;

        int height = level.getHeight();
        for (int y = pos.getY(); y <= height; y++) {
            BlockPos current = pos.atY(y);
            BlockState currentState = level.getBlockState(current);
            if (currentState.is(block)) {
                continue;
            }

            if (!level.isEmptyBlock(current)) {
                break;
            }

            level.setBlockAndUpdate(current, this.defaultBlockState());
            break;
        }
    }

    @Unique
    private ArrayList<BlockPos> getBlocks(@UnknownNullability Level world, BlockPos pos) {
        SugarCaneBlock block = (SugarCaneBlock)(Object)this;
        ArrayList<BlockPos> positions = new ArrayList<>();

        int x = 1;
        while (world.getBlockState(pos.below(x)).is(block)) {
            positions.add(pos.below(x));
            x++;
        }

        positions.add(pos);

        int z = 1;
        while (world.getBlockState(pos.above(z)).is(block)) {
            positions.add(pos.above(z));
            z++;
        }

        return positions;
    }
}