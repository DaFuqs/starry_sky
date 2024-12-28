package de.dafuqs.starryskies.state_provider;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.data_loaders.*;
import net.minecraft.block.*;
import net.minecraft.util.dynamic.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.stateprovider.*;

import java.util.*;

public class WeightedBlockGroupBlockStateProvider extends BlockStateProvider {
	public static final MapCodec<WeightedBlockGroupBlockStateProvider> CODEC;
	private final String group;
	
	protected WeightedBlockGroupBlockStateProvider(String group) {
		this.group = group;
	}
	
	protected BlockStateProviderType<?> getType() {
		return StarryStateProviders.WEIGHTED_BLOCK_GROUP_STATE_PROVIDER;
	}
	
	public BlockState get(Random random, BlockPos pos) {
		Map<Block, Float> weightedBlocks = WeightedBlockGroupDataLoader.INSTANCE.get(group);
		return Support.getWeightedRandom(weightedBlocks, random).getDefaultState();
	}
	
	static {
		CODEC = Codecs.NON_EMPTY_STRING.fieldOf("group").xmap(WeightedBlockGroupBlockStateProvider::new, (provider) -> provider.group);
	}
}
