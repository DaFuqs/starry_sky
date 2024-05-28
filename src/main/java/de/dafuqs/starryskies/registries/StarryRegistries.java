package de.dafuqs.starryskies.registries;

import com.mojang.serialization.Lifecycle;
import de.dafuqs.starryskies.data_loaders.*;
import de.dafuqs.starryskies.spheroids.spheroids.Spheroid;
import net.fabricmc.fabric.api.event.registry.*;
import net.minecraft.registry.*;

public class StarryRegistries {
	public static final Registry<SpheroidDimensionType> STARRY_DIMENSION_TYPE = create(StarryRegistryKeys.STARRY_DIMENSION_TYPE);
	public static final ResettableRegistry<SpheroidDistributionLoader.SpheroidDistributionType> SPHEROID_DISTRIBUTION_TYPE = createReloadable(StarryRegistryKeys.SPHEROID_DISTRIBUTION_TYPE);

	public static final Registry<SpheroidDecoratorType<?>> SPHEROID_DECORATOR_TYPE = create(StarryRegistryKeys.SPHEROID_DECORATOR_TYPE);
	public static final ResettableRegistry<SpheroidDecorator> SPHEROID_DECORATOR = createReloadable(StarryRegistryKeys.SPHEROID_DECORATOR);

	public static final Registry<SpheroidTemplateType<?>> SPHEROID_TEMPLATE_TYPE = create(StarryRegistryKeys.SPHEROID_TEMPLATE_TYPE);
	public static final ResettableRegistry<Spheroid.Template<?>> SPHEROID_TEMPLATE = createReloadable(StarryRegistryKeys.SPHEROID_TEMPLATE);
	
	public static final ResettableRegistry<UniqueBlockGroupsLoader.UniqueBlockGroup> UNIQUE_BLOCK_GROUP = createReloadable(StarryRegistryKeys.UNIQUE_BLOCK_GROUP);
	public static final ResettableRegistry<WeightedBlockGroupsLoader.WeightedBlockGroup> WEIGHTED_BLOCK_GROUP = createReloadable(StarryRegistryKeys.WEIGHTED_BLOCK_GROUP);
	
	public static void register() {
	
	}
	
	public static <T> Registry<T> create(RegistryKey<Registry<T>> key) {
		return FabricRegistryBuilder.createSimple(key).attribute(RegistryAttribute.MODDED).buildAndRegister();
	}

	public static <T> ResettableRegistry<T> createReloadable(RegistryKey<Registry<T>> key) {
		return createResettable(key).attribute(RegistryAttribute.MODDED).buildAndRegister();
	}

	private static <T> FabricRegistryBuilder<T, ResettableRegistry<T>> createResettable(RegistryKey<Registry<T>> key) {
		return FabricRegistryBuilder.from(new ResettableRegistry<>(key, Lifecycle.experimental()));
	}
	
}
