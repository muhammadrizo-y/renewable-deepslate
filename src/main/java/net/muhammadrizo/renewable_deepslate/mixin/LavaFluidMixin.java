package net.muhammadrizo.renewable_deepslate.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFluid.class)
public class LavaFluidMixin {
    @Inject(
            method = "spreadTo(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onSpreadTo(LevelAccessor world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState, CallbackInfo ci) {
        // Mirrors the vanilla DOWN-onto-water branch of spreadTo; reimplemented instead of a
        // @Redirect on setBlockAndUpdate because NeoForge patches that call to setBlock(pos, state, 3).
        if (direction != Direction.DOWN) return;
        if (!world.getFluidState(pos).is(FluidTags.WATER)) return;
        if (!(state.getBlock() instanceof LiquidBlock)) return;

        BlockState replacement = pos.getY() < 0
                ? Blocks.DEEPSLATE.defaultBlockState()
                : Blocks.STONE.defaultBlockState();
        world.setBlockAndUpdate(pos, replacement);

        // Vanilla LavaFluid#fizz body is exactly this levelEvent call.
        world.levelEvent(1501, pos, 0);
        ci.cancel();
    }
}
