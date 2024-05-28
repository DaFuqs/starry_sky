package de.dafuqs.starryskies.dimension;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.data_loaders.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.registry.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.awt.*;
import java.util.List;
import java.util.*;

import static org.apache.logging.log4j.Level.*;

public class SystemGenerator {
	
	public static HashMap<SpheroidDimensionType, SystemGenerator> systemGeneratorMap = new HashMap<>();
	
	// spawning probabilities
	private final SpheroidDimensionType spheroidDimensionType;
	private final HashMap<Point, List<de.dafuqs.starryskies.spheroids.spheroids.Spheroid>> cache = new HashMap<>();
	public static ServerWorld world;
	
	private final int SYSTEM_SIZE_CHUNKS;
	private final int MIN_DISTANCE_BETWEEN_SPHERES;
	private final int SPHERE_DENSITY;
	private final int FLOOR_HEIGHT;
	
	public SystemGenerator(SpheroidDimensionType spheroidDimensionType) {
		this.spheroidDimensionType = spheroidDimensionType;
		systemGeneratorMap.put(spheroidDimensionType, this);
		
		this.SYSTEM_SIZE_CHUNKS = StarrySkies.CONFIG.systemSizeChunks;
		switch (spheroidDimensionType) {
			case OVERWORLD -> {
				this.MIN_DISTANCE_BETWEEN_SPHERES = StarrySkies.CONFIG.minDistanceBetweenSpheresOverworld;
				this.SPHERE_DENSITY = StarrySkies.CONFIG.sphereDensityOverworld;
				this.FLOOR_HEIGHT = StarrySkies.CONFIG.floorHeightOverworld;
			}
			case NETHER -> {
				this.MIN_DISTANCE_BETWEEN_SPHERES = StarrySkies.CONFIG.minDistanceBetweenSpheresNether;
				this.SPHERE_DENSITY = StarrySkies.CONFIG.sphereDensityNether;
				this.FLOOR_HEIGHT = StarrySkies.CONFIG.floorHeightNether;
			}
			default -> {
				this.MIN_DISTANCE_BETWEEN_SPHERES = StarrySkies.CONFIG.minDistanceBetweenSpheresEnd;
				this.SPHERE_DENSITY = StarrySkies.CONFIG.sphereDensityEnd;
				this.FLOOR_HEIGHT = StarrySkies.CONFIG.floorHeightEnd;
			}
		}
	}
	
	public static SystemGenerator getSystemGeneratorOfWorld(@NotNull RegistryKey<World> worldRegistryKey) {
		if (worldRegistryKey.equals(StarrySkyDimension.OVERWORLD_KEY)) {
			return systemGeneratorMap.get(SpheroidDimensionType.OVERWORLD);
		} else if (worldRegistryKey.equals(StarrySkyDimension.NETHER_KEY)) {
			return systemGeneratorMap.get(SpheroidDimensionType.NETHER);
		} else {
			return systemGeneratorMap.get(SpheroidDimensionType.END);
		}
	}
	
	/**
	 * Returns the system at the given chunk coordinates
	 * If a system does not exist yet it will be generated
	 *
	 * @param chunkX chunk chunkX location
	 * @param chunkZ chunk chunkZ location
	 * @return List of planetoids representing the system this chunk is in
	 */
	public List<Spheroid> getSystemAtChunkPos(int chunkX, int chunkZ) {
		Point systemPos = Support.getSystemCoordinateFromChunkCoordinate(chunkX, chunkZ);
		return getSystemAtPoint(systemPos);
	}
	
	public List<Spheroid> getSystemAtPoint(Point systemPos) {
		List<Spheroid> curSystem = cache.get(systemPos);
		
		if (curSystem == null) {
			if (world == null) {
				world = StarrySkies.getStarryWorld(spheroidDimensionType);
			}
			
			//doesn't exist. Generate new system and cache it
			curSystem = generateSpheroidsAtSystemPoint(world, systemPos);
			cache.put(systemPos, curSystem);
		}
		
		return curSystem;
	}
	
	private @NotNull ChunkRandom getSystemRandom(@NotNull Point systemPoint) {
		int firstChunkPosX = systemPoint.x * SYSTEM_SIZE_CHUNKS;
		int firstChunkPosZ = systemPoint.y * SYSTEM_SIZE_CHUNKS;
		ChunkRandom systemRandom = new ChunkRandom(new CheckedRandom(StarrySkies.starryWorld.getSeed()));
		systemRandom.setCarverSeed(StarrySkies.starryWorld.getSeed(), firstChunkPosX, firstChunkPosZ); // and the seed from the first chunk+
        StarrySkies.LOGGER.debug("Generated seed for system at {},{}(first chunk: {},{}", systemPoint.x, systemPoint.y, firstChunkPosX, firstChunkPosZ);
		return systemRandom;
	}
	
	private @NotNull List<Spheroid> generateSpheroidsAtSystemPoint(World world, @NotNull Point systemPoint) {
		int systemPointX = systemPoint.x;
		int systemPointZ = systemPoint.y;
		
		ChunkRandom systemRandom = getSystemRandom(systemPoint);
		
		// Places a log/leaf planet at 16, 16 in the overworld etc.
		ArrayList<Spheroid> defaultSpheroids = getDefaultSpheroids(systemPointX, systemPointZ, systemRandom);
		ArrayList<Spheroid> spheroids = new ArrayList<>(defaultSpheroids);
		
		// try to create DENSITY planets in system
		int worldHeight = world.getHeight();
		for (int currentDensity = 0; currentDensity < SPHERE_DENSITY; currentDensity++) {
			
			// create new planets
			Spheroid currentSpheroid = getRandomSpheroid(systemRandom);
			
			// set position, check bounds with system edges on x and z
			int xPos = Support.getRandomBetween(systemRandom, currentSpheroid.getRadius(), (SYSTEM_SIZE_CHUNKS * 16 - currentSpheroid.getRadius()));
			xPos += SYSTEM_SIZE_CHUNKS * 16 * systemPointX;
			int zPos = Support.getRandomBetween(systemRandom, currentSpheroid.getRadius(), (SYSTEM_SIZE_CHUNKS * 16 - currentSpheroid.getRadius()));
			zPos += SYSTEM_SIZE_CHUNKS * 16 * systemPointZ;
			int yPos = world.getBottomY() + FLOOR_HEIGHT + currentSpheroid.getRadius() + systemRandom.nextInt(((worldHeight - currentSpheroid.getRadius() * 2 - FLOOR_HEIGHT)));
			BlockPos spherePos = new BlockPos(xPos, yPos, zPos);
			
			// check for collisions with existing spheroids
			// if any collision, discard it
			boolean discard = false;
			for (Spheroid spheroid : spheroids) {
				//each spheroid has to be at least pl1.radius + pl2.radius + min distance apart
				int distMin = (spheroid.getRadius() + currentSpheroid.getRadius() + MIN_DISTANCE_BETWEEN_SPHERES);
				double distSquared = spherePos.getSquaredDistance(spheroid.getPosition());
				if (distSquared < distMin * distMin) {
					discard = true;
					break;
				}
			}
			
			if (!discard) {
				// no intersections with other spheres => add it to the list
				currentSpheroid.setPosition(spherePos);
				spheroids.add(currentSpheroid);
			}
		}

        StarrySkies.LOGGER.debug("Created a new system with {} spheroids at system position {},{}", spheroids.size(), systemPointX, systemPointZ);
		
		return spheroids;
	}
	
	private ArrayList<Spheroid> getDefaultSpheroids(int systemPointX, int systemPointZ, ChunkRandom random) {
		ArrayList<Spheroid> defaultSpheroids = new ArrayList<>();
		Spheroid spheroid;
		switch (this.spheroidDimensionType) {
			case NETHER:
				if (systemPointX == 0 && systemPointZ == 0) {
					spheroid = SpheroidTemplateLoader.STARTER_NETHER.generate(random);
					spheroid.setPosition(new BlockPos(16, 70, 16));
					defaultSpheroids.add(spheroid);
				}
				break;
			case END:
				if ((systemPointX == 0 && systemPointZ == 0)
						|| (systemPointX == -1 && systemPointZ == 0)
						|| (systemPointX == 0 && systemPointZ == -1)
						|| (systemPointX == -1 && systemPointZ == -1)) {
					spheroid = SpheroidTemplateLoader.STARTER_END.generate(random);
					spheroid.setPosition(new BlockPos(0, 30, 0));
					defaultSpheroids.add(spheroid);
				}
				break;
			default:
				if (systemPointX == 0 && systemPointZ == 0) {
					spheroid = SpheroidTemplateLoader.STARTER_OVERWORLD.generate(random);
					spheroid.setPosition(new BlockPos(16, 70, 16));
					defaultSpheroids.add(spheroid);
				}
		}
		return defaultSpheroids;
	}
	
	private Spheroid getRandomSpheroid(ChunkRandom systemRandom) {
		Spheroid.Template template;
		do {
			template = SpheroidTemplateLoader.getWeightedRandomSpheroid(spheroidDimensionType, systemRandom);
		} while (template == null);

        StarrySkies.LOGGER.debug("Created a new sphere of type {} Next random: {}", template, systemRandom.nextInt());
		return template.generate(systemRandom);
	}
	
}
