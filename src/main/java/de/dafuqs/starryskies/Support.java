package de.dafuqs.starryskies;

import de.dafuqs.starryskies.dimension.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.entity.player.*;
import net.minecraft.server.world.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.awt.*;
import java.util.List;
import java.util.*;

public class Support {
	
	public static class SpheroidDistance {
		public Spheroid spheroid;
		public double squaredDistance;
		
		public SpheroidDistance(Spheroid spheroid, double squaredDistance) {
			this.spheroid = spheroid;
			this.squaredDistance = squaredDistance;
		}
	}
	
	private static final List<Point> aroundPoints = new ArrayList<>() {{
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
	
	@Contract("_ -> new")
	public static Optional<SpheroidDistance> getClosestSpheroidToPlayer(@NotNull PlayerEntity serverPlayerEntity) {
		Vec3d playerPos = serverPlayerEntity.getPos();
		BlockPos playerPosBlock = new BlockPos((int) playerPos.x, (int) playerPos.y, (int) playerPos.z);
		
		SystemGenerator systemGenerator = SystemGenerator.getSystemGeneratorOfWorld(serverPlayerEntity.getEntityWorld().getRegistryKey());
		if (systemGenerator != null) {
			List<Spheroid> localSystem = systemGenerator.getSystemAtChunkPos(playerPosBlock.getX() / 16, playerPosBlock.getZ() / 16);
			
			Spheroid closestSpheroid = null;
			double currentMinDistance = Double.MAX_VALUE;
			for (Spheroid p : localSystem) {
				double currDist = playerPosBlock.getSquaredDistance(p.getPosition());
				if (currDist < currentMinDistance) {
					currentMinDistance = currDist;
					closestSpheroid = p;
				}
			}
			
			return Optional.of(new SpheroidDistance(closestSpheroid, currentMinDistance));
		} else {
			return Optional.empty();
		}
	}
	
	public static @Nullable SpheroidDistance getClosestSpheroid3x3(@NotNull ServerWorld serverWorld, BlockPos position, Identifier spheroidIdentifier) {
		SystemGenerator spheroidGenerator = SystemGenerator.getSystemGeneratorOfWorld(serverWorld.getRegistryKey());
		
		Spheroid closestSpheroid = null;
		double currentMinDistance = Double.MAX_VALUE;
		for (Point currentPoint : aroundPoints) {
			Point systemPos = getSystemCoordinateFromChunkCoordinate(position.getX() / 16, position.getZ() / 16);
			
			List<Spheroid> currentSystem = spheroidGenerator.getSystemAtPoint(new Point(systemPos.x + currentPoint.x, systemPos.y + currentPoint.y));
			for (Spheroid p : currentSystem) {
				if (p.getTemplate().getID().equals(spheroidIdentifier)) {
					double currDist = position.getSquaredDistance(p.getPosition());
					if (currDist < currentMinDistance) {
						currentMinDistance = currDist;
						closestSpheroid = p;
					}
				}
			}
			
			if (closestSpheroid != null) {
				return new SpheroidDistance(closestSpheroid, currentMinDistance);
			}
		}
		
		return null;
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
	
	/**
	 * System size of 50 results in system 0,0 at 0>+800, -1,0 at -800>0
	 *
	 * @param chunkX X coordinate of chunk
	 * @param chunkZ Z coordinate of chunk
	 * @return the system point
	 */
	@Contract("_, _ -> new")
	public static @NotNull Point getSystemCoordinateFromChunkCoordinate(int chunkX, int chunkZ) {
		int sysX;
		if (chunkX >= 0) {
			sysX = chunkX / StarrySkies.CONFIG.systemSizeChunks;
		} else {
			sysX = (int) Math.floor(chunkX / (float) StarrySkies.CONFIG.systemSizeChunks);
		}
		
		int sysZ;
		if (chunkZ >= 0) {
			sysZ = chunkZ / StarrySkies.CONFIG.systemSizeChunks;
		} else {
			sysZ = (int) Math.floor(chunkZ / (float) StarrySkies.CONFIG.systemSizeChunks);
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
	
}
