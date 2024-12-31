package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.loot.*;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.floatprovider.*;
import net.minecraft.util.math.intprovider.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;

import java.util.*;

public class NetherFortressSphere extends Sphere<NetherFortressSphere.Config> {
	
	private static final BlockState NETHER_BRICKS = Blocks.NETHER_BRICKS.getDefaultState();
	private static final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();
	private static final BlockState NETHER_WART = Blocks.NETHER_WART.getDefaultState();
	private static final BlockState SOUL_SAND = Blocks.SOUL_SAND.getDefaultState();
	private static final BlockState NETHER_BRICK_FENCE = Blocks.NETHER_BRICK_FENCE.getDefaultState();
	private static final BlockState LAVA = Blocks.LAVA.getDefaultState();
	
	public NetherFortressSphere(Codec<NetherFortressSphere.Config> codec) {
		super(codec);
	}
	
	@Override
	public PlacedSphere<?> generate(ConfiguredSphere<? extends Sphere<NetherFortressSphere.Config>, Config> configuredSphere, Config config, ChunkRandom random, DynamicRegistryManager registryManager, BlockPos pos, float radius) {
		return new NetherFortressSphere.Placed(configuredSphere, radius, configuredSphere.getDecorators(random), configuredSphere.getSpawns(random), random, config.shellThickness.get(random));
	}
	
	public static class Config extends SphereConfig {
		
		public static final Codec<NetherFortressSphere.Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				SphereConfig.CONFIG_CODEC.forGetter((config) -> config),
				IntProvider.POSITIVE_CODEC.fieldOf("shell_thickness").forGetter((config) -> config.shellThickness)
		).apply(instance, (sphereConfig, shellThickness) -> new Config(sphereConfig.size, sphereConfig.decorators, sphereConfig.spawns, sphereConfig.generation, shellThickness)));
		
		protected final IntProvider shellThickness;
		
		public Config(FloatProvider size, Map<RegistryEntry<ConfiguredSphereDecorator<?, ?>>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation, IntProvider shellThickness) {
			super(size, decorators, spawns, generation);
			this.shellThickness = shellThickness;
		}
		
	}
	
	public static class Placed extends PlacedSphere<NetherFortressSphere.Config> {
		
		private final float shellRadius;
		
		private final List<BlockPos> interiorDecoratorPositions = new ArrayList<>();
		
		public Placed(ConfiguredSphere<? extends Sphere<NetherFortressSphere.Config>, NetherFortressSphere.Config> configuredSphere, float radius, List<RegistryEntry<ConfiguredSphereDecorator<?, ?>>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random, float shellRadius) {
			super(configuredSphere, radius, decorators, spawns, random);
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
			
			BlockPos.Mutable currBlockPos = new BlockPos.Mutable();
			for (int x2 = Math.max(chunkX * 16, x - ceiledRadius); x2 <= maxX; x2++) {
				for (int y2 = y - ceiledRadius; y2 <= y + ceiledRadius; y2++) {
					for (int z2 = Math.max(chunkZ * 16, z - ceiledRadius); z2 <= maxZ; z2++) {
						long d = Math.round(Support.getDistance(x, y, z, x2, y2, z2));
						if (d > this.radius) {
							continue;
						}
						
						currBlockPos.set(x2, y2, z2);
						
						if (d <= shellRadius) {
							chunk.setBlockState(currBlockPos, NETHER_BRICKS, false);
						}
						if (d < this.getRadius() - 10 && (y2 % 10 == (this.position.getY() + 9) % 10 && x2 % 10 == (this.position.getX()) % 10 && z2 % 10 == (this.position.getZ()) % 10)) {
							interiorDecoratorPositions.add(currBlockPos);
						}
					}
				}
			}
		}
		
		@Override
		public String getDescription(DynamicRegistryManager registryManager) {
			return "+++ NetherFortressSphere +++" +
					"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
					"\nTemplateID: " + this.getID(registryManager) +
					"\nRadius: " + this.radius +
					"\nShellRadius: " + this.shellRadius;
		}
		
		/**
		 * NetherFortressSphere uses the decorator to place all the
		 * internal rooms more easily
		 *
		 * @param world  The world
		 * @param random The decoration random
		 */
		@Override
		public void decorate(StructureWorldAccess world, BlockPos origin, Random random) {
			super.decorate(world, origin, random);
			
			ChunkPos originChunkPos = new ChunkPos(origin);
			for (BlockPos interiorDecoratorPosition : interiorDecoratorPositions) {
				if (Support.isBlockPosInChunkPos(originChunkPos, interiorDecoratorPosition)) {
					int randomStructure = random.nextInt(7);
					switch (randomStructure) {
						case 0 -> placeBlazeSpawnerRoom(world, interiorDecoratorPosition);
						case 1 -> placeWitherSkeletonRoom(world, interiorDecoratorPosition);
						case 2 -> placeNetherWartRoom(world, interiorDecoratorPosition);
						case 3 -> placeSolid(world, interiorDecoratorPosition);
						case 4 -> placeEmpty(world, interiorDecoratorPosition);
						case 5 -> placeLava(world, interiorDecoratorPosition);
						default -> placeChestRoom(world, interiorDecoratorPosition);
					}
				}
			}
		}
		
		private void placeSolid(WorldAccess worldAccess, BlockPos blockPos) {
			for (int x2 = -4; x2 < 5; x2++) {
				for (int y2 = 0; y2 < 9; y2++) {
					for (int z2 = -4; z2 < 5; z2++) {
						BlockPos destinationBlockPos = blockPos.add(x2, y2, z2);
						worldAccess.setBlockState(destinationBlockPos, NETHER_BRICKS, 3);
					}
				}
			}
		}
		
		private void placeEmpty(WorldAccess worldAccess, BlockPos blockPos) {
			for (int x2 = -4; x2 < 5; x2++) {
				for (int y2 = 0; y2 < 9; y2++) {
					for (int z2 = -4; z2 < 5; z2++) {
						BlockPos destinationBlockPos = blockPos.add(x2, y2, z2);
						worldAccess.setBlockState(destinationBlockPos, AIR, 3);
					}
				}
			}
		}
		
		private void placeLava(WorldAccess worldAccess, BlockPos blockPos) {
			for (int x2 = -4; x2 < 5; x2++) {
				for (int y2 = 0; y2 < 9; y2++) {
					for (int z2 = -4; z2 < 5; z2++) {
						BlockPos destinationBlockPos = blockPos.add(x2, y2, z2);
						worldAccess.setBlockState(destinationBlockPos, LAVA, 3);
					}
				}
			}
		}
		
		private void placeChestRoom(WorldAccess worldAccess, BlockPos blockPos) {
			for (int x2 = -4; x2 < 5; x2++) {
				for (int y2 = 0; y2 < 9; y2++) {
					for (int z2 = -4; z2 < 5; z2++) {
						BlockPos destinationBlockPos = blockPos.add(x2, y2, z2);
						worldAccess.setBlockState(destinationBlockPos, AIR, 3);
					}
				}
			}
			
			placeCenterChestWithLootTable(worldAccess.getChunk(blockPos), blockPos, LootTables.NETHER_BRIDGE_CHEST, random, false);
		}
		
		private void placeBlazeSpawnerRoom(WorldAccess worldAccess, BlockPos blockPos) {
			for (int x2 = -4; x2 < 5; x2++) {
				for (int y2 = 0; y2 < 9; y2++) {
					for (int z2 = -4; z2 < 5; z2++) {
						BlockPos destinationBlockPos = blockPos.add(x2, y2, z2);
						worldAccess.setBlockState(destinationBlockPos, AIR, 3);
					}
				}
			}
			
			BlockPos spawnerPos = blockPos.up(4);
			for (int x2 = -1; x2 < 2; x2++) {
				for (int y2 = -1; y2 < 2; y2++) {
					for (int z2 = -1; z2 < 2; z2++) {
						BlockPos destinationBlockPos = spawnerPos.add(x2, y2, z2);
						worldAccess.setBlockState(destinationBlockPos, NETHER_BRICK_FENCE, 3);
					}
				}
			}
			
			worldAccess.setBlockState(spawnerPos.up(2), NETHER_BRICK_FENCE, 3);
			worldAccess.setBlockState(spawnerPos.up(3), NETHER_BRICK_FENCE, 3);
			worldAccess.setBlockState(spawnerPos.up(4), NETHER_BRICK_FENCE, 3);
			
			placeSpawner(worldAccess, spawnerPos, EntityType.BLAZE);
		}
		
		private void placeWitherSkeletonRoom(WorldAccess worldAccess, BlockPos blockPos) {
			for (int x2 = -4; x2 < 5; x2++) {
				for (int y2 = 0; y2 < 9; y2++) {
					for (int z2 = -4; z2 < 5; z2++) {
						BlockPos destinationBlockPos = blockPos.add(x2, y2, z2);
						worldAccess.setBlockState(destinationBlockPos, AIR, 3);
					}
				}
			}
			
			BlockPos spawnerPos = blockPos.up(4);
			for (int x2 = -1; x2 < 2; x2++) {
				for (int y2 = -1; y2 < 2; y2++) {
					for (int z2 = -1; z2 < 2; z2++) {
						BlockPos destinationBlockPos = spawnerPos.add(x2, y2, z2);
						worldAccess.setBlockState(destinationBlockPos, NETHER_BRICK_FENCE, 3);
					}
				}
			}
			
			worldAccess.setBlockState(spawnerPos.up(2), NETHER_BRICK_FENCE, 3);
			worldAccess.setBlockState(spawnerPos.up(3), NETHER_BRICK_FENCE, 3);
			worldAccess.setBlockState(spawnerPos.up(4), NETHER_BRICK_FENCE, 3);
			
			placeSpawner(worldAccess, spawnerPos, EntityType.WITHER_SKELETON);
		}
		
		private void placeNetherWartRoom(WorldAccess worldAccess, BlockPos blockPos) {
			for (int x2 = -4; x2 < 5; x2++) {
				for (int y2 = 0; y2 < 9; y2++) {
					for (int z2 = -4; z2 < 5; z2++) {
						BlockPos destinationBlockPos = blockPos.add(x2, y2, z2);
						worldAccess.setBlockState(destinationBlockPos, AIR, 3);
					}
				}
			}
			
			for (int x2 = -4; x2 < 5; x2++) {
				for (int z2 = -4; z2 < 5; z2++) {
					BlockPos destinationBlockPos = blockPos.add(x2, 0, z2);
					if (Math.abs(x2) < 3 && Math.abs(z2) < 3) {
						worldAccess.setBlockState(destinationBlockPos, SOUL_SAND, 3);
						int randomAge = random.nextInt(3);
						worldAccess.setBlockState(destinationBlockPos.up(), NETHER_WART.with(NetherWartBlock.AGE, randomAge), 3);
					} else {
						worldAccess.setBlockState(destinationBlockPos, NETHER_BRICKS, 3);
					}
				}
			}
		}
		
	}
	
}
	
