package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.worldgen.*;

public class ChorusFruitDecoratorConfig implements SphereDecoratorConfig {

	public static Codec<ChorusFruitDecoratorConfig> CODEC = Codec.unit(ChorusFruitDecoratorConfig::new);

	public final float chorusChance;

	// TODO: make configurable
	public ChorusFruitDecoratorConfig() {
		this.chorusChance = 0.03F;
	}

}
