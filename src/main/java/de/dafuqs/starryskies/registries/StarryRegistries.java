package de.dafuqs.starryskies.registries;

import de.dafuqs.starryskies.worldgen.*;
import de.dafuqs.starryskies.worldgen.dimension.*;
import net.fabricmc.fabric.api.event.registry.*;
import net.minecraft.registry.*;

public class StarryRegistries {

	public static final Registry<Sphere<?>> SPHERE = create(StarryRegistryKeys.SPHERE);
	public static final Registry<SphereDecorator<?>> SPHERE_DECORATOR = create(StarryRegistryKeys.SPHERE_DECORATOR);

	public static void register() {
		DynamicRegistries.register(StarryRegistryKeys.SYSTEM_GENERATOR, SystemGenerator.CODEC);
		DynamicRegistries.register(StarryRegistryKeys.GENERATION_GROUP, GenerationGroup.CODEC);
		DynamicRegistries.register(StarryRegistryKeys.CONFIGURED_SPHERE, ConfiguredSphere.CODEC);
		DynamicRegistries.register(StarryRegistryKeys.CONFIGURED_SPHERE_DECORATOR, ConfiguredSphereDecorator.CODEC);
	}

	public static <T> Registry<T> create(RegistryKey<Registry<T>> key) {
		return FabricRegistryBuilder.createSimple(key).attribute(RegistryAttribute.MODDED).buildAndRegister();
	}

}
