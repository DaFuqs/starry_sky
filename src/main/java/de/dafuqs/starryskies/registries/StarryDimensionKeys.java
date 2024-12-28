package de.dafuqs.starryskies.registries;

import de.dafuqs.starryskies.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class StarryDimensionKeys {
	
	public static final BlockPos STARRY_END_SPAWN_BLOCK_POS = new BlockPos(10, 64, 0);
	public static final BlockPos STARRY_OVERWORLD_SPAWN_BLOCK_POS = new BlockPos(16, 85, 16);
	
	
	public static final Identifier STARRY_SKIES_DIMENSION_ID = StarrySkies.id("overworld");
	public static final Identifier STARRY_SKIES_NETHER_DIMENSION_ID = StarrySkies.id("nether");
	public static final Identifier STARRY_SKIES_END_DIMENSION_ID = StarrySkies.id("end");

	public static final RegistryKey<World> OVERWORLD_KEY = getWorld(STARRY_SKIES_DIMENSION_ID);
	public static final RegistryKey<World> NETHER_KEY = getWorld(STARRY_SKIES_NETHER_DIMENSION_ID);
	public static final RegistryKey<World> END_KEY = getWorld(STARRY_SKIES_END_DIMENSION_ID);

	private static RegistryKey<World> getWorld(Identifier id) {
		return RegistryKey.of(RegistryKeys.WORLD, id);
	}

}
