package com.xkingdark.betterbonemeal.core.mixin.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(NetherWartBlock.class)
public class NetherWartBlockMixin extends Block implements Fertilizable {
    public NetherWartBlockMixin(Settings settings) {
        super(settings);
    }

    @Unique
    public int getMaxAge() {
        return NetherWartBlock.MAX_AGE;
    };

    @Unique
    public int getAge(BlockState state) {
        return state.get(NetherWartBlock.AGE);
    };

    @Unique
    public final boolean isMature(BlockState state) {
        return this.getAge(state) >= this.getMaxAge();
    };

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return !this.isMature(state);
    };

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    };

    @Unique
    protected int getGrowthAmount(World world) {
        return MathHelper.nextInt(world.random, 2, 3);
    };

    @Unique
    public BlockState withAge(int age) {
        return this.getDefaultState().with(NetherWartBlock.AGE, age);
    };

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int i = Math.min(this.getMaxAge(), this.getAge(state) + this.getGrowthAmount(world));
        world.setBlockState(pos, this.withAge(i), 2);
    };
};