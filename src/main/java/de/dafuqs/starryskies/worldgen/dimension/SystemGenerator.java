package de.dafuqs.starryskies.worldgen.dimension;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.worldgen.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.block.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;
import org.jetbrains.annotations.*;

import java.awt.*;
import java.util.List;
import java.util.*;

import static de.dafuqs.starryskies.Support.*;

public class SystemGenerator {
	
	public static final Codec<SystemGenerator> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Codec.INT.fieldOf("spheres_per_system").forGetter(generator -> generator.spheresPerSystem),
					Codec.INT.fieldOf("min_distance_between_spheres").forGetter(generator -> generator.minDistanceBetweenSpheres),
					Codec.INT.fieldOf("floor_height").forGetter(generator -> generator.floorHeight),
					BLOCKSTATE_STRING_CODEC.fieldOf("floor_state").forGetter(generator -> generator.floorState),
					BLOCKSTATE_STRING_CODEC.fieldOf("bottom_state").forGetter(generator -> generator.bottomState),
					DefaultSphere.CODEC.listOf().fieldOf("fixed_spheres").forGetter(generator -> generator.defaultSpheres)
			).apply(instance, SystemGenerator::new)
	);
	
	private final int spheresPerSystem;
	private final int minDistanceBetweenSpheres;
	private final int floorHeight;
	private final BlockState floorState;
	private final BlockState bottomState;
	private final List<DefaultSphere> defaultSpheres;
	
	private final Map<Map<ConfiguredSphere<?, ?>, Float>, Float> generationGroups = new Object2FloatArrayMap<>();
	private final Map<Point, System> systemCache = new Object2ObjectArrayMap<>();
	
	public SystemGenerator(int spheresPerSystem, int minDistanceBetweenSpheres, int floorHeight, BlockState floorState, BlockState bottomState, List<DefaultSphere> defaultSpheres) {
		this.spheresPerSystem = spheresPerSystem;
		this.minDistanceBetweenSpheres = minDistanceBetweenSpheres;
		this.floorHeight = floorHeight;
		this.floorState = floorState;
		this.bottomState = bottomState;
		this.defaultSpheres = defaultSpheres;
	}
	
	public void addGenerationGroup(Map<ConfiguredSphere<?, ?>, Float> weightedSpheres, float weight) {
		this.generationGroups.put(weightedSpheres, weight);
	}
	
	public int getFloorHeight() {
		return this.floorHeight;
	}
	
	public BlockState getSeaBlock(int heightY) {
		if (heightY == 0) {
			return bottomState;
		} else {
			return floorState;
		}
	}
	
	/**
	 * Returns the system at the given chunk coordinates
	 * If a system does not exist yet, it will be generated
	 *
	 * @param chunkX chunk chunkX location
	 * @param chunkZ chunk chunkZ location
	 * @return The List of spheres representing the system this chunk is in
	 */
	public System getSystem(WorldAccess worldAccess, long seed, int chunkX, int chunkZ) {
		Point systemPos = Support.getSystemCoordinateFromChunkCoordinate(chunkX, chunkZ);
		return getSystem(worldAccess, seed, systemPos);
	}
	
	public System getSystem(StructureWorldAccess world, BlockPos pos) {
		ChunkPos chunkPos = new ChunkPos(pos);
		Point systemPos = Support.getSystemCoordinateFromChunkCoordinate(chunkPos.x, chunkPos.z);
		return getSystem(world, world.getSeed(), systemPos);
	}
	
	public System getSystem(StructureWorldAccess world, Point systemPos) {
		System system = systemCache.get(systemPos);
		
		if (system == null) {
			// System at that pos is not generated yet
			// Generate new system and cache it
			system = System.generateSystem(this, world.getBottomY(), world.getHeight(), world.getSeed(), systemPos, world.getRegistryManager());
			systemCache.put(systemPos, system);
		}
		
		return system;
	}
	
	public System getSystem(WorldAccess worldAccess, long seed, Point systemPos) {
		System system = systemCache.get(systemPos);
		
		if (system == null) {
			// System at that pos is not generated yet
			// Generate new system and cache it
			system = System.generateSystem(this, worldAccess.getBottomY(), worldAccess.getHeight(), seed, systemPos, worldAccess.getRegistryManager());
			systemCache.put(systemPos, system);
		}
		
		return system;
	}
	
	public Iterable<? extends PlacedSphere<?>> getSystem(Chunk chunk, long seed, DynamicRegistryManager registryManager) {
		Point systemPos = Support.getSystemCoordinateFromChunkCoordinate(chunk.getPos().x, chunk.getPos().z);
		System system = systemCache.get(systemPos);
		
		if (system == null) {
			// System at that pos is not generated yet
			// Generate new system and cache it
			system = System.generateSystem(this, chunk.getBottomY(), chunk.getHeight(), seed, systemPos, registryManager);
			systemCache.put(systemPos, system);
		}
		
		return system;
	}
	
	public record DefaultSphere(int systemX, int systemZ, int x, int y, int z, Identifier sphereId) {
		public static final Codec<DefaultSphere> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						Codec.INT.fieldOf("system_x").forGetter(s -> s.systemX),
						Codec.INT.fieldOf("system_y").forGetter(s -> s.systemZ),
						Codec.INT.fieldOf("x").forGetter(s -> s.x),
						Codec.INT.fieldOf("y").forGetter(s -> s.y),
						Codec.INT.fieldOf("z").forGetter(s -> s.z),
						Identifier.CODEC.fieldOf("sphere_id").forGetter(s -> s.sphereId)
				).apply(instance, DefaultSphere::new)
		);
	}
	
	public record System(List<PlacedSphere<?>> spheres) implements Iterable<PlacedSphere<?>> {
		
		private static System generateSystem(SystemGenerator systemGenerator, int bottomY, int worldHeight, long seed, @NotNull Point systemPoint, DynamicRegistryManager registryManager) {
			int systemPointX = systemPoint.x;
			int systemPointZ = systemPoint.y;
			
			ChunkRandom systemRandom = getSystemRandom(systemPoint, seed);
			
			// Places a log/leaf planet at 16, 16 in the overworld etc.
			List<PlacedSphere<?>> defaultSpheres = getDefaultSpheres(systemGenerator, systemPointX, systemPointZ, systemRandom, registryManager);
			List<PlacedSphere<?>> spheresInSystem = new ArrayList<>(defaultSpheres);
			
			// try to create DENSITY spheresInSystem in this system
			for (int currentDensity = 0; currentDensity < systemGenerator.spheresPerSystem; currentDensity++) {
				
				// create new planets
				@Nullable PlacedSphere<?> currentSphere = getRandomSphere(systemGenerator, systemRandom, systemPoint, registryManager, bottomY, worldHeight, spheresInSystem);
				if (currentSphere == null) {
					continue;
				}
				
				spheresInSystem.add(currentSphere);
			}
			
			StarrySkies.LOGGER.debug("Created a new system with {} spheres at system position {},{}", spheresInSystem.size(), systemPointX, systemPointZ);
			
			return new System(spheresInSystem);
		}
		
		private static @NotNull ChunkRandom getSystemRandom(@NotNull Point systemPoint, long seed) {
			ChunkRandom systemRandom = new ChunkRandom(new CheckedRandom(seed));
			systemRandom.setCarverSeed(seed, systemPoint.x, systemPoint.y);
			return systemRandom;
		}
		
		private static List<PlacedSphere<?>> getDefaultSpheres(SystemGenerator systemGenerator, int systemPointX, int systemPointZ, ChunkRandom systemRandom, DynamicRegistryManager registryManager) {
			if (systemGenerator.defaultSpheres.isEmpty()) {
				return List.of();
			}
			
			Registry<ConfiguredSphere<?, ?>> templateRegistry = registryManager.get(StarryRegistryKeys.CONFIGURED_SPHERE);
			ArrayList<PlacedSphere<?>> defaultSpheres = new ArrayList<>();
			for (DefaultSphere defaultSphere : systemGenerator.defaultSpheres) {
				if (systemPointX == defaultSphere.systemX && systemPointZ == defaultSphere.systemZ) {
					ConfiguredSphere<?, ?> template = templateRegistry.get(defaultSphere.sphereId);
					if (template != null) {
						BlockPos pos = new BlockPos(defaultSphere.x, defaultSphere.y, defaultSphere.z);
						PlacedSphere<?> sphere = template.generate(systemRandom, registryManager, pos, template.getSize(systemRandom));
						sphere.setPosition(new BlockPos(defaultSphere.x, defaultSphere.y, defaultSphere.z));
						defaultSpheres.add(sphere);
					}
				}
			}
			
			return defaultSpheres;
		}
		
		private static @Nullable PlacedSphere<?> getRandomSphere(SystemGenerator systemGenerator, ChunkRandom systemRandom, @NotNull Point systemPoint, DynamicRegistryManager registryManager, int bottomY, int worldHeight, List<PlacedSphere<?>> spheresInSystem) {
			ConfiguredSphere<?, ?> selectedSphere;
			PlacedSphere<?> placed;
			
			int systemSizeChunks = StarrySkies.CONFIG.systemSizeChunks;
			
			do {
				do {
					Map<ConfiguredSphere<?, ?>, Float> spheresInSelectedGroup = getWeightedRandom(systemGenerator.generationGroups, systemRandom);
					if (spheresInSelectedGroup.isEmpty()) {
						return null;
					}
					selectedSphere = getWeightedRandom(spheresInSelectedGroup, systemRandom);
				} while (selectedSphere == null);
				
				StarrySkies.LOGGER.debug("Created a new sphere of type {} Next random: {}", selectedSphere, systemRandom.nextInt());
				
				// set position, check bounds with system edges on x and z
				float radius = selectedSphere.getSize(systemRandom);
				int iRadius = (int) radius;
				int xPos = Support.getRandomBetween(systemRandom, iRadius, (systemSizeChunks * 16 - iRadius));
				xPos += systemSizeChunks * 16 * systemPoint.x;
				int zPos = Support.getRandomBetween(systemRandom, iRadius, (systemSizeChunks * 16 - iRadius));
				zPos += systemSizeChunks * 16 * systemPoint.y;
				int yPos = bottomY + systemGenerator.floorHeight + iRadius + systemRandom.nextInt(((worldHeight - iRadius * 2 - systemGenerator.floorHeight)));
				BlockPos spherePos = new BlockPos(xPos, yPos, zPos);
				
				placed = selectedSphere.generate(systemRandom, registryManager, spherePos, radius);
				placed.setPosition(spherePos);
				
				// Check for intersections with other spheres in this system
				// if any collision, discard it
				for (PlacedSphere<?> sphere : spheresInSystem) {
					//each sphere has to be at least pl1.radius + pl2.radius + min distance apart
					int distMin = (sphere.getRadius() + iRadius + systemGenerator.minDistanceBetweenSpheres);
					double distSquared = spherePos.getSquaredDistance(sphere.getPosition());
					if (distSquared < distMin * distMin) {
						placed = null;
						break;
					}
				}
			} while (placed == null);
			
			return placed;
		}
		
		@NotNull
		@Override
		public Iterator<PlacedSphere<?>> iterator() {
			return this.spheres.iterator();
		}
	}
	
}
