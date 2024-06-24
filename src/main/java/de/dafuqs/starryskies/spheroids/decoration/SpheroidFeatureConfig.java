package de.dafuqs.starryskies.spheroids.decoration;

import com.mojang.serialization.*;

public interface SpheroidFeatureConfig {
	
	DefaultSpheroidFeatureConfig DEFAULT = DefaultSpheroidFeatureConfig.INSTANCE;
	
	class DefaultSpheroidFeatureConfig implements SpheroidFeatureConfig {
		
		public static final DefaultSpheroidFeatureConfig INSTANCE = new DefaultSpheroidFeatureConfig();
		public static final Codec<DefaultSpheroidFeatureConfig> CODEC = Codec.unit(() -> INSTANCE);
		
		public DefaultSpheroidFeatureConfig() {
		}
		
	}
	
}
