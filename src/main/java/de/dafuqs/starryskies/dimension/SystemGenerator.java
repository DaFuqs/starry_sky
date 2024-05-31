package de.dafuqs.starryskies.dimension;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.data_loaders.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.block.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.awt.*;
import java.util.List;
import java.util.*;

import static de.dafuqs.starryskies.Support.*;

public class SystemGenerator {
	
	public static final Codec<SystemGenerator> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Codec.INT.fieldOf("system_size_chunks").forGetter(generator -> generator.systemSizeChunks),
					Codec.INT.fieldOf("spheres_per_system").forGetter(generator -> generator.spheresPerSystem),
					Codec.INT.fieldOf("min_distance_between_spheres").forGetter(generator -> generator.minDistanceBetweenSpheres),
					Codec.INT.fieldOf("floor_height").forGetter(generator -> generator.floorHeight),
					BLOCKSTATE_STRING_CODEC.fieldOf("floor_state").forGetter(generator -> generator.floorState),
					BLOCKSTATE_STRING_CODEC.fieldOf("bottom_state").forGetter(generator -> generator.bottomState),
					DefaultSpheroidType.CODEC.listOf().fieldOf("fixed_spheres").forGetter(generator -> generator.defaultSpheres)
			).apply(instance, SystemGenerator::new)
	);
	
	public static Map<RegistryKey<World>, SystemGenerator> systemGeneratorMap = new HashMap<>();
	
	// spawning probabilities
	private final HashMap<Point, List<Spheroid>> sphereCache = new HashMap<>();
	
	private final int systemSizeChunks;
	private final int spheresPerSystem;
	private final int minDistanceBetweenSpheres;
	private final int floorHeight;
	private final BlockState floorState;
	private final BlockState bottomState;
	private final List<DefaultSpheroidType> defaultSpheres;
	private final Map<Identifier, Float> generationGroups = new Object2FloatArrayMap<>();
	
	public record DefaultSpheroidType(int systemX, int systemZ, int x, int y, int z, Identifier templateID) {
		public static final Codec<DefaultSpheroidType> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						Codec.INT.fieldOf("system_x").forGetter(s -> s.systemX),
						Codec.INT.fieldOf("system_y").forGetter(s -> s.systemZ),
						Codec.INT.fieldOf("x").forGetter(s -> s.x),
						Codec.INT.fieldOf("y").forGetter(s -> s.y),
						Codec.INT.fieldOf("z").forGetter(s -> s.z),
						Identifier.CODEC.fieldOf("id").forGetter(s -> s.templateID)
				).apply(instance, DefaultSpheroidType::new)
		);
	}
	
	public SystemGenerator(int systemSizeChunks, int spheresPerSystem, int minDistanceBetweenSpheres, int floorHeight, BlockState floorState, BlockState bottomState, List<DefaultSpheroidType> defaultSpheres) {
		this.systemSizeChunks = systemSizeChunks;
		this.spheresPerSystem = spheresPerSystem;
		this.minDistanceBetweenSpheres = minDistanceBetweenSpheres;
		this.floorHeight = floorHeight;
		this.floorState = floorState;
		this.bottomState = bottomState;
		this.defaultSpheres = defaultSpheres;
	}
	
	public static SystemGenerator get(RegistryKey<World> registryKey) {
		return systemGeneratorMap.get(registryKey);
	}
	
	public void addGenerationGroup(Identifier id, float weight) {
		this.generationGroups.put(id, weight);
	}
	
	public Iterable<Identifier> getGenerationGroups() {
		return this.generationGroups.keySet();
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
	 * @return List of planetoids representing the system this chunk is in
	 */
	public List<Spheroid> getSystemAtChunkPos(StructureWorldAccess worldAccess, int chunkX, int chunkZ) {
		Point systemPos = Support.getSystemCoordinateFromChunkCoordinate(chunkX, chunkZ);
		return getSystemAtPoint(worldAccess, systemPos);
	}
	
	public List<Spheroid> getSystemAtPoint(StructureWorldAccess worldAccess, Point systemPos) {
		List<Spheroid> system = sphereCache.get(systemPos);
		
		if (system == null) {
			// System at that pos is not generated yet
			// Generate new system and cache it
			system = generateSpheroidsAtSystemPoint(worldAccess, systemPos);
			sphereCache.put(systemPos, system);
		}
		
		return system;
	}
	
	private @NotNull ChunkRandom getSystemRandom(StructureWorldAccess worldAccess, @NotNull Point systemPoint) {
		int firstChunkPosX = systemPoint.x * systemSizeChunks;
		int firstChunkPosZ = systemPoint.y * systemSizeChunks;
		ChunkRandom systemRandom = new ChunkRandom(new CheckedRandom(worldAccess.getSeed()));
		systemRandom.setCarverSeed(worldAccess.getSeed(), firstChunkPosX, firstChunkPosZ); // and the seed from the first chunk+
		StarrySkies.LOGGER.debug("Generated seed for system at {},{}(first chunk: {},{}", systemPoint.x, systemPoint.y, firstChunkPosX, firstChunkPosZ);
		return systemRandom;
	}
	
	private @NotNull List<Spheroid> generateSpheroidsAtSystemPoint(StructureWorldAccess worldAccess, @NotNull Point systemPoint) {
		int systemPointX = systemPoint.x;
		int systemPointZ = systemPoint.y;
		
		ChunkRandom systemRandom = getSystemRandom(worldAccess, systemPoint);
		
		// Places a log/leaf planet at 16, 16 in the overworld etc.
		ArrayList<Spheroid> defaultSpheroids = getDefaultSpheroids(systemPointX, systemPointZ, systemRandom);
		ArrayList<Spheroid> spheroids = new ArrayList<>(defaultSpheroids);
		
		// try to create DENSITY planets in system
		int worldHeight = worldAccess.getHeight();
		for (int currentDensity = 0; currentDensity < spheresPerSystem; currentDensity++) {
			
			// create new planets
			Spheroid currentSpheroid = getRandomSpheroid(systemRandom);
			
			// set position, check bounds with system edges on x and z
			int xPos = Support.getRandomBetween(systemRandom, currentSpheroid.getRadius(), (systemSizeChunks * 16 - currentSpheroid.getRadius()));
			xPos += systemSizeChunks * 16 * systemPointX;
			int zPos = Support.getRandomBetween(systemRandom, currentSpheroid.getRadius(), (systemSizeChunks * 16 - currentSpheroid.getRadius()));
			zPos += systemSizeChunks * 16 * systemPointZ;
			int yPos = worldAccess.getBottomY() + floorHeight + currentSpheroid.getRadius() + systemRandom.nextInt(((worldHeight - currentSpheroid.getRadius() * 2 - floorHeight)));
			BlockPos spherePos = new BlockPos(xPos, yPos, zPos);
			
			// check for collisions with existing spheroids
			// if any collision, discard it
			boolean discard = false;
			for (Spheroid spheroid : spheroids) {
				//each spheroid has to be at least pl1.radius + pl2.radius + min distance apart
				int distMin = (spheroid.getRadius() + currentSpheroid.getRadius() + minDistanceBetweenSpheres);
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
	
	private ArrayList<Spheroid> getDefaultSpheroids(int systemPointX, int systemPointZ, ChunkRandom systemRandom) {
		ArrayList<Spheroid> defaultSpheroids = new ArrayList<>();
		
		for (DefaultSpheroidType defaultSphere : this.defaultSpheres) {
			if (systemPointX == defaultSphere.systemX && systemPointZ == defaultSphere.systemZ) {
				Spheroid.Template<?> template = StarryRegistries.SPHEROID_TEMPLATE.get(defaultSphere.templateID);
				if(template != null) {
					Spheroid spheroid = template.generate(systemRandom);
					spheroid.setPosition(new BlockPos(defaultSphere.x, defaultSphere.y, defaultSphere.z));
					defaultSpheroids.add(spheroid);
				}
			}
		}
		
		return defaultSpheroids;
	}
	
	private Spheroid getRandomSpheroid(ChunkRandom systemRandom) {
		Spheroid.Template<?> template;
		do {
			Identifier distributionTypeID = Support.getWeightedRandom(generationGroups, systemRandom);
			template = SpheroidTemplateLoader.getWeightedRandomSpheroid(distributionTypeID, systemRandom);
		} while (template == null);
		
		StarrySkies.LOGGER.debug("Created a new sphere of type {} Next random: {}", template, systemRandom.nextInt());
		return template.generate(systemRandom);
	}
	
}
