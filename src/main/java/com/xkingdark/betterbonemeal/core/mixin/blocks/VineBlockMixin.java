package com.xkingdark.betterbonemeal.core.mixin.blocks;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;

@Mixin(VineBlock.class)
public class VineBlockMixin extends Block implements Fertilizable {
    public VineBlockMixin(Settings settings) {
        super(settings);
    };

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        VineBlock block = (VineBlock)(Object)this;
        ArrayList<BlockPos> positions = new ArrayList<>();
        positions.add(pos);

        int x = 1;
        while (world.getBlockState(pos.down(x)).isOf(block)) {
            positions.add(pos.down(x));
            x++;
        };

        int minY = world.getDimension().minY();
        BlockPos down = positions.getLast().down();
        return world.isAir(down) && down.getY() >= minY;
    };

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    };

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        VineBlock block = (VineBlock)(Object)this;

        int minY = world.getDimension().minY();
        for (int y = pos.getY(); y >= minY; y--) {
            BlockPos down = pos.withY(y);
            BlockState blockState = world.getBlockState(down);

            if (blockState.isOf(block))
                continue;

            if (!world.isAir(down))
                break;

            world.setBlockState(down, state);
            world.setBlockState(down.up(), state, 260);
            break;
        };
    };
};