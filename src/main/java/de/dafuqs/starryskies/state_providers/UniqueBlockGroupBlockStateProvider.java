package de.dafuqs.starryskies.state_providers;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.data_loaders.*;
import net.minecraft.block.*;
import net.minecraft.util.dynamic.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.stateprovider.*;

import java.util.*;

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
		Block block = UniqueBlockGroupDataLoader.INSTANCE.get(group);
		if (block == null) {
			StarrySkies.LOGGER.warn("Trying to query a nonexistent UniqueBlockGroup: {}", group);
			StarrySkies.LOGGER.error(Arrays.toString(Thread.currentThread().getStackTrace()));
			return Blocks.AIR.getDefaultState();
		}
		return block.getDefaultState();
	}
	
	static {
		CODEC = Codecs.NON_EMPTY_STRING.fieldOf("group").xmap(UniqueBlockGroupBlockStateProvider::new, (provider) -> provider.group);
	}
}
