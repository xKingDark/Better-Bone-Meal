package com.xkingdark.betterbonemeal.core.mixin.items;

import com.xkingdark.betterbonemeal.Main;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {
    @Inject(
        method = "useOnBlock",
        at = @At("HEAD"),
        cancellable = true
    )
    private void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        if (player == null)
            return;

        ItemStack itemStack = context.getStack();
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block instanceof Fertilizable fertilizable) {
            if (fertilizable.isFertilizable(world, blockPos, blockState)) {
                if (world instanceof ServerWorld) {
                    if (player.isSneaking() && (block instanceof CropBlock crop)) {
                        int maxAge = crop.getMaxAge();
                        int age = crop.getAge(blockState);

                        if (!world.isClient()) {
                            context.getPlayer().emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
                            world.syncWorldEvent(1505, blockPos, 15);
                        };
                        cir.setReturnValue(ActionResult.SUCCESS);
                        cir.cancel();

                        int itemCount = itemStack.getCount();
                        int amount = 0;
                        while (age < maxAge && amount < itemCount) {
                            int random = MathHelper.nextInt(world.random, 2, 5);
                            age = Math.min(maxAge, age + random);
                            amount++;
                        };

                        world.setBlockState(blockPos, blockState.with(CropBlock.AGE, age));
                        itemStack.decrement(amount);
                    };
                };
            };
        };
    };
};