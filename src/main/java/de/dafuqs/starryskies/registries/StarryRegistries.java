package de.dafuqs.starryskies.registries;

import de.dafuqs.starryskies.data_loaders.*;
import de.dafuqs.starryskies.dimension.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.fabricmc.fabric.api.event.registry.*;
import net.minecraft.registry.*;

public class StarryRegistries {
	public static final Registry<SystemGenerator> SYSTEM_GENERATOR = create(StarryRegistryKeys.SYSTEM_GENERATOR);
	//public static final ResettableRegistry<SpheroidDistributionTypeLoader.SpheroidDistributionType> SPHEROID_DISTRIBUTION_TYPE = createReloadable(StarryRegistryKeys.SPHEROID_DISTRIBUTION_TYPE);
	
	public static final Registry<SpheroidFeature<?>> SPHEROID_FEATURE = create(StarryRegistryKeys.SPHEROID_FEATURE);
	public static final Registry<ConfiguredSpheroidFeature<?, ?>> CONFIGURED_SPHEROID_FEATURE = create(StarryRegistryKeys.CONFIGURED_SPHEROID_FEATURE);
	
	public static final Registry<SpheroidTemplateType<?>> SPHEROID_TEMPLATE_TYPE = create(StarryRegistryKeys.SPHEROID_TEMPLATE_TYPE);
	public static final Registry<Spheroid.Template<?>> SPHEROID_TEMPLATE = create(StarryRegistryKeys.SPHEROID_TEMPLATE);
	
	public static final Registry<UniqueBlockGroupsLoader.UniqueBlockGroup> UNIQUE_BLOCK_GROUP = create(StarryRegistryKeys.UNIQUE_BLOCK_GROUP);
	public static final Registry<WeightedBlockGroupsLoader.WeightedBlockGroup> WEIGHTED_BLOCK_GROUP = create(StarryRegistryKeys.WEIGHTED_BLOCK_GROUP);
	
	public static void register() {
		DynamicRegistries.register(StarryRegistryKeys.CONFIGURED_SPHEROID_FEATURE, CONFIGURED_SPHEROID_FEATURE.getCodec());
		DynamicRegistries.register(StarryRegistryKeys.SPHEROID_TEMPLATE, SPHEROID_TEMPLATE.getCodec());
		DynamicRegistries.register(StarryRegistryKeys.UNIQUE_BLOCK_GROUP, UNIQUE_BLOCK_GROUP.getCodec());
		DynamicRegistries.register(StarryRegistryKeys.WEIGHTED_BLOCK_GROUP, WEIGHTED_BLOCK_GROUP.getCodec());
	}
	
	public static <T> Registry<T> create(RegistryKey<Registry<T>> key) {
		return FabricRegistryBuilder.createSimple(key).attribute(RegistryAttribute.MODDED).buildAndRegister();
	}
	
}
