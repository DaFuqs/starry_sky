package de.dafuqs.starryskies.spheroids;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.dimension.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.world.gen.feature.*;

public class DecoratorFeatures {
	
	public static Identifier SPHEROID_DECORATOR_FEATURE_ID = StarrySkies.locate("spheroid_decorator");
	public static Feature<DefaultFeatureConfig> SPHEROID_DECORATOR_FEATURE;
	
	public static void initialize() {
		SPHEROID_DECORATOR_FEATURE = Registry.register(Registries.FEATURE, SPHEROID_DECORATOR_FEATURE_ID, new SpheroidDecoratorFeature(DefaultFeatureConfig.CODEC));
	}
}