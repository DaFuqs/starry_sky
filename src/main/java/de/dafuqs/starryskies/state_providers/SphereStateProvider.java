package de.dafuqs.starryskies.state_providers;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import net.minecraft.registry.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.gen.stateprovider.*;

public class SphereStateProvider {
	
	public static final MapCodec<BlockStateProvider> BLOCK_STATE_PROVIDER_MAP_CODEC = Registries.BLOCK_STATE_PROVIDER_TYPE.getCodec().dispatchMap(BlockStateProvider::getType, BlockStateProviderType::getCodec);
	
	public static final Codec<SphereStateProvider> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			BLOCK_STATE_PROVIDER_MAP_CODEC.forGetter((provider) -> provider.provider),
			Codec.BOOL.optionalFieldOf("reroll_for_every_pos", false).forGetter((provider) -> provider.rerollForEveryPos)
	).apply(instance, SphereStateProvider::new));
	
	private final BlockStateProvider provider;
	private final boolean rerollForEveryPos;
	
	public SphereStateProvider(BlockStateProvider provider, boolean rerollForEveryPos) {
		this.provider = provider;
		this.rerollForEveryPos = rerollForEveryPos;
	}
	
	public BlockStateProvider getForSphere(Random random, BlockPos spherePos) {
		if (rerollForEveryPos) {
			return provider;
		} else {
			return BlockStateProvider.of(provider.get(random, spherePos));
		}
	}
	
}
