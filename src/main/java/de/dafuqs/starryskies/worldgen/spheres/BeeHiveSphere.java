package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.data_loaders.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.block.enums.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.floatprovider.*;
import net.minecraft.util.math.intprovider.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;

import java.util.*;

public class BeeHiveSphere extends Sphere<BeeHiveSphere.Config> {
	
	private static final String FLOWERS_GROUP = "flowers";
	private static final String TALL_FLOWERS_GROUP = "tall_flowers";
	
	public BeeHiveSphere(Codec<BeeHiveSphere.Config> codec) {
		super(codec);
	}
	
	@Override
	public PlacedSphere<?> generate(ConfiguredSphere<? extends Sphere<BeeHiveSphere.Config>, Config> configuredSphere, Config config, ChunkRandom random, DynamicRegistryManager registryManager) {
		return new BeeHiveSphere.Placed(configuredSphere, configuredSphere.getSize(random), configuredSphere.getDecorators(random), configuredSphere.getSpawns(random), random, config.shellThickness.get(random), config.flowerRingRadius.get(random), config.flowerRingSpacing.get(random));
	}
	
	public static class Config extends SphereConfig {
		
		public static final Codec<BeeHiveSphere.Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				SphereConfig.CONFIG_CODEC.forGetter((config) -> config),
				IntProvider.POSITIVE_CODEC.fieldOf("shell_thickness").forGetter((config) -> config.shellThickness),
				IntProvider.POSITIVE_CODEC.fieldOf("flower_ring_radius").forGetter((config) -> config.shellThickness),
				IntProvider.POSITIVE_CODEC.fieldOf("flower_ring_spacing").forGetter((config) -> config.shellThickness)
		).apply(instance, (sphereConfig, shellThickness, flowerRingRadius, flowerRingSpacing) -> new Config(sphereConfig.size, sphereConfig.decorators, sphereConfig.spawns, sphereConfig.generation, shellThickness, flowerRingRadius, flowerRingSpacing)));
		
		protected final IntProvider shellThickness;
		protected final IntProvider flowerRingRadius;
		protected final IntProvider flowerRingSpacing;
		
		public Config(FloatProvider size, Map<ConfiguredSphereDecorator<?, ?>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation, IntProvider shellThickness, IntProvider flowerRingRadius, IntProvider flowerRingSpacing) {
			super(size, decorators, spawns, generation);
			this.shellThickness = shellThickness;
			this.flowerRingRadius = flowerRingRadius;
			this.flowerRingSpacing = flowerRingSpacing;
		}
		
	}
	
	public static class Placed extends PlacedSphere<BeeHiveSphere.Config> {
		
		private final int shellThickness;
		private final int flowerRingRadius;
		private final int flowerRingSpacing;
		
		private List<BeehiveBlockEntity> outerBeehiveBlockEntities = new ArrayList<>();
		private BeehiveBlockEntity queenBeehiveBlockEntity;
		
		public Placed(ConfiguredSphere<? extends Sphere<BeeHiveSphere.Config>, BeeHiveSphere.Config> configuredSphere, float radius, List<ConfiguredSphereDecorator<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
					  int shellThickness, int flowerRingRadius, int flowerRingSpacing) {
			super(configuredSphere, radius, decorators, spawns, random);
			this.shellThickness = shellThickness;
			this.flowerRingRadius = flowerRingRadius;
			this.flowerRingSpacing = flowerRingSpacing;
		}
		
		@Override
		public void generate(Chunk chunk, DynamicRegistryManager registryManager) {
			int chunkX = chunk.getPos().x;
			int chunkZ = chunk.getPos().z;
			random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
			int x = this.getPosition().getX();
			int y = this.getPosition().getY();
			int z = this.getPosition().getZ();
			
			int ceiledRadius = (int) Math.ceil(this.radius);
			int maxX = Math.min(chunkX * 16 + 15, x + ceiledRadius);
			int maxZ = Math.min(chunkZ * 16 + 15, z + ceiledRadius);
			
			float endRingDistance = this.radius;
			float startRingDistance = this.radius - this.flowerRingRadius;
			float shellDistance = startRingDistance - this.flowerRingSpacing;
			float coreDistance = shellDistance - shellThickness;
			
			BlockState beeHiveBlockState = Blocks.BEE_NEST.getDefaultState();
			
			BlockPos.Mutable currBlockPos = new BlockPos.Mutable();
			for (int x2 = Math.max(chunkX * 16, x - ceiledRadius); x2 <= maxX; x2++) {
				for (int y2 = y - ceiledRadius; y2 <= y + ceiledRadius; y2++) {
					for (int z2 = Math.max(chunkZ * 16, z - ceiledRadius); z2 <= maxZ; z2++) {
						long d = Math.round(Support.getDistance(x, y, z, x2, y2, z2));
						if (d > this.radius) {
							continue;
						}
						currBlockPos.set(x2, y2, z2);
						
						if (d == 0) {
							// bee hive in center
							chunk.setBlockState(currBlockPos, beeHiveBlockState, false);
							this.queenBeehiveBlockEntity = new BeehiveBlockEntity(currBlockPos, beeHiveBlockState);
							chunk.setBlockEntity(queenBeehiveBlockEntity);
						} else if (d == shellThickness && y2 - y == 0 && random.nextInt(10) == 0) {
							// middle outer shell: random hives
							Direction direction;
							float xDist = x2 - x;
							float zDist = z2 - z;
							if (xDist > 0) {
								if (Math.abs(xDist) > Math.abs(zDist)) {
									direction = Direction.EAST;
								} else {
									if (zDist > 0) {
										direction = Direction.SOUTH;
									} else {
										direction = Direction.NORTH;
									}
								}
							} else {
								if (Math.abs(xDist) < Math.abs(zDist)) {
									if (zDist > 0) {
										direction = Direction.SOUTH;
									} else {
										direction = Direction.NORTH;
									}
								} else {
									direction = Direction.WEST;
								}
							}
							// set the block
							BlockState blockState = Blocks.BEE_NEST.getDefaultState().with(BeehiveBlock.FACING, direction);
							chunk.setBlockState(currBlockPos, blockState, false);
							
							// set and save the blockentity
							BeehiveBlockEntity outerBeehiveBlockEntity = new BeehiveBlockEntity(currBlockPos, blockState);
							chunk.setBlockEntity(outerBeehiveBlockEntity);
							this.outerBeehiveBlockEntities.add(outerBeehiveBlockEntity);
						} else if (d <= coreDistance) {
							// core
							int r = random.nextInt((int) Math.ceil(coreDistance / 3F)); // way more honey in the middle
							if (coreDistance - r <= d) {
								chunk.setBlockState(currBlockPos, Blocks.HONEY_BLOCK.getDefaultState(), false);
							} else {
								chunk.setBlockState(currBlockPos, Blocks.AIR.getDefaultState(), false);
							}
						} else if (d <= shellDistance) {
							// shell
							if (random.nextInt(10) == 0) {
								chunk.setBlockState(currBlockPos, Blocks.HONEY_BLOCK.getDefaultState(), false);
							} else {
								chunk.setBlockState(currBlockPos, Blocks.HONEYCOMB_BLOCK.getDefaultState(), false);
							}
						} else if (y - y2 == 0 && d > startRingDistance && d <= endRingDistance) {
							chunk.setBlockState(currBlockPos, Blocks.GRASS_BLOCK.getDefaultState(), false);
							int rand = random.nextInt(4);
							if (rand == 0) {
								chunk.setBlockState(currBlockPos.up(), getRandomFlower(random), false);
							} else if (rand == 1) {
								BlockState randomTallFlower = getRandomTallFlower(registryManager, random, currBlockPos);
								chunk.setBlockState(currBlockPos.up(), randomTallFlower.with(TallPlantBlock.HALF, DoubleBlockHalf.LOWER), false);
								chunk.setBlockState(currBlockPos.up(2), randomTallFlower.with(TallPlantBlock.HALF, DoubleBlockHalf.UPPER), false);
							}
						}
					}
				}
			}
		}
		
		public BlockState getRandomFlower(ChunkRandom random) {
			return WeightedBlockGroupDataLoader.INSTANCE.getEntry(FLOWERS_GROUP, random);
		}
		
		public BlockState getRandomTallFlower(DynamicRegistryManager registryManager, ChunkRandom random, BlockPos pos) {
			return WeightedBlockGroupDataLoader.INSTANCE.getEntry(TALL_FLOWERS_GROUP, random);
		}
		
		@Override
		public void populateEntities(ChunkPos chunkPos, ChunkRegion chunkRegion, ChunkRandom chunkRandom) {
			if (isCenterInChunk(chunkPos)) {
				if (queenBeehiveBlockEntity != null) {
					queenBeehiveBlockEntity.addBee(getBee());
				}
				
				for (BeehiveBlockEntity beehiveBlockEntity : this.outerBeehiveBlockEntities) {
					int beeCount = 2 + random.nextInt(2);
					for (int j = 0; j < beeCount; ++j) {
						beehiveBlockEntity.addBee(getBee());
					}
				}
			}
		}
		
		public BeehiveBlockEntity.BeeData getBee() {
			return BeehiveBlockEntity.BeeData.create(random.nextInt(599));
		}
		
		@Override
		public String getDescription(DynamicRegistryManager registryManager) {
			return "+++ BeeHiveSphere +++" +
					"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
					"\nTemplateID: " + this.getID(registryManager) +
					"\nRadius: " + this.radius +
					"\nShellRadius: " + this.shellThickness +
					"\nFlowerRingRadius: " + this.flowerRingRadius +
					"\nFlowerRingSpacing: " + this.flowerRingSpacing;
		}
	}
	
}
	
