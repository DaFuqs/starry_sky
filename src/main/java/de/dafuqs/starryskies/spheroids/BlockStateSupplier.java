package de.dafuqs.starryskies.spheroids;

import com.mojang.brigadier.exceptions.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.data_loaders.*;
import net.minecraft.block.*;
import net.minecraft.command.argument.*;
import net.minecraft.util.*;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.*;

import java.util.*;

import static de.dafuqs.starryskies.Support.*;

public abstract sealed class BlockStateSupplier {
	
	public static Codec<BlockStateSupplier> CODEC = new Codec<>() {
		private static final Codec<List<BlockState>> LIST_CODEC = BLOCKSTATE_STRING_CODEC.listOf();
		private static final Codec<Map<BlockState, Float>> MAP_CODEC = Codec.unboundedMap(BLOCKSTATE_STRING_CODEC, Codec.FLOAT);
		
		@Override
		public <T> DataResult<Pair<BlockStateSupplier, T>> decode(DynamicOps<T> ops, T input) {
			var str = Codec.STRING.parse(ops, input);
			if (str.isSuccess()) {
				try {
					var id = str.getOrThrow();
					return DataResult.success(Pair.of(BlockStateSupplier.of(id), ops.empty()));
				} catch (Exception e) {
					return DataResult.error(() -> "Failed to parse block state supplier: " + e);
				}
			}
			
			var list = LIST_CODEC.parse(ops, input);
			if (list.isSuccess()) return list.map(l -> Pair.of(BlockStateSupplier.of(l), ops.empty()));
			
			return MAP_CODEC.parse(ops, input).map(m -> Pair.of(BlockStateSupplier.of(m), ops.empty()));
		}
		
		@Override
		public <T> DataResult<T> encode(BlockStateSupplier input, DynamicOps<T> ops, T prefix) {
			T stringResult = switch (input) {
				case SingleBlockStateSupplier single ->
						ops.createString(BlockArgumentParser.stringifyBlockState(single.state));
				case WeightedBlockStateGroupSupplier weighted -> ops.createString("%" + weighted.identifier);
				case UniqueBlockStateGroupSupplier unique -> ops.createString("$" + unique.identifier);
				default -> null;
			};
			if (stringResult != null) return DataResult.success(stringResult);
			if (input instanceof WeightedBlockStateSupplier weighted)
				return MAP_CODEC.encode(weighted.states, ops, prefix);
			if (input instanceof BlockStateListSupplier list) return LIST_CODEC.encode(list.states, ops, prefix);
			return DataResult.error(() -> "Unknown BlockStateSupplier type");
		}
	};
	
	public static @NotNull BlockStateSupplier of(String id) throws CommandSyntaxException {
		if (id.startsWith("$")) {
			return new WeightedBlockStateGroupSupplier(id);
		} else if (id.startsWith("%")) {
			return new UniqueBlockStateGroupSupplier(id);
		} else {
			return new SingleBlockStateSupplier(id);
		}
	}
	
	public static @NotNull BlockStateSupplier of(List<BlockState> states) {
		return new BlockStateListSupplier(states);
	}
	
	public static @NotNull BlockStateSupplier of(@NotNull Map<BlockState, Float> map) {
		return new WeightedBlockStateSupplier(map);
	}
	
	public abstract BlockState get(Random random);
	
	public static final class SingleBlockStateSupplier extends BlockStateSupplier {
		
		BlockState state;
		
		public SingleBlockStateSupplier(@NotNull String str) throws CommandSyntaxException {
			state = StarrySkies.getStateFromString(str);
		}
		
		public BlockState get(Random random) {
			return state;
		}
		
	}
	
	public static final class BlockStateListSupplier extends BlockStateSupplier {
		
		List<BlockState> states = new ArrayList<>();
		
		public BlockStateListSupplier(@NotNull List<BlockState> list) {
			states.addAll(list);
		}
		
		public BlockState get(@NotNull Random random) {
			return states.get(random.nextInt(states.size()));
		}
		
	}
	
	public static final class WeightedBlockStateSupplier extends BlockStateSupplier {
		
		Map<BlockState, Float> states = new HashMap<>();
		
		public WeightedBlockStateSupplier(@NotNull Map<BlockState, Float> map) {
			states.putAll(map);
		}
		
		public BlockState get(Random random) {
			return Support.getWeightedRandom(states, random);
		}
		
	}
	
	public static final class WeightedBlockStateGroupSupplier extends BlockStateSupplier {
		
		Identifier identifier;
		
		public WeightedBlockStateGroupSupplier(@NotNull String s) {
			s = s.substring(1);
			identifier = Identifier.tryParse(s);
		}
		
		public BlockState get(Random random) {
			return WeightedBlockGroupsLoader.WeightedBlockGroup.getRandomState(identifier, random);
		}
		
	}
	
	public static final class UniqueBlockStateGroupSupplier extends BlockStateSupplier {
		
		Identifier identifier;
		
		public UniqueBlockStateGroupSupplier(@NotNull String s) {
			s = s.substring(1);
			identifier = Identifier.tryParse(s);
		}
		
		public BlockState get(Random random) {
			return UniqueBlockGroupsLoader.UniqueBlockGroup.getFirstState(identifier);
		}
		
	}
	
}
