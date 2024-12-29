package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.loot.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.floatprovider.*;
import net.minecraft.util.math.intprovider.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;

import java.util.*;

public class StrongholdSphere extends Sphere<StrongholdSphere.Config> {
	
	private static final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();
	private static final BlockState STONE_BRICKS = Blocks.STONE_BRICKS.getDefaultState();
	private static final BlockState INFESTED_STONE_BRICKS = Blocks.INFESTED_STONE_BRICKS.getDefaultState();
	private static final BlockState MOSSY_STONE_BRICKS = Blocks.MOSSY_STONE_BRICKS.getDefaultState();
	private static final BlockState OAK_PLANKS = Blocks.OAK_PLANKS.getDefaultState();
	private static final BlockState END_PORTAL_FRAME = Blocks.END_PORTAL_FRAME.getDefaultState();
	private static final BlockState LAVA = Blocks.LAVA.getDefaultState();
	private static final BlockState IRON_BARS = Blocks.IRON_BARS.getDefaultState();
	private static final BlockState BOOKSHELF = Blocks.BOOKSHELF.getDefaultState();
	
	public StrongholdSphere(Codec<StrongholdSphere.Config> codec) {
		super(codec);
	}
	
	@Override
	public PlacedSphere<?> generate(ConfiguredSphere<? extends Sphere<StrongholdSphere.Config>, Config> configuredSphere, Config config, ChunkRandom random, DynamicRegistryManager registryManager) {
		return new StrongholdSphere.Placed(configuredSphere, configuredSphere.getSize(random), configuredSphere.getDecorators(random), configuredSphere.getSpawns(random), random, config.shellThickness.get(random));
	}
	
	public static class Config extends SphereConfig {
		
		public static final Codec<StrongholdSphere.Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				SphereConfig.CONFIG_CODEC.forGetter((config) -> config),
				IntProvider.POSITIVE_CODEC.fieldOf("shell_thickness").forGetter((config) -> config.shellThickness)
		).apply(instance, (sphereConfig, shellThickness) -> new Config(sphereConfig.size, sphereConfig.decorators, sphereConfig.spawns, sphereConfig.generation, shellThickness)));
		
		protected final IntProvider shellThickness;
		
		public Config(FloatProvider size, Map<ConfiguredSphereDecorator<?, ?>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation, IntProvider shellThickness) {
			super(size, decorators, spawns, generation);
			this.shellThickness = shellThickness;
		}
		
	}
	
	public static class Placed extends PlacedSphere<StrongholdSphere.Config> {
		
		private final float shellRadius;
		private final ArrayList<BlockPos> interiorDecoratorPositions = new ArrayList<>();
		private BlockPos portalPosition;
		
		public Placed(ConfiguredSphere<? extends Sphere<StrongholdSphere.Config>, StrongholdSphere.Config> configuredSphere, float radius, List<ConfiguredSphereDecorator<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random, float shellRadius) {
			super(configuredSphere, radius, decorators, spawns, random);
			this.shellRadius = shellRadius;
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
							if (y2 % 10 == (this.position.getY() + 8) % 10 || x2 % 10 == (this.position.getX() + 5) % 10 || z2 % 10 == (this.position.getZ() + 5) % 10) {
								if ((y2 - y) % 6 == 0 && ((x2 - x) % 4 == 2 || (z2 - z) % 4 == 0)) {
									chunk.setBlockState(currBlockPos, MOSSY_STONE_BRICKS, false);
								} else {
									chunk.setBlockState(currBlockPos, STONE_BRICKS, false);
								}
							}
						} else {
							if (y2 % 2 == 0) {
								if (x2 % 5 == 0) {
									chunk.setBlockState(currBlockPos, INFESTED_STONE_BRICKS, false);
								} else {
									chunk.setBlockState(currBlockPos, STONE_BRICKS, false);
								}
							} else {
								if (x2 % 2 == 0 && z2 % 2 == 0) {
									chunk.setBlockState(currBlockPos, MOSSY_STONE_BRICKS, false);
								} else {
									chunk.setBlockState(currBlockPos, STONE_BRICKS, false);
								}
							}
						}
						if (d < this.getRadius() - 9 && (y2 % 10 == (this.position.getY() + 9) % 10 && x2 % 10 == (this.position.getX()) % 10 && z2 % 10 == (this.position.getZ()) % 10)) {
							if (d == 1) {
								// place end portal in center
								portalPosition = currBlockPos;
							} else {
								interiorDecoratorPositions.add(currBlockPos);
							}
						}
					}
				}
			}
		}
		
		@Override
		public String getDescription(DynamicRegistryManager registryManager) {
			return "+++ StrongholdSphere +++" +
					"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
					"\nTemplateID: " + this.getID(registryManager) +
					"\nRadius: " + this.radius +
					"\nShellRadius: " + this.shellRadius;
		}
		
		@Override
		public void decorate(StructureWorldAccess world, BlockPos origin, Random random) {
			ChunkPos thisChunkPos = new ChunkPos(this.position);
			ChunkPos originChunkPos = new ChunkPos(origin);
			
			if (portalPosition != null && thisChunkPos.equals(originChunkPos)) {
				placeEndPortal(world, portalPosition.up());
			}
			
			for (BlockPos interiorDecoratorPosition : interiorDecoratorPositions) {
				if (Support.isBlockPosInChunkPos(originChunkPos, interiorDecoratorPosition)) {
					int randomStructure = random.nextInt(5);
					switch (randomStructure) {
						case 0 -> placeLibrary(world, interiorDecoratorPosition);
						case 1 -> placeCorridor(world, interiorDecoratorPosition);
						case 2 -> placeCrossing(world, interiorDecoratorPosition);
						case 3 -> placePrison(world, interiorDecoratorPosition);
						default -> placeFullCube(world, interiorDecoratorPosition);
					}
				}
			}
		}
		
		private void placeEndPortal(WorldAccess worldAccess, BlockPos blockPos) {
			for (int x2 = -3; x2 <= 3; x2++) {
				for (int z2 = -3; z2 <= 3; z2++) {
					
					BlockPos destinationBlockPos = blockPos.add(x2, 0, z2);
					if ((Math.abs(x2) == 3 || Math.abs(z2) == 3)) {
						if (!(Math.abs(x2) == 3 && Math.abs(z2) == 3)) {
							worldAccess.setBlockState(destinationBlockPos.down(), STONE_BRICKS, 3);
						}
					} else if (!(Math.abs(x2) == 2 && Math.abs(z2) == 2)) {
						if ((Math.abs(x2) == 2 || Math.abs(z2) == 2)) {
							// Place end portal
							Direction direction;
							if (x2 == -2) {
								direction = Direction.EAST;
							} else if (x2 == 2) {
								direction = Direction.WEST;
							} else if (z2 == -2) {
								direction = Direction.SOUTH;
							} else {
								direction = Direction.NORTH;
							}
							
							if (random.nextBoolean()) {
								worldAccess.setBlockState(destinationBlockPos, END_PORTAL_FRAME.with(EndPortalFrameBlock.FACING, direction), 3);
							} else {
								worldAccess.setBlockState(destinationBlockPos, END_PORTAL_FRAME.with(EndPortalFrameBlock.FACING, direction).with(EndPortalFrameBlock.EYE, true), 3);
							}
							worldAccess.setBlockState(destinationBlockPos.down(), STONE_BRICKS, 3);
							worldAccess.setBlockState(destinationBlockPos.down(2), STONE_BRICKS, 3);
						} else {
							worldAccess.setBlockState(destinationBlockPos, AIR, 3);
							worldAccess.setBlockState(destinationBlockPos.down(), AIR, 3);
							worldAccess.setBlockState(destinationBlockPos.down(2), LAVA, 3);
							worldAccess.setBlockState(destinationBlockPos.down(3), STONE_BRICKS, 3);
						}
						worldAccess.setBlockState(destinationBlockPos.up(), AIR, 3);
						worldAccess.setBlockState(destinationBlockPos.up(2), AIR, 3);
						worldAccess.setBlockState(destinationBlockPos.up(3), AIR, 3);
					} else {
						placeSpawner(worldAccess, destinationBlockPos.down(2), EntityType.SILVERFISH);
					}
				}
			}
		}
		
		private void placeFullCube(WorldAccess worldAccess, BlockPos blockPos) {
			for (int x2 = -4; x2 < 5; x2++) {
				for (int y2 = 0; y2 < 9; y2++) {
					for (int z2 = -4; z2 < 5; z2++) {
						BlockPos destinationBlockPos = blockPos.add(x2, y2, z2);
						worldAccess.setBlockState(destinationBlockPos, INFESTED_STONE_BRICKS, 3);
					}
				}
			}
		}
		
		private void placeLibrary(WorldAccess worldAccess, BlockPos blockPos) {
			for (int x2 = -4; x2 < 5; x2++) {
				for (int y2 = 0; y2 < 4; y2++) {
					for (int z2 = -4; z2 < 5; z2++) {
						if (Math.abs(x2) == 4 || Math.abs(z2) == 4 || (Math.abs(x2 % 2) == 1 && Math.abs(z2 % 2) == 1)) {
							BlockPos destinationBlockPos = blockPos.add(x2, y2, z2);
							if (y2 == 3) {
								worldAccess.setBlockState(destinationBlockPos, OAK_PLANKS, 3);
							} else {
								worldAccess.setBlockState(destinationBlockPos, BOOKSHELF, 3);
							}
						}
					}
				}
			}
			placeCenterChestWithLootTable(worldAccess.getChunk(blockPos), blockPos, LootTables.STRONGHOLD_LIBRARY_CHEST, random, false);
		}
		
		private void placePrison(WorldAccess worldAccess, BlockPos blockPos) {
			for (int x2 = -4; x2 < 5; x2++) {
				for (int y2 = 0; y2 < 9; y2++) {
					BlockPos destinationBlockPos = blockPos.add(x2, y2, 0);
					worldAccess.setBlockState(destinationBlockPos, IRON_BARS.with(PaneBlock.EAST, true).with(PaneBlock.WEST, true), 3);
				}
			}
			for (int y2 = 0; y2 < 9; y2++) {
				for (int z2 = -4; z2 < 5; z2++) {
					BlockPos destinationBlockPos = blockPos.add(0, y2, z2);
					worldAccess.setBlockState(destinationBlockPos, IRON_BARS.with(PaneBlock.NORTH, true).with(PaneBlock.SOUTH, true), 3);
				}
			}
			
			for (int y2 = 0; y2 < 9; y2++) {
				BlockPos destinationBlockPos = blockPos.add(0, y2, 0);
				worldAccess.setBlockState(destinationBlockPos, IRON_BARS.with(PaneBlock.EAST, true).with(PaneBlock.WEST, true).with(PaneBlock.NORTH, true).with(PaneBlock.SOUTH, true), 3);
			}
		}
		
		private void placeCrossing(WorldAccess worldAccess, BlockPos blockPos) {
			for (int x2 = -1; x2 < 2; x2++) {
				for (int y2 = 0; y2 < 3; y2++) {
					for (int z2 = -1; z2 < 2; z2++) {
						BlockPos destinationBlockPos = blockPos.add(x2, y2, z2);
						worldAccess.setBlockState(destinationBlockPos, MOSSY_STONE_BRICKS, 3);
					}
				}
			}
			
			placeCenterChestWithLootTable(worldAccess.getChunk(blockPos), blockPos, LootTables.STRONGHOLD_CROSSING_CHEST, random, false);
		}
		
		private void placeCorridor(WorldAccess worldAccess, BlockPos blockPos) {
			for (int x2 = -1; x2 < 2; x2++) {
				for (int y2 = 0; y2 < 9; y2++) {
					for (int z2 = -1; z2 < 2; z2++) {
						BlockState blockState;
						BlockPos destinationBlockPos = blockPos.add(x2, y2, z2);
						if (y2 == 0 || (Math.abs(x2) == 1 && Math.abs(z2) == 1)) {
							blockState = STONE_BRICKS;
						} else if (y2 < 4) {
							if (x2 == -1 || x2 == 1) {
								blockState = IRON_BARS.with(PaneBlock.SOUTH, true).with(PaneBlock.NORTH, true);
							} else if (z2 == -1 || z2 == 1) {
								blockState = IRON_BARS.with(PaneBlock.WEST, true).with(PaneBlock.EAST, true);
							} else {
								blockState = AIR;
							}
						} else {
							blockState = STONE_BRICKS;
						}
						worldAccess.setBlockState(destinationBlockPos, blockState, 3);
					}
				}
			}
			
			placeCenterChestWithLootTable(worldAccess.getChunk(blockPos), blockPos.up(), LootTables.STRONGHOLD_CORRIDOR_CHEST, random, false);
		}
		
	}
	
}
	
