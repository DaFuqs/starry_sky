package de.dafuqs.starryskies.worldgen;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import net.minecraft.registry.tag.*;
import net.minecraft.world.gen.structure.*;

public class SphereTags {
	
	public static final TagKey<ConfiguredSphere<?, ?>> EYE_OF_ENDER_LOCATED = of("eye_of_ender_located");
	public static final TagKey<ConfiguredSphere<?, ?>> ON_OCEAN_EXPLORER_MAPS = of("on_ocean_explorer_maps");
	
	private static TagKey<ConfiguredSphere<?, ?>> of(String id) {
		return TagKey.of(StarryRegistryKeys.CONFIGURED_SPHERE, StarrySkies.id(id));
	}
	
	public static TagKey<ConfiguredSphere<?, ?>> getForVanillaStructure(TagKey<Structure> structureTag) {
		return of(structureTag.id().getPath());
	}
}
