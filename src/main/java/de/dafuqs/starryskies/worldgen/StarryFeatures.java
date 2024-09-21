package de.dafuqs.starryskies.worldgen;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.dimension.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.world.gen.feature.*;

public class StarryFeatures {

	public static Identifier SPHERE_DECORATOR_FEATURE_ID = StarrySkies.locate("sphere_decoration");
	public static Feature<DefaultFeatureConfig> SPHERE_DECORATION;

	public static void initialize() {
		SPHERE_DECORATION = Registry.register(Registries.FEATURE, SPHERE_DECORATOR_FEATURE_ID, new SphereDecorationFeature(DefaultFeatureConfig.CODEC));
	}
}