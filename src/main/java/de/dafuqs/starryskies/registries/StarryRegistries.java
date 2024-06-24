package de.dafuqs.starryskies.registries;

import de.dafuqs.starryskies.data_loaders.*;
import de.dafuqs.starryskies.dimension.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.fabricmc.fabric.api.event.registry.*;
import net.minecraft.registry.*;

public class StarryRegistries {
	
	public static final Registry<SystemGenerator> SYSTEM_GENERATOR = create(StarryRegistryKeys.SYSTEM_GENERATOR);
	public static final Registry<SpheroidTemplateType<?>> SPHEROID_TEMPLATE_TYPE = create(StarryRegistryKeys.SPHEROID_TEMPLATE_TYPE);
	public static final Registry<SpheroidFeature<?>> SPHEROID_FEATURE = create(StarryRegistryKeys.SPHEROID_FEATURE);
	
	public static void register() {
		DynamicRegistries.register(StarryRegistryKeys.STATE_PROVIDERS, StarryStateProvider.CODEC);
		DynamicRegistries.register(StarryRegistryKeys.SPHEROID_TEMPLATE, Spheroid.Template.CODEC);
		DynamicRegistries.register(StarryRegistryKeys.CONFIGURED_SPHEROID_FEATURE, ConfiguredSpheroidFeature.CODEC);
	}
	
	public static <T> Registry<T> create(RegistryKey<Registry<T>> key) {
		return FabricRegistryBuilder.createSimple(key).attribute(RegistryAttribute.MODDED).buildAndRegister();
	}
	
}
