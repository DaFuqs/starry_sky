package de.dafuqs.starryskies.worldgen;

import de.dafuqs.starryskies.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.world.gen.feature.*;

public class StarryFeatures {

	public static Identifier SPHEROID_DECORATOR_FEATURE_ID = StarrySkies.locate("spheroid_decorator");
	public static Feature<DefaultFeatureConfig> SPHEROID_DECORATOR_FEATURE;

	public static void initialize() {
		SPHEROID_DECORATOR_FEATURE = Registry.register(Registries.FEATURE, SPHEROID_DECORATOR_FEATURE_ID, new SphereDecorator(DefaultFeatureConfig.CODEC));
	}
}