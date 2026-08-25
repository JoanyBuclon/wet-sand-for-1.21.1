package net.hearthian.wetsand.mixin.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

/**
 * Opens the valid block set of a block entity type, so the mod can add its own blocks to a
 * vanilla type instead of rebuilding the set and dropping what other mods added.
 */
@Mixin(BlockEntityType.class)
public interface BlockEntityTypeAccessor {
    @Accessor("validBlocks")
    Set<Block> wet_sand$getValidBlocks();

    @Mutable
    @Accessor("validBlocks")
    void wet_sand$setValidBlocks(Set<Block> validBlocks);
}
