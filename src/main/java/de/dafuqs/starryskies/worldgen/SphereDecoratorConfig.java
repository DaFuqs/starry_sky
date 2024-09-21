package de.dafuqs.starryskies.worldgen;

import com.mojang.serialization.*;

public interface SphereDecoratorConfig {

	DefaultSphereDecoratorConfig DEFAULT = DefaultSphereDecoratorConfig.INSTANCE;

	class DefaultSphereDecoratorConfig implements SphereDecoratorConfig {

		public static final DefaultSphereDecoratorConfig INSTANCE = new DefaultSphereDecoratorConfig();
		public static final Codec<DefaultSphereDecoratorConfig> CODEC = Codec.unit(() -> INSTANCE);

		public DefaultSphereDecoratorConfig() {
		}

	}

}
