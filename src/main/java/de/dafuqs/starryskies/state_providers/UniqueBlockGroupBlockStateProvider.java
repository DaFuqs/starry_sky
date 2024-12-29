package de.dafuqs.starryskies.state_providers;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.data_loaders.*;
import net.minecraft.block.*;
import net.minecraft.util.dynamic.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.gen.stateprovider.*;

public class UniqueBlockGroupBlockStateProvider extends BlockStateProvider {
	public static final MapCodec<UniqueBlockGroupBlockStateProvider> CODEC;
	private final String group;
	
	protected UniqueBlockGroupBlockStateProvider(String group) {
		this.group = group;
	}
	
	protected BlockStateProviderType<?> getType() {
		return StarryStateProviders.UNIQUE_BLOCK_GROUP_STATE_PROVIDER;
	}
	
	public BlockState get(Random random, BlockPos pos) {
		return UniqueBlockGroupDataLoader.INSTANCE.getEntry(group, random);
	}
	
	static {
		CODEC = Codecs.NON_EMPTY_STRING.fieldOf("group").xmap(UniqueBlockGroupBlockStateProvider::new, (provider) -> provider.group);
	}
}
