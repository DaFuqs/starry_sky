package de.dafuqs.starryskies.registries;

import de.dafuqs.starryskies.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class StarryDimensionKeys {

	public static final Identifier STARRY_SKIES_DIMENSION_ID = new Identifier(StarrySkies.MOD_ID, "overworld");
	public static final Identifier STARRY_SKIES_NETHER_DIMENSION_ID = new Identifier(StarrySkies.MOD_ID, "nether");
	public static final Identifier STARRY_SKIES_END_DIMENSION_ID = new Identifier(StarrySkies.MOD_ID, "end");

	public static final RegistryKey<World> OVERWORLD_KEY = getWorld(STARRY_SKIES_DIMENSION_ID);
	public static final RegistryKey<World> NETHER_KEY = getWorld(STARRY_SKIES_NETHER_DIMENSION_ID);
	public static final RegistryKey<World> END_KEY = getWorld(STARRY_SKIES_END_DIMENSION_ID);

	private static RegistryKey<World> getWorld(Identifier id) {
		return RegistryKey.of(RegistryKeys.WORLD, id);
	}

}
