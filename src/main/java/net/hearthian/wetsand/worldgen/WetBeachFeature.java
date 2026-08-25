package net.hearthian.wetsand.worldgen;

import com.mojang.serialization.Codec;
import net.hearthian.wetsand.blocks.Wettable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Wets the sand around water when a chunk is generated, so a shoreline reads right from the start
 * instead of waiting for the random ticks to walk the humidity outwards.
 * <p>
 * Applies the same rule as {@link Wettable#tryDrench}, the closer the water the wetter the sand.
 * Runs after the terrain and the water, so it works around any water body and under any terrain
 * mod, unlike a surface rule that only knows the water height of its own column.
 */
public class WetBeachFeature extends Feature<NoneFeatureConfiguration> {
    public WetBeachFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(@NotNull FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        boolean wetted = false;

        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;
                pos.set(x, level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z) - 1, z);

                BlockState state = level.getBlockState(pos);
                // Suspicious sand keeps its loot in a block entity. Leave it to the random tick,
                // which knows how to carry the item across the block swap.
                if (state.hasBlockEntity()
                    || !(state.getBlock() instanceof Wettable wettable)
                    || wettable.getHumidityLevel() != Wettable.HumidityLevel.UNAFFECTED) {
                    continue;
                }

                Block drenched = drench(wettable, state.getBlock(), waterDistance(level, pos));
                if (drenched != state.getBlock()) {
                    setBlock(level, pos.immutable(), drenched.withPropertiesOf(state));
                    wetted = true;
                }
            }
        }

        return wetted;
    }

    /** Chebyshev distance to the closest water in range, or 0 when there is none. */
    private static int waterDistance(WorldGenLevel level, BlockPos pos) {
        // ponytail: 7x7x7 scan per surface column, invert to a water-first sweep if chunk gen drags.
        return BlockPos.findClosestMatch(pos, Wettable.HUMIDITY_RANGE, Wettable.HUMIDITY_RANGE,
                candidate -> level.getFluidState(candidate).is(Fluids.WATER))
            .map(water -> Wettable.chessboardDistance(water, pos))
            .orElse(0);
    }

    /** Climbs the humidity chain, one step per block of distance saved, as the random tick would. */
    private static Block drench(Wettable wettable, Block block, int distance) {
        int steps = distance == 0 ? 0 : Wettable.HUMIDITY_RANGE - distance + 1;

        for (int step = 0; step < steps; step++) {
            Optional<Block> wetter = wettable.getIncreasedHumidityBlock(block);
            if (wetter.isEmpty()) break;
            block = wetter.get();
        }

        return block;
    }
}
