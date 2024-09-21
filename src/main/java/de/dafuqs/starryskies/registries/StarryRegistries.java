package de.dafuqs.starryskies.registries;

import de.dafuqs.starryskies.data_loaders.*;
import de.dafuqs.starryskies.worldgen.SphereDecorator;
import de.dafuqs.starryskies.worldgen.*;
import de.dafuqs.starryskies.worldgen.dimension.*;
import net.fabricmc.fabric.api.event.registry.*;
import net.minecraft.registry.*;

public class StarryRegistries {

	public static final Registry<SystemGenerator> SYSTEM_GENERATOR = create(StarryRegistryKeys.SYSTEM_GENERATOR);
	public static final Registry<Spheres<?>> SPHERE = create(StarryRegistryKeys.SPHERE);
	public static final Registry<SphereDecorator<?>> SPHERE_DECORATOR = create(StarryRegistryKeys.SPHERE_DECORATOR);

	public static void register() {
		DynamicRegistries.register(StarryRegistryKeys.STATE_PROVIDER, StarryStateProvider.CODEC);
		DynamicRegistries.register(StarryRegistryKeys.CONFIGURED_SPHERE, ConfiguredSphere.CODEC);
		DynamicRegistries.register(StarryRegistryKeys.CONFIGURED_SPHERE_DECORATOR, ConfiguredSphereDecorator.CODEC);
	}

	public static <T> Registry<T> create(RegistryKey<Registry<T>> key) {
		return FabricRegistryBuilder.createSimple(key).attribute(RegistryAttribute.MODDED).buildAndRegister();
	}

}
