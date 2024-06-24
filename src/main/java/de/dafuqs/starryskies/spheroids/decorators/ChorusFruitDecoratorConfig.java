package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.spheroids.decoration.*;

public class ChorusFruitDecoratorConfig implements SpheroidFeatureConfig {
	
	public static Codec<ChorusFruitDecoratorConfig> CODEC = Codec.unit(ChorusFruitDecoratorConfig::new);
	
	public final float chorusChance;
	
	// TODO: make configurable
	public ChorusFruitDecoratorConfig() {
		this.chorusChance = 0.03F;
	}
	
}
