package com.xkingdark.betterbonemeal.core.mixin.blocks;
import net.minecraft.util.Mth;
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

@Mixin(NetherWartBlock.class)
public class NetherWartBlockMixin extends Block implements BonemealableBlock {
    public NetherWartBlockMixin(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Unique
    public int getMaxAge() {
        return NetherWartBlock.MAX_AGE;
    }

    @Unique
    public int getAge(BlockState state) {
        return state.getValue(NetherWartBlock.AGE);
    }

    @Unique
    public final boolean isMature(BlockState state) {
        return this.getAge(state) >= this.getMaxAge();
    }

    @Override
    public boolean isValidBonemealTarget(@NonNull LevelReader level, @NonNull BlockPos pos, @NonNull BlockState state) {
        return !this.isMature(state);
    }

    @Override
    public boolean isBonemealSuccess(@NonNull Level level, @NonNull RandomSource random, @NonNull BlockPos pos, @NonNull BlockState state) {
        return true;
    }

    @Unique
    protected int getGrowthAmount(Level level) {
        return Mth.nextInt(level.getRandom(), 2, 3);
    }

    @Unique
    public BlockState withAge(int age) {
        return this.defaultBlockState().setValue(NetherWartBlock.AGE, age);
    }

    @Override
    public void performBonemeal(@NonNull ServerLevel level, @NonNull RandomSource random, @NonNull BlockPos pos, @NonNull BlockState state) {
        int i = Math.min(this.getMaxAge(), this.getAge(state) + this.getGrowthAmount(level));
        level.setBlock(pos, this.withAge(i), 2);
    }
}