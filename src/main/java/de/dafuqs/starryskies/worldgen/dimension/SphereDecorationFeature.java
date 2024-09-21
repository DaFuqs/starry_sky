package de.dafuqs.starryskies.worldgen.dimension;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.util.math.*;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.util.*;
import org.jetbrains.annotations.*;

public class SphereDecorationFeature extends Feature<DefaultFeatureConfig> {

	public SphereDecorationFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec);
	}

	@Override
	public boolean generate(@NotNull FeatureContext featureContext) {
		if (featureContext.getGenerator() instanceof StarrySkyChunkGenerator starrySkyChunkGenerator) {
			SystemGenerator systemGenerator = starrySkyChunkGenerator.getSystemGenerator();

			for (PlacedSphere sphere : systemGenerator.getSystem(featureContext.getWorld(), featureContext.getOrigin())) {
				if (sphere.isInChunk(new ChunkPos(featureContext.getOrigin()))) {
					sphere.decorate(featureContext.getWorld(), featureContext.getOrigin(), featureContext.getRandom());
				}
			}
		}
		return false;
	}

}
