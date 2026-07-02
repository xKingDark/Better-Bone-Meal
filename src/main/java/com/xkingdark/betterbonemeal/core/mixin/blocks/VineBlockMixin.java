package com.xkingdark.betterbonemeal.core.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;

@Mixin(VineBlock.class)
public class VineBlockMixin extends Block implements BonemealableBlock {
    public VineBlockMixin(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
    public boolean isValidBonemealTarget(LevelReader level, @NonNull BlockPos pos, @NonNull BlockState state) {
        VineBlock block = (VineBlock)(Object)this;
        ArrayList<BlockPos> positions = new ArrayList<>();
        positions.add(pos);

        int x = 1;
        while (level.getBlockState(pos.below(x)).is(block)) {
            positions.add(pos.below(x));
            x++;
        }

        int minY = level.dimensionType().minY();
        BlockPos down = positions.getLast().below();

        return level.isEmptyBlock(down) && down.getY() >= minY;
    }

    @Override
    public boolean isBonemealSuccess(@NonNull Level level, @NonNull RandomSource random, @NonNull BlockPos pos, @NonNull BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, @NonNull RandomSource random, BlockPos pos, @NonNull BlockState state) {
        VineBlock block = (VineBlock)(Object)this;

        int minY = level.dimensionType().minY();
        for (int y = pos.getY(); y >= minY; --y) {
            BlockPos current = pos.atY(y);
            BlockState currentState = level.getBlockState(current);

            if (currentState.is(block)) {
                continue;
            }

            if (!currentState.isAir()) {
                break;
            }

            level.setBlockAndUpdate(current, state);
            break;
        }
    }
}