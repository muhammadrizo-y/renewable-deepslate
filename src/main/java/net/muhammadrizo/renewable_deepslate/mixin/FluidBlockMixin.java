package net.muhammadrizo.renewable_deepslate.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public class FluidBlockMixin {
    @Inject(
            method = "shouldSpreadLiquid(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onShouldSpreadLiquid(Level world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!state.getFluidState().is(FluidTags.LAVA)) return;
        if (pos.getY() >= 0) return;

        boolean isSoulSoil = world.getBlockState(pos.below()).is(Blocks.SOUL_SOIL);

        for (Direction direction : LiquidBlock.POSSIBLE_FLOW_DIRECTIONS) {
            BlockPos neighborPos = pos.relative(direction.getOpposite());

            if (world.getFluidState(neighborPos).is(FluidTags.WATER)) {
                Block block = world.getFluidState(pos).isSource()
                        ? Blocks.OBSIDIAN
                        : Blocks.COBBLED_DEEPSLATE;
                world.setBlockAndUpdate(pos, block.defaultBlockState());

                // Vanilla has no named constant for this in Mojmap names; LiquidBlock#fizz uses the literal 1501 (yarn: WorldEvents.LAVA_EXTINGUISHED).
                world.levelEvent(1501, pos, 0);
                cir.setReturnValue(false);
                return;
            }

            if (isSoulSoil && world.getBlockState(neighborPos).is(Blocks.BLUE_ICE)) {
                world.setBlockAndUpdate(pos, Blocks.BASALT.defaultBlockState());
                world.levelEvent(1501, pos, 0);
                cir.setReturnValue(false);
                return;
            }
        }
    }
}
