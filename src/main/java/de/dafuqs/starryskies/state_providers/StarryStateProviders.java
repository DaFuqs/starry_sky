package de.dafuqs.starryskies.state_providers;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.*;
import net.minecraft.registry.*;
import net.minecraft.world.gen.stateprovider.*;

public class StarryStateProviders {
	
	public static final BlockStateProviderType<WeightedBlockGroupBlockStateProvider> WEIGHTED_BLOCK_GROUP_STATE_PROVIDER = register("weighted_block_group_state_provider", WeightedBlockGroupBlockStateProvider.CODEC);
	public static final BlockStateProviderType<UniqueBlockGroupBlockStateProvider> UNIQUE_BLOCK_GROUP_STATE_PROVIDER = register("unique_block_group_state_provider", UniqueBlockGroupBlockStateProvider.CODEC);
	
	private static <P extends BlockStateProvider> BlockStateProviderType<P> register(String name, MapCodec<P> codec) {
		return Registry.register(Registries.BLOCK_STATE_PROVIDER_TYPE, StarrySkies.id(name), new BlockStateProviderType<>(codec));
	}
	
	public static void register() {
	
	}
	
}
