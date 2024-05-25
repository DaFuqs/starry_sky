package de.dafuqs.starryskies.registries;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.dimension.*;
import net.minecraft.block.*;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.*;
import net.minecraft.util.*;
import net.minecraft.world.biome.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

// TOOD: Note to Daf: Make this dynamic, anyhow.
// I [the writer of this note] do not envision any way you would make this dynamic, so I leave this task to you.

public enum SpheroidDimensionType {
	OVERWORLD(StarrySkies.locate("overworld")),
	NETHER(StarrySkies.locate("nether")),
	END(StarrySkies.locate("nether"));

	private final @NotNull Identifier id;

	SpheroidDimensionType(@NotNull Identifier id) {
		this.id = id;
	}

	private static void register(SpheroidDimensionType type) {
		Registry.register(StarryRegistries.STARRY_DIMENSION_TYPE, type.id, type);
	}

	public static void initialize() {
		register(OVERWORLD);
		register(NETHER);
		register(END);
	}

	public @NotNull Identifier id() {
		return this.id;
	}
	
	public static SpheroidDimensionType of(String s) {
		try {
			return valueOf(s.toUpperCase(Locale.ROOT));
		} catch (Throwable t) {
            StarrySkies.LOGGER.warn("Dimension type of `{}` was requested, but it does not exist. Falling back to OVERWORLD", s);
			return SpheroidDimensionType.OVERWORLD;
		}
	}
	
	public int getFloorHeight() {
		switch (this) {
			case OVERWORLD -> {
				return StarrySkies.CONFIG.floorHeightOverworld;
			}
			case NETHER -> {
				return StarrySkies.CONFIG.floorHeightNether;
			}
			default -> {
				return StarrySkies.CONFIG.floorHeightEnd;
			}
		}
	}
	
	public BlockState getFloorBlockState() {
		switch (this) {
			case OVERWORLD -> {
				return Registries.BLOCK.get(new Identifier(StarrySkies.CONFIG.floorBlockOverworld.toLowerCase())).getDefaultState();
			}
			case NETHER -> {
				return Registries.BLOCK.get(new Identifier(StarrySkies.CONFIG.floorBlockNether.toLowerCase())).getDefaultState();
			}
			default -> {
				return Registries.BLOCK.get(new Identifier(StarrySkies.CONFIG.floorBlockEnd.toLowerCase())).getDefaultState();
			}
		}
	}
	
	public BlockState getBottomBlockState() {
		switch (this) {
			case OVERWORLD -> {
				return Registries.BLOCK.get(new Identifier(StarrySkies.CONFIG.bottomBlockOverworld.toLowerCase())).getDefaultState();
			}
			case NETHER -> {
				return Registries.BLOCK.get(new Identifier(StarrySkies.CONFIG.bottomBlockNether.toLowerCase())).getDefaultState();
			}
			default -> {
				return Registries.BLOCK.get(new Identifier(StarrySkies.CONFIG.bottomBlockEnd.toLowerCase())).getDefaultState();
			}
		}
	}
	
	public RegistryEntry<Biome> getBiome(Registry<Biome> biomeRegistry) {
		switch (this) {
			case OVERWORLD -> {
				return biomeRegistry.entryOf(StarrySkyBiomes.OVERWORLD_KEY);
			}
			case NETHER -> {
				return biomeRegistry.entryOf(StarrySkyBiomes.NETHER_KEY);
			}
			default -> {
				return biomeRegistry.entryOf(StarrySkyBiomes.END_KEY);
			}
		}
	}
}
