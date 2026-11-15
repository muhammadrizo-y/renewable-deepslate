package net.muhammadrizo.renewable_deepslate.mixin.platform.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forge replaces the vanilla {@code LiquidBlock#shouldSpreadLiquid} call sites in
 * {@code onPlace}/{@code neighborChanged} with {@code FluidInteractionRegistry#canInteract}
 * (which both checks and performs the interactions). Hooking its head preserves the mod's
 * fizz conversions with identical trigger coverage. Targeted by name so this mod can be
 * built without Forge on the compile classpath; this mixin is only listed in the Forge
 * mixin config.
 */
@Mixin(targets = "net.minecraftforge.fluids.FluidInteractionRegistry")
public class ForgeCanInteractMixin {
    @Inject(
            method = "canInteract(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void onCanInteract(Level world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        FluidState fluidState = world.getFluidState(pos);
        if (!fluidState.is(FluidTags.LAVA)) return;
        if (pos.getY() >= 0) return;

        boolean isSoulSoil = world.getBlockState(pos.below()).is(Blocks.SOUL_SOIL);

        for (Direction direction : LiquidBlock.POSSIBLE_FLOW_DIRECTIONS) {
            BlockPos neighborPos = pos.relative(direction.getOpposite());

            if (world.getFluidState(neighborPos).is(FluidTags.WATER)) {
                Block block = fluidState.isSource()
                        ? Blocks.OBSIDIAN
                        : Blocks.COBBLED_DEEPSLATE;
                world.setBlockAndUpdate(pos, block.defaultBlockState());
                world.levelEvent(1501, pos, 0);
                cir.setReturnValue(true);
                return;
            }

            if (isSoulSoil && world.getBlockState(neighborPos).is(Blocks.BLUE_ICE)) {
                world.setBlockAndUpdate(pos, Blocks.BASALT.defaultBlockState());
                world.levelEvent(1501, pos, 0);
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
