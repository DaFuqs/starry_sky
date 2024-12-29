package de.dafuqs.starryskies;

import com.google.common.collect.*;
import com.mojang.datafixers.util.*;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.worldgen.*;
import de.dafuqs.starryskies.worldgen.dimension.*;
import net.minecraft.block.*;
import net.minecraft.command.argument.*;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.*;

public class Support {

	private static final List<Point> AROUND_POINTS = new ArrayList<>() {{
		add(new Point(0, 0));
		add(new Point(1, -1));
		add(new Point(1, 0));
		add(new Point(1, 1));
		add(new Point(0, -1));
		add(new Point(0, 1));
		add(new Point(-1, -1));
		add(new Point(-1, 0));
		add(new Point(-1, 1));
	}};
	public static Codec<BlockState> BLOCKSTATE_STRING_CODEC = Codec.STRING.xmap(StarrySkies::getNullableStateFromString, BlockArgumentParser::stringifyBlockState);
	public static Codec<BlockArgumentParser.BlockResult> BLOCK_RESULT_CODEC = Codec.STRING.xmap(StarrySkies::getBlockResult, Support::blockResultToParseableString);

	public static String blockResultToParseableString(BlockArgumentParser.BlockResult result) {
		var stringifiedBlockState = BlockArgumentParser.stringifyBlockState(result.blockState());
		return result.nbt() == null ? stringifiedBlockState : stringifiedBlockState.concat(result.nbt().toString());
	}
	
	public static Optional<SphereDistance> getClosestSphere(ServerWorld world, BlockPos pos) {
		if (!(world.getChunkManager().getChunkGenerator() instanceof StarrySkyChunkGenerator starrySkyChunkGenerator)) {
			return Optional.empty();
		}
		
		SystemGenerator systemGenerator = starrySkyChunkGenerator.getSystemGenerator();
		if (systemGenerator != null) {
			PlacedSphere<?> closestSphere = null;
			double currentMinDistance = Double.MAX_VALUE;
			
			for (PlacedSphere<?> p : systemGenerator.getSystem(world, pos)) {
				double currDist = pos.getSquaredDistance(p.getPosition());
				if (currDist < currentMinDistance) {
					currentMinDistance = currDist;
					closestSphere = p;
				}
			}
			
			return Optional.of(new SphereDistance(closestSphere, currentMinDistance));
		} else {
			return Optional.empty();
		}
	}
	
	public static Optional<Pair<BlockPos, RegistryEntry<ConfiguredSphere<?, ?>>>> getClosestSphere3x3(@NotNull ServerWorld serverWorld, BlockPos position, Predicate<RegistryEntry<ConfiguredSphere<?, ?>>> predicate, DynamicRegistryManager registryManager) {
		if (!(serverWorld.getChunkManager().getChunkGenerator() instanceof StarrySkyChunkGenerator starrySkyChunkGenerator)) {
			return Optional.empty();
		}
		
		SystemGenerator systemGenerator = starrySkyChunkGenerator.getSystemGenerator();
		
		PlacedSphere<?> closestSphere = null;
		double currentMinDistance = Double.MAX_VALUE;
		for (Point currentPoint : AROUND_POINTS) {
			Point systemPos = getSystemCoordinateFromChunkCoordinate(position.getX() / 16, position.getZ() / 16);
			
			for (PlacedSphere<?> p : systemGenerator.getSystem(serverWorld, new Point(systemPos.x + currentPoint.x, systemPos.y + currentPoint.y))) {
				if (predicate.test(p.getRegistryEntry(registryManager))) {
					double currDist = position.getSquaredDistance(p.getPosition());
					if (currDist < currentMinDistance) {
						currentMinDistance = currDist;
						closestSphere = p;
					}
				}
			}
			
			if (closestSphere != null) {
				return Optional.of(new Pair<>(closestSphere.getPosition(), closestSphere.getRegistryEntry(registryManager)));
			}
		}
		
		return Optional.empty();
	}

	public static <E> E getWeightedRandom(@NotNull Map<E, Float> weights, Random random) {
		E result = null;
		double bestValue = Double.MAX_VALUE;

		for (E element : weights.keySet()) {
			double value = -Math.log(random.nextDouble()) / (weights.get(element));

			if (value < bestValue) {
				bestValue = value;
				result = element;
			}
		}
		return result;
	}

	public static @NotNull Point getSystemCoordinateFromChunkCoordinate(int chunkX, int chunkZ) {
		int systemSizeChunks = StarrySkies.CONFIG.systemSizeChunks;

		int sysX;
		if (chunkX >= 0) {
			sysX = chunkX / systemSizeChunks;
		} else {
			sysX = (int) Math.floor(chunkX / (float) systemSizeChunks);
		}

		int sysZ;
		if (chunkZ >= 0) {
			sysZ = chunkZ / systemSizeChunks;
		} else {
			sysZ = (int) Math.floor(chunkZ / (float) systemSizeChunks);
		}

		return new Point(sysX, sysZ);
	}

	/**
	 * Returns a random number between lowest and highest
	 *
	 * @param lowest  The lowest number (inclusive)
	 * @param highest The highest number (inclusive)
	 * @return The random number between lowest and highest
	 */
	public static int getRandomBetween(@NotNull Random random, int lowest, int highest) {
		return lowest + random.nextInt(highest - lowest + 1);
	}

	public static float getRandomBetween(@NotNull Random random, float lowest, float highest) {
		return lowest + random.nextFloat() * (highest - lowest);
	}

	public static double getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2));
	}

	public static double getDistance(@NotNull BlockPos blockPos1, @NotNull BlockPos blockpos2) {
		return getDistance(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ(), blockpos2.getX(), blockpos2.getY(), blockpos2.getZ());
	}

	public static boolean isBlockPosInChunkPos(@NotNull ChunkPos chunkPos, @NotNull BlockPos blockPos) {
		return (blockPos.getX() >= chunkPos.getStartX()
				&& blockPos.getX() < chunkPos.getStartX() + 16
				&& blockPos.getZ() >= chunkPos.getStartZ()
				&& blockPos.getZ() < chunkPos.getStartZ() + 16);
	}

	public static int getLowerGroundBlock(WorldAccess world, @NotNull BlockPos position, int minHeight) {
		BlockPos.Mutable blockPos$Mutable = new BlockPos.Mutable(position.getX(), position.getY(), position.getZ());

		//if height is an air block, move down until we reached a solid block. We are now on the surface of a piece of land
		while (blockPos$Mutable.getY() > minHeight) {
			if (!world.isAir(blockPos$Mutable)) {
				break;
			}
			blockPos$Mutable.move(Direction.DOWN);
		}
		return blockPos$Mutable.getY();
	}

	public static int getUpperGroundBlock(WorldAccess world, @NotNull BlockPos position, int minHeight) {
		BlockPos.Mutable blockPos$Mutable = new BlockPos.Mutable(position.getX(), position.getY(), position.getZ());

		//if height is an air block, move down until we reached a solid block. We are now on the surface of a piece of land
		while (blockPos$Mutable.getY() > minHeight) {
			if (!world.isAir(blockPos$Mutable)) {
				return blockPos$Mutable.getY();
			}
			blockPos$Mutable.move(Direction.UP);
		}
		return -1;
	}
	
	public static class SphereDistance {
		public PlacedSphere<?> sphere;
		public double squaredDistance;
		
		public SphereDistance(PlacedSphere<?> sphere, double squaredDistance) {
			this.sphere = sphere;
			this.squaredDistance = squaredDistance;
		}
	}

	public record FailSoftMapCodec<K, V>(Codec<K> keyCodec,
										 Codec<V> elementCodec) implements BaseMapCodec<K, V>, Codec<Map<K, V>> {

		@Override
		public <T> DataResult<Pair<Map<K, V>, T>> decode(final DynamicOps<T> ops, final T input) {
			return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap(map -> decode(ops, map)).map(r -> Pair.of(r, input));
		}

		@Override
		public <T> DataResult<T> encode(final Map<K, V> input, final DynamicOps<T> ops, final T prefix) {
			return encode(input, ops, ops.mapBuilder()).build(prefix);
		}

		@Override
		public <T> DataResult<Map<K, V>> decode(final DynamicOps<T> ops, final MapLike<T> input) {
			final ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();

			input.entries().forEach(pair -> {
				try {
					final DataResult<K> k = keyCodec().parse(ops, pair.getFirst());
					final DataResult<V> v = elementCodec().parse(ops, pair.getSecond());

					Optional<K> optionalK = k.result();
					Optional<V> optionalV = v.result();

					if (optionalK.isPresent() && optionalV.isPresent()) {
						builder.put(optionalK.get(), optionalV.get());
					}
				} catch (Throwable ignored) {
				}
			});

			final Map<K, V> elements = builder.build();

			return DataResult.success(elements);
		}

		@Override
		public String toString() {
			return "FailSoftMapCodec[" + keyCodec + " -> " + elementCodec + ']';
		}
	}
}
