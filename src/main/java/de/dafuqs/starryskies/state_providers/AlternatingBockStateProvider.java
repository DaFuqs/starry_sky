package de.dafuqs.starryskies.state_providers;

import com.mojang.serialization.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.stateprovider.*;

import java.util.*;

public class AlternatingBockStateProvider extends BlockStateProvider {
	
	public static final MapCodec<AlternatingBockStateProvider> CODEC;
	private final List<BlockState> states;
	
	protected AlternatingBockStateProvider(List<BlockState> states) {
		this.states = states;
	}
	
	protected BlockStateProviderType<?> getType() {
		return StarryStateProviders.ALTERNATING_STATE_PROVIDER;
	}
	
	public BlockState get(Random random, BlockPos pos) {
		int sum = Math.abs(pos.getX()) + Math.abs(pos.getY()) + Math.abs(pos.getZ());
		int mod = sum % this.states.size();
		return states.get(mod);
	}
	
	static {
		CODEC = BlockState.CODEC.listOf().fieldOf("states").xmap(AlternatingBockStateProvider::new, (provider) -> provider.states);
	}
}
