package com.xkingdark.betterbonemeal.core.mixin.blocks;

import com.xkingdark.betterbonemeal.Main;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(SugarCaneBlock.class)
public abstract class SugarCaneBlockMixin extends Block implements Fertilizable {
    public SugarCaneBlockMixin(Settings settings) {
        super(settings);
    };

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        ArrayList<BlockPos> list = this.getBlocks((World) world, pos);
        BlockPos up = list.getLast().up();
        int maxY = world.getTopYInclusive();
        if (!world.isAir(up) || up.getY() >= maxY)
            return false;

        return list.size() < 3;
    };

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    };

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        SugarCaneBlock block = (SugarCaneBlock)(Object)this;

        int height = world.getHeight();
        for (int y = pos.getY(); y <= height; y++) {
            BlockPos up = pos.withY(y);
            BlockState blockState = world.getBlockState(up);
            if (blockState.isOf(block))
                continue;

            if (!world.isAir(up))
                break;

            world.setBlockState(up, this.getDefaultState());
            world.setBlockState(up.down(), state.with(SugarCaneBlock.AGE, 0), 260);
            break;
        };
    };

    @Unique
    private ArrayList<BlockPos> getBlocks(World world, BlockPos pos) {
        SugarCaneBlock block = (SugarCaneBlock)(Object)this;
        ArrayList<BlockPos> positions = new ArrayList<>();

        int x = 1;
        while (world.getBlockState(pos.down(x)).isOf(block)) {
            positions.add(pos.down(x));
            x++;
        };

        positions.add(pos);

        int z = 1;
        while (world.getBlockState(pos.up(z)).isOf(block)) {
            positions.add(pos.up(z));
            z++;
        };

        return positions;
    };
};