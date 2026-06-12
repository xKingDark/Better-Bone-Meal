package com.xkingdark.betterbonemeal.core.mixin.items;

import com.xkingdark.betterbonemeal.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {
    @Inject(
        method = "useOn",
        at = @At("HEAD"),
        cancellable = true
    )
    private void useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        ItemStack itemStack = context.getItemInHand();
        BlockState blockState = level.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (!(block instanceof BonemealableBlock fertilizable) || !fertilizable.isValidBonemealTarget(level, blockPos, blockState)) {
            return;
        }

        if (level instanceof ServerLevel) {
            if (player.isCrouching() && (block instanceof CropBlock crop)) {
                int maxAge = crop.getMaxAge();
                int age = crop.getAge(blockState);

                if (!level.isClientSide()) {
                    context.getPlayer().gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                    level.levelEvent(1505, blockPos, 15);
                }
                cir.setReturnValue(InteractionResult.SUCCESS);
                cir.cancel();

                int itemCount = itemStack.getCount();
                int amount = 0;
                while (age < maxAge && amount < itemCount) {
                    int random = Mth.nextInt(level.getRandom(), 2, 5);

                    age = Math.min(maxAge, age + random);
                    amount++;
                }

                level.setBlockAndUpdate(blockPos, blockState.trySetValue(CropBlock.AGE, age));
                itemStack.shrink(amount);
            }
        }
    }
}