package de.dafuqs.starryskies.spheroids;

import de.dafuqs.starryskies.StarrySkies;
import de.dafuqs.starryskies.spheroids.spheroids.SimpleSpheroid;
import de.dafuqs.starryskies.spheroids.spheroids.Spheroid;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class StarryRegistries {
	
	public static final RegistryKey<Registry<Class<? extends Spheroid.Template>>> SPHEROID_TYPE_KEY = StarryRegistries.createRegistryKey("spheroid_type");
	public static final Registry<Class<? extends Spheroid.Template>> SPHEROID_TYPE = Registry.create(SPHEROID_TYPE_KEY, registry -> SimpleSpheroid.Template.class);
	
	public static final RegistryKey<Registry<SpheroidDecorator>> SPHEROID_DECORATOR_KEY = StarryRegistries.createRegistryKey("spheroid_decorator");
	public static final Registry<SpheroidDecorator> SPHEROID_DECORATOR = Registry.create(SPHEROID_DECORATOR_KEY, registry -> SpheroidDecorators.CACTUS);

	private static <T> RegistryKey<Registry<T>> createRegistryKey(String registryId) {
		return RegistryKey.ofRegistry(new Identifier(StarrySkies.MOD_ID, registryId));
	}
	
}
