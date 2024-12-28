package de.dafuqs.starryskies.registries;

import de.dafuqs.starryskies.*;
import net.minecraft.registry.*;
import net.minecraft.world.biome.*;

public class StarryBiomeKeys {

	public static final RegistryKey<Biome> OVERWORLD_KEY = getBiomeKey("overworld");
	public static final RegistryKey<Biome> NETHER_KEY = getBiomeKey("nether");
	public static final RegistryKey<Biome> END_KEY = getBiomeKey("end");

	private static RegistryKey<Biome> getBiomeKey(String name) {
		return RegistryKey.of(RegistryKeys.BIOME, StarrySkies.id(name));
	}

}
