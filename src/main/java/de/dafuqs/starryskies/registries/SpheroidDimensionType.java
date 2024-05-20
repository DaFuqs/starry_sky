package de.dafuqs.starryskies.registries;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.dimension.*;
import net.minecraft.block.*;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.*;
import net.minecraft.util.*;
import net.minecraft.world.biome.*;

import java.util.*;

public enum SpheroidDimensionType {
	OVERWORLD,
	NETHER,
	END;
	
	public static SpheroidDimensionType of(String s) {
		try {
			return valueOf(s.toUpperCase(Locale.ROOT));
		} catch (Throwable t) {
			StarrySkies.log(Level.WARN, "Dimension type of `" + s + "` was requested, but it does not exist. Falling back to OVERWORLD");
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
