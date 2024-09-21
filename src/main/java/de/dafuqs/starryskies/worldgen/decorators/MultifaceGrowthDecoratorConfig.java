package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.*;
import net.minecraft.world.gen.feature.*;

public class MultifaceGrowthDecoratorConfig implements SphereDecoratorConfig {

	public static final Codec<MultifaceGrowthDecoratorConfig> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Registries.BLOCK.getCodec().fieldOf("block").forGetter(decorator -> decorator.featureConfig.lichen),
					RegistryCodecs.entryList(RegistryKeys.BLOCK, true).fieldOf("placeable_on_blocks").forGetter(decorator -> decorator.featureConfig.canPlaceOn),
					Codec.FLOAT.fieldOf("chance").forGetter(decorator -> decorator.chance)
			).apply(instance, MultifaceGrowthDecoratorConfig::new)
	);

	public final MultifaceGrowthFeatureConfig featureConfig;
	public final float chance;

	public MultifaceGrowthDecoratorConfig(Block block, RegistryEntryList<Block> placeableOn, float chance) {
		this.featureConfig = new MultifaceGrowthFeatureConfig((MultifaceGrowthBlock) block, 20, false, true, true, 0.5F, placeableOn);
		this.chance = chance;
	}
}
