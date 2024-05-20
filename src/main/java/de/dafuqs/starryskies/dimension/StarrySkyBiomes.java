package de.dafuqs.starryskies.dimension;

import de.dafuqs.starryskies.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.world.biome.*;

public class StarrySkyBiomes {
	
	public static final RegistryKey<Biome> OVERWORLD_KEY = getBiomeKey("overworld");
	public static final RegistryKey<Biome> NETHER_KEY = getBiomeKey("nether");
	public static final RegistryKey<Biome> END_KEY = getBiomeKey("end");
	
	private static RegistryKey<Biome> getBiomeKey(String name) {
		return RegistryKey.of(RegistryKeys.BIOME, new Identifier(StarrySkies.MOD_ID, name));
	}
	
	public static void initialize() {
	
	}
	
}
