package net.muhammadrizo.renewable_deepslate.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidBlock.class)
public class FluidBlockMixin {
    @Inject(
            method = "receiveNeighborFluids",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onReceiveNeighborFluids(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!state.getFluidState().isIn(FluidTags.LAVA)) return;
        if (pos.getY() >= 0) return;

        boolean isSoulSoil = world.getBlockState(pos.down()).isOf(Blocks.SOUL_SOIL);

        for (Direction direction : FluidBlock.FLOW_DIRECTIONS) {
            BlockPos neighborPos = pos.offset(direction.getOpposite());

            if (world.getFluidState(neighborPos).isIn(FluidTags.WATER)) {
                Block block = world.getFluidState(pos).isStill()
                        ? Blocks.OBSIDIAN
                        : Blocks.COBBLED_DEEPSLATE;
                world.setBlockState(pos, block.getDefaultState());

                world.syncWorldEvent(net.minecraft.world.WorldEvents.LAVA_EXTINGUISHED, pos, 0);
                cir.setReturnValue(false);
                return;
            }

            if (isSoulSoil && world.getBlockState(neighborPos).isOf(Blocks.BLUE_ICE)) {
                world.setBlockState(pos, Blocks.BASALT.getDefaultState());
                world.syncWorldEvent(net.minecraft.world.WorldEvents.LAVA_EXTINGUISHED, pos, 0);
                cir.setReturnValue(false);
                return;
            }
        }
    }
}
