package net.hearthian.wetsand.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SugarCaneBlock.class)
public class SugarCaneBlockMixin {
    @Inject(
        method="canSurvive",
        at=@At("HEAD"),
        cancellable=true
    )
    // Note that this is a generic method, in Mixin you'll have to use
    // Object to replace type parameters
    private void canPlaceAtMixin(BlockState state, LevelReader world, BlockPos pos, CallbackInfoReturnable<Object> cir) {
        BlockState blockState = world.getBlockState(pos.below());
        if (blockState.is(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("wet-sand", "can_grow_sugar_cane")))) {
            cir.setReturnValue(true);
        }
    }
}
