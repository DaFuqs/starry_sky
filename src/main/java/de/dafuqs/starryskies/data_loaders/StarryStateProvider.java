package de.dafuqs.starryskies.data_loaders;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import net.minecraft.block.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.stateprovider.*;

public record StarryStateProvider(BlockStateProvider provider) {
	
	public static final Codec<StarryStateProvider> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					BlockStateProvider.TYPE_CODEC.fieldOf("states").forGetter(group -> group.provider)
			).apply(instance, StarryStateProvider::new)
	);
	
	public static BlockState getRandomState(DynamicRegistryManager registryManager, Identifier groupId, BlockPos pos, Random random) {
		Registry<StarryStateProvider> registry = registryManager.get(StarryRegistryKeys.STATE_PROVIDERS);
		StarryStateProvider provider = registry.get(groupId);
		if (provider == null) {
			StarrySkies.LOGGER.warn("Referencing empty/non-existing WeightedBlockGroup: {}. Using AIR instead.", groupId);
			return Blocks.AIR.getDefaultState();
		}
		return provider.provider.get(random, pos);
	}
	
}
