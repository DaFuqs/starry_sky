package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import net.minecraft.block.*;

public class PlantAroundPondDecoratorConfig implements SpheroidFeatureConfig {
	
	public static final Codec<PlantAroundPondDecoratorConfig> CODEC = Codec.unit(PlantAroundPondDecoratorConfig::new);
	public static final int pond_tries = 3;
	public static final float plant_chance = 0.5F;
	public static final int minHeight = 1;
	public static final int maxHeight = 3;
	public final BlockState block = Blocks.SUGAR_CANE.getDefaultState();
	
	// TODO: make configurable
	public PlantAroundPondDecoratorConfig() {
		super();
	}
}
