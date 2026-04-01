package net.muhammadrizo.renewable_deepslate.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LavaFluid.class)
public class LavaFluidMixin {
    @Redirect(
            method = "flow(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;Lnet/minecraft/fluid/FluidState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/WorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"
            )
    )
    private boolean redirectStoneGen(WorldAccess world, BlockPos pos, BlockState state, int flags) {
        BlockState replacement = pos.getY() < 0
                ? Blocks.DEEPSLATE.getDefaultState()
                : Blocks.STONE.getDefaultState();
        return world.setBlockState(pos, replacement, flags);
    }
}
