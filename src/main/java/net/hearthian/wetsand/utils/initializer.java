package net.hearthian.wetsand.utils;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.hearthian.wetsand.blocks.*;
import net.hearthian.wetsand.mixin.block.BlockEntityTypeAccessor;
import net.hearthian.wetsand.worldgen.WetBeachFeature;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static net.hearthian.wetsand.WetSand.MOD_ID;

public class initializer {
    public static final Block SAND = new WettableFallingBlock(
            Wettable.HumidityLevel.UNAFFECTED,
            BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND)
    );
    public static final Block MOIST_SAND = new WettableFallingBlock(
            Wettable.HumidityLevel.MOIST,
            BlockBehaviour.Properties.ofFullCopy(SAND)
    );
    public static final Block WET_SAND = new WettableBlock(
            Wettable.HumidityLevel.WET,
            BlockBehaviour.Properties.ofFullCopy(SAND)
    );
    public static final Block SOAKED_SAND = new SoakedBlock(
            BlockBehaviour.Properties.ofFullCopy(SAND)
    );
    public static final Block RED_SAND = new WettableFallingBlock(
            Wettable.HumidityLevel.UNAFFECTED,
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND)
    );
    public static final Block MOIST_RED_SAND = new WettableFallingBlock(
            Wettable.HumidityLevel.MOIST,
            BlockBehaviour.Properties.ofFullCopy(RED_SAND)
    );
    public static final Block WET_RED_SAND = new WettableBlock(
            Wettable.HumidityLevel.WET,
            BlockBehaviour.Properties.ofFullCopy(RED_SAND)
    );
    public static final Block SOAKED_RED_SAND = new SoakedBlock(
            BlockBehaviour.Properties.ofFullCopy(RED_SAND)
    );

    public static final Block SUSPICIOUS_SAND = new WettableBrushableBlock(
            Wettable.HumidityLevel.UNAFFECTED, SAND, SoundEvents.BRUSH_SAND, SoundEvents.BRUSH_SAND,
            BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.SNARE).strength(0.25F).sound(SoundType.SUSPICIOUS_SAND).pushReaction(PushReaction.DESTROY)
    );
    public static final Block MOIST_SUSPICIOUS_SAND = new WettableBrushableBlock(
            Wettable.HumidityLevel.MOIST, MOIST_SAND, SoundEvents.BRUSH_SAND, SoundEvents.BRUSH_SAND,
            BlockBehaviour.Properties.ofFullCopy(SUSPICIOUS_SAND)
    );
    public static final Block WET_SUSPICIOUS_SAND = new WettableBrushableBlock(
            Wettable.HumidityLevel.WET, WET_SAND, SoundEvents.BRUSH_SAND, SoundEvents.BRUSH_SAND,
            BlockBehaviour.Properties.ofFullCopy(SUSPICIOUS_SAND)
    );
    public static final Block SOAKED_SUSPICIOUS_SAND = new SoakedBrushableBlock(
            SOAKED_SAND, SoundEvents.BRUSH_SAND, SoundEvents.BRUSH_SAND,
            BlockBehaviour.Properties.ofFullCopy(SUSPICIOUS_SAND)
    );

    private static void registerBlockItem(String path, Block block) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, path);

        Registry.register(BuiltInRegistries.BLOCK, id, block);
        Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, new Item.Properties()));
    }

    public static void initBlockItems() {
        registerBlockItem("moist_sand", MOIST_SAND);
        registerBlockItem("wet_sand", WET_SAND);
        registerBlockItem("soaked_sand", SOAKED_SAND);
        registerBlockItem("moist_red_sand", MOIST_RED_SAND);
        registerBlockItem("wet_red_sand", WET_RED_SAND);
        registerBlockItem("soaked_red_sand", SOAKED_RED_SAND);
        registerBlockItem("moist_suspicious_sand", MOIST_SUSPICIOUS_SAND);
        registerBlockItem("wet_suspicious_sand", WET_SUSPICIOUS_SAND);
        registerBlockItem("soaked_suspicious_sand", SOAKED_SUSPICIOUS_SAND);

    }

    /** Lets the brushable block entity live in the moist, wet and soaked suspicious sands. */
    public static void initBrushableBlocks() {
        BlockEntityTypeAccessor accessor = (BlockEntityTypeAccessor) BlockEntityType.BRUSHABLE_BLOCK;
        Set<Block> validBlocks = new HashSet<>(accessor.wet_sand$getValidBlocks());

        Collections.addAll(validBlocks, MOIST_SUSPICIOUS_SAND, WET_SUSPICIOUS_SAND, SOAKED_SUSPICIOUS_SAND);
        accessor.wet_sand$setValidBlocks(Set.copyOf(validBlocks));
    }

    /** Wets the sand around water at generation, instead of leaving it all to the random ticks. */
    public static void initWorldGen() {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, "wet_beach");

        Registry.register(BuiltInRegistries.FEATURE, id, new WetBeachFeature(NoneFeatureConfiguration.CODEC));
        BiomeModifications.addFeature(
            BiomeSelectors.foundInOverworld(),
            GenerationStep.Decoration.TOP_LAYER_MODIFICATION,
            ResourceKey.create(Registries.PLACED_FEATURE, id)
        );
    }

    public static void initCreativePlacement() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(content -> {
            content.addAfter(Items.SAND, MOIST_SAND);
            content.addAfter(MOIST_SAND, WET_SAND);
            content.addAfter(WET_SAND, SOAKED_SAND);
            content.addAfter(Items.RED_SAND, MOIST_RED_SAND);
            content.addAfter(MOIST_RED_SAND, WET_RED_SAND);
            content.addAfter(WET_RED_SAND, SOAKED_RED_SAND);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(content -> {
            content.addAfter(Items.SUSPICIOUS_SAND, MOIST_SUSPICIOUS_SAND);
            content.addAfter(MOIST_SUSPICIOUS_SAND, WET_SUSPICIOUS_SAND);
            content.addAfter(WET_SUSPICIOUS_SAND, SOAKED_SUSPICIOUS_SAND);
        });
    }
}
