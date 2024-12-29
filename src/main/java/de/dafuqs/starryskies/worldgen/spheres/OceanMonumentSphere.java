package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.floatprovider.*;
import net.minecraft.util.math.intprovider.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;

import java.util.*;

public class OceanMonumentSphere extends Sphere<OceanMonumentSphere.Config> {
	
	private static final BlockState WATER = Blocks.WATER.getDefaultState();
	private static final BlockState PRISMARINE = Blocks.PRISMARINE.getDefaultState();
	private static final BlockState PRISMARINE_BRICKS = Blocks.PRISMARINE_BRICKS.getDefaultState();
	private static final BlockState DARK_PRISMARINE = Blocks.DARK_PRISMARINE.getDefaultState();
	private static final BlockState SEA_LANTERN = Blocks.SEA_LANTERN.getDefaultState();
	private static final BlockState TREASURE = Blocks.WET_SPONGE.getDefaultState();
	
	public OceanMonumentSphere(Codec<OceanMonumentSphere.Config> codec) {
		super(codec);
	}
	
	@Override
	public PlacedSphere<?> generate(ConfiguredSphere<? extends Sphere<OceanMonumentSphere.Config>, Config> configuredSphere, Config config, ChunkRandom random, DynamicRegistryManager registryManager, BlockPos pos, float radius) {
		return new OceanMonumentSphere.Placed(configuredSphere, radius, configuredSphere.getDecorators(random), configuredSphere.getSpawns(random), random, config.coreRadius.get(random), config.shellThickness.get(random));
	}
	
	public static class Config extends SphereConfig {
		
		public static final Codec<OceanMonumentSphere.Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				SphereConfig.CONFIG_CODEC.forGetter((config) -> config),
				FloatProvider.createValidatedCodec(1.0F, 32.0F).fieldOf("core_radius").forGetter((config) -> config.coreRadius),
				IntProvider.POSITIVE_CODEC.fieldOf("shell_thickness").forGetter((config) -> config.shellThickness)
		).apply(instance, (sphereConfig, coreRadius, shellThickness) -> new Config(sphereConfig.size, sphereConfig.decorators, sphereConfig.spawns, sphereConfig.generation, coreRadius, shellThickness)));
		
		protected final FloatProvider coreRadius;
		protected final IntProvider shellThickness;
		
		public Config(FloatProvider size, Map<ConfiguredSphereDecorator<?, ?>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation, FloatProvider coreRadius, IntProvider shellThickness) {
			super(size, decorators, spawns, generation);
			this.coreRadius = coreRadius;
			this.shellThickness = shellThickness;
		}
		
	}
	
	public static class Placed extends PlacedSphere<OceanMonumentSphere.Config> {
		
		private final float coreRadius;
		private final float shellRadius;
		
		private final ArrayList<BlockPos> guardianPositions = new ArrayList<>();
		
		public Placed(ConfiguredSphere<? extends Sphere<OceanMonumentSphere.Config>, OceanMonumentSphere.Config> configuredSphere, float radius, List<ConfiguredSphereDecorator<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random, float coreRadius, float shellRadius) {
			super(configuredSphere, radius, decorators, spawns, random);
			this.coreRadius = coreRadius;
			this.shellRadius = shellRadius;
		}
		
		@Override
		public void generate(Chunk chunk, DynamicRegistryManager registryManager) {
			int chunkX = chunk.getPos().x;
			int chunkZ = chunk.getPos().z;
			random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
			BlockPos spherePos = this.getPosition();
			int x = spherePos.getX();
			int y = spherePos.getY();
			int z = spherePos.getZ();
			
			int ceiledRadius = (int) Math.ceil(this.radius);
			int maxX = Math.min(chunkX * 16 + 15, x + ceiledRadius);
			int maxZ = Math.min(chunkZ * 16 + 15, z + ceiledRadius);
			
			float shellDistance = this.radius - this.shellRadius;
			
			BlockPos.Mutable currBlockPos = new BlockPos.Mutable();
			for (int x2 = Math.max(chunkX * 16, x - ceiledRadius); x2 <= maxX; x2++) {
				for (int y2 = y - ceiledRadius; y2 <= y + ceiledRadius; y2++) {
					for (int z2 = Math.max(chunkZ * 16, z - ceiledRadius); z2 <= maxZ; z2++) {
						long d = Math.round(Support.getDistance(x, y, z, x2, y2, z2));
						if (d > this.radius) {
							continue;
						}
						currBlockPos.set(x2, y2, z2);
						
						if (d <= this.coreRadius) {
							chunk.setBlockState(currBlockPos, TREASURE, false);
						} else if (d < coreRadius + 3) {
							chunk.setBlockState(currBlockPos, WATER, false);
						} else if (d == coreRadius + 3) {
							if (Math.abs(x2 - x) < 2 || Math.abs(z2 - z) < 2) {
								chunk.setBlockState(currBlockPos, WATER, false);
							} else {
								chunk.setBlockState(currBlockPos, DARK_PRISMARINE, false);
							}
						} else if (d <= shellDistance) {
							if (y2 % 10 == 0 || x2 % 10 == 0 || z2 % 10 == 0) {
								if ((y2 - y) % 6 == 0 && ((x2 - x) % 4 == 2 || (z2 - z) % 4 == 0)) {
									chunk.setBlockState(currBlockPos, SEA_LANTERN, false);
								} else {
									chunk.setBlockState(currBlockPos, PRISMARINE_BRICKS, false);
								}
							} else {
								if (y2 % 10 == 5 && x2 % 10 == 5 && z2 % 10 == 5) {
									guardianPositions.add(currBlockPos);
								}
								chunk.setBlockState(currBlockPos, WATER, false);
							}
						} else {
							if (y2 % 2 == 0) {
								chunk.setBlockState(currBlockPos, PRISMARINE, false);
							} else {
								chunk.setBlockState(currBlockPos, PRISMARINE_BRICKS, false);
							}
						}
					}
				}
			}
		}
		
		@Override
		public String getDescription(DynamicRegistryManager registryManager) {
			return "+++ OceanMonumentSphere +++" +
					"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
					"\nTemplateID: " + this.getID(registryManager) +
					"\nRadius: " + this.radius +
					"\nCoreRadius: " + this.coreRadius +
					"\nShellRadius: " + this.shellRadius;
		}
		
		@Override
		public void populateEntities(ChunkPos chunkPos, ChunkRegion chunkRegion, ChunkRandom chunkRandom) {
			for (BlockPos guardianPosition : guardianPositions) {
				if (Support.isBlockPosInChunkPos(chunkPos, guardianPosition)) {
					
					MobEntity mobentity;
					if (random.nextFloat() < 0.08) {
						mobentity = EntityType.ELDER_GUARDIAN.create(chunkRegion.toServerWorld());
					} else {
						mobentity = EntityType.GUARDIAN.create(chunkRegion.toServerWorld());
					}
					
					if (mobentity != null) {
						float width = mobentity.getWidth();
						double xLength = MathHelper.clamp(guardianPosition.getX(), (double) chunkPos.getStartX() + (double) width, (double) chunkPos.getStartX() + 16.0D - (double) width);
						double zLength = MathHelper.clamp(guardianPosition.getZ(), (double) chunkPos.getStartZ() + (double) width, (double) chunkPos.getStartZ() + 16.0D - (double) width);
						
						try {
							mobentity.refreshPositionAndAngles(xLength, guardianPosition.getY(), zLength, random.nextFloat() * 360.0F, 0.0F);
							mobentity.setPersistent();
							if (mobentity.canSpawn(chunkRegion, SpawnReason.CHUNK_GENERATION) && mobentity.canSpawn(chunkRegion)) {
								mobentity.initialize(chunkRegion, chunkRegion.getLocalDifficulty(mobentity.getBlockPos()), SpawnReason.CHUNK_GENERATION, null);
								boolean success = chunkRegion.spawnEntity(mobentity);
								if (!success) {
									return;
								}
							}
						} catch (Exception exception) {
							StarrySkies.LOGGER.warn("Failed to spawn mob on sphere {}\nException: {}", this.getDescription(chunkRegion.getRegistryManager()), exception);
						}
					}
				}
			}
		}
		
	}
	
}
	
