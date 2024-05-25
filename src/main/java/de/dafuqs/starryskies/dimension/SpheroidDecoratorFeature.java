package de.dafuqs.starryskies.dimension;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.util.math.*;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.util.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class SpheroidDecoratorFeature extends Feature<DefaultFeatureConfig> {
	
	public SpheroidDecoratorFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(DefaultFeatureConfig.CODEC);
	}
	
	@Override
	public boolean generate(@NotNull FeatureContext featureContext) {
		if (featureContext.getGenerator() instanceof StarrySkyChunkGenerator) {
			SystemGenerator systemGenerator = SystemGenerator.getSystemGeneratorOfWorld(featureContext.getWorld().toServerWorld().getRegistryKey());
			List<Spheroid> localSystem = systemGenerator.getSystemAtChunkPos(featureContext.getOrigin().getX() / 16, featureContext.getOrigin().getZ() / 16);
			for (Spheroid spheroid : localSystem) {
				if (spheroid.isInChunk(new ChunkPos(featureContext.getOrigin()))) {

                    StarrySkies.LOGGER.debug("Decorating spheroid at x:{} z:{}{}", featureContext.getOrigin().getX(), featureContext.getOrigin().getZ(), spheroid.getDescription());
					spheroid.decorate(featureContext.getWorld(), featureContext.getOrigin(), featureContext.getRandom());
					StarrySkies.LOGGER.debug("Finished decorating.");
				}
			}
		}
		return false;
	}
	
}
