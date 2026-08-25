package net.hearthian.wetsand.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WettableBrushableBlock extends BrushableBlock implements Wettable {
    private final HumidityLevel humidityLevel;

    public WettableBrushableBlock(HumidityLevel humidityLevel, Block baseBlock, SoundEvent brushingSound, SoundEvent brushingCompleteSound, Properties settings) {
        super(baseBlock, brushingSound, brushingCompleteSound, settings);
        this.humidityLevel = humidityLevel;
    }

    protected void randomTick(@NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource random) {
        this.tickHumidity(state, world, pos);
    }

    protected boolean isRandomlyTicking(BlockState state) {
        return getIncreasedHumidityBlock(state.getBlock()).isPresent();
    }

    @Override
    public void tick(@NotNull BlockState state, ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (world.getBlockEntity(pos) instanceof BrushableBlockEntity brushableBlockEntity) {
            brushableBlockEntity.checkReset();
        }

        if (humidityLevel.ordinal() <= 1 && FallingBlock.isFree(world.getBlockState(pos.below())) && pos.getY() >= world.getMinBuildHeight()) {
            FallingBlockEntity fallingBlockEntity = FallingBlockEntity.fall(world, pos, state);
            fallingBlockEntity.disableDrop();
        }
    }

    @Override
    public HumidityLevel getHumidityLevel() {
        return humidityLevel;
    }
}