package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.component.*;
import net.minecraft.component.type.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.loot.*;
import net.minecraft.potion.*;
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

public class EndCitySphere extends Sphere<EndCitySphere.Config> {
	
	private static final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();
	private static final BlockState PURPUR_BLOCK = Blocks.PURPUR_BLOCK.getDefaultState();
	private static final BlockState PURPUR_PILLAR = Blocks.PURPUR_PILLAR.getDefaultState();
	private static final BlockState MAGENTA_STAINED_GLASS = Blocks.MAGENTA_STAINED_GLASS.getDefaultState();
	private static final BlockState END_STONE_BRICKS = Blocks.END_STONE_BRICKS.getDefaultState();
	private static final BlockState DRAGON_WALL_HEAD = Blocks.DRAGON_WALL_HEAD.getDefaultState();
	
	public EndCitySphere(Codec<EndCitySphere.Config> codec) {
		super(codec);
	}
	
	@Override
	public PlacedSphere<?> generate(ConfiguredSphere<? extends Sphere<EndCitySphere.Config>, Config> configuredSphere, Config config, ChunkRandom random, DynamicRegistryManager registryManager, BlockPos pos, float radius) {
		return new EndCitySphere.Placed(configuredSphere, radius, configuredSphere.getDecorators(random), configuredSphere.getSpawns(random), random, config.shellThickness.get(random));
	}
	
	public static class Config extends SphereConfig {
		
		public static final Codec<EndCitySphere.Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				SphereConfig.CONFIG_CODEC.forGetter((config) -> config),
				IntProvider.POSITIVE_CODEC.fieldOf("shell_thickness").forGetter((config) -> config.shellThickness)
		).apply(instance, (sphereConfig, shellThickness) -> new Config(sphereConfig.size, sphereConfig.decorators, sphereConfig.spawns, sphereConfig.generation, shellThickness)));
		
		protected final IntProvider shellThickness;
		
		public Config(FloatProvider size, Map<RegistryEntry<ConfiguredSphereDecorator<?, ?>>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation, IntProvider shellThickness) {
			super(size, decorators, spawns, generation);
			this.shellThickness = shellThickness;
		}
		
	}
	
	public static class Placed extends PlacedSphere<EndCitySphere.Config> {
		
		private final float shellRadius;
		
		public Placed(ConfiguredSphere<? extends Sphere<EndCitySphere.Config>, EndCitySphere.Config> configuredSphere, float radius, List<RegistryEntry<ConfiguredSphereDecorator<?, ?>>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random, float shellRadius) {
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
						
						if (d <= (this.radius - this.shellRadius)) {
							chunk.setBlockState(currBlockPos, PURPUR_BLOCK, false);
						} else {
							if (y2 % 2 == 0) {
								chunk.setBlockState(currBlockPos, END_STONE_BRICKS, false);
							} else {
								chunk.setBlockState(currBlockPos, PURPUR_BLOCK, false);
							}
						}
					}
				}
			}
		}
		
		@Override
		public String getDescription(DynamicRegistryManager registryManager) {
			return "+++ EndCitySphere +++" +
					"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
					"\nTemplateID: " + this.getID(registryManager) +
					"\nRadius: " + this.radius +
					"\nShellRadius: " + this.shellRadius;
		}
		
		/*
		 * EndCitySphere uses the decorator to place all the
		 * internal rooms more easily
		 *
		 * @param world The world
		 * @param random The decoration random
		 */
		@Override
		public void decorate(StructureWorldAccess world, BlockPos origin, Random random) {
			super.decorate(world, origin, random);
			
			placeSolid(world, position);
			placeEmpty(world, position.add(0, 12, 0));
			placeElytra(world, position.add(0, 24, 0));
			placeTreasure(world, position.add(0, 36, 0));
			placeBrewingStand(world, position.add(0, 48, 0));
			placeDragonHead(world, position.add(0, 64, 0));
			placeShulkerSpawner(world, position.add(0, 72, 0));
			
			/*
			ChunkPos originChunkPos = new ChunkPos(origin);
			for (BlockPos interiorDecoratorPosition : interiorDecoratorPositions) {
				if (Support.isBlockPosInChunkPos(originChunkPos, interiorDecoratorPosition)) {
					int randomStructure = random.nextInt(8);
					switch (randomStructure) {
						case 0 -> placeSolid(world, interiorDecoratorPosition);
						case 1 -> placeEmpty(world, interiorDecoratorPosition);
						case 2 -> placeElytra(world, interiorDecoratorPosition);
						case 3 -> placeTreasure(world, interiorDecoratorPosition);
						case 4 -> placeBrewingStand(world, interiorDecoratorPosition);
						case 5 -> placeDragonHead(world, interiorDecoratorPosition);
						default -> // double chance
								placeShulkerSpawner(world, interiorDecoratorPosition);
					}
				}
			}*/
		}
		
		
		private void placeSolid(WorldAccess worldAccess, BlockPos blockPos) {
			for (int x2 = -4; x2 < 5; x2++) {
				for (int y2 = 0; y2 < 9; y2++) {
					for (int z2 = -4; z2 < 5; z2++) {
						BlockPos destinationBlockPos = blockPos.add(x2, y2, z2);
						worldAccess.setBlockState(destinationBlockPos, PURPUR_BLOCK, 3);
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
		
		private void placeShulkerSpawner(StructureWorldAccess world, BlockPos blockPos) {
			for (int x2 = -4; x2 < 5; x2++) {
				for (int y2 = 0; y2 < 9; y2++) {
					for (int z2 = -4; z2 < 5; z2++) {
						BlockPos destinationBlockPos = blockPos.add(x2, y2, z2);
						world.setBlockState(destinationBlockPos, AIR, 3);
					}
				}
			}
			
			BlockPos spawnerPos = blockPos.up(5);
			for (int x2 = -1; x2 < 2; x2++) {
				for (int y2 = -1; y2 < 2; y2++) {
					for (int z2 = -1; z2 < 2; z2++) {
						BlockPos destinationBlockPos = spawnerPos.add(x2, y2, z2);
						world.setBlockState(destinationBlockPos, MAGENTA_STAINED_GLASS, 3);
					}
				}
			}
			
			world.setBlockState(spawnerPos.up(1), PURPUR_PILLAR, 3);
			world.setBlockState(spawnerPos.up(2), PURPUR_PILLAR, 3);
			world.setBlockState(spawnerPos.up(3), PURPUR_PILLAR, 3);
			
			placeSpawner(world, spawnerPos, EntityType.SHULKER);
		}
		
		private void placeBrewingStand(WorldAccess worldAccess, BlockPos blockPos) {
			for (int x2 = -4; x2 < 5; x2++) {
				for (int y2 = 0; y2 < 9; y2++) {
					for (int z2 = -4; z2 < 5; z2++) {
						BlockPos destinationBlockPos = blockPos.add(x2, y2, z2);
						worldAccess.setBlockState(destinationBlockPos, AIR, 3);
					}
				}
			}
			
			worldAccess.setBlockState(blockPos, PURPUR_PILLAR, 3);
			worldAccess.setBlockState(blockPos.up(), Blocks.BREWING_STAND.getDefaultState(), 3);
			
			ItemStack healingPotionStack = new ItemStack(Items.POTION, 1);
			healingPotionStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Potions.STRONG_HEALING));
			
			if (worldAccess.getBlockEntity(blockPos.up()) instanceof BrewingStandBlockEntity brewingStand) {
				brewingStand.setStack(0, healingPotionStack.copy());
				brewingStand.setStack(1, healingPotionStack.copy());
				brewingStand.setStack(2, healingPotionStack.copy());
			}
		}
		
		private void placeTreasure(WorldAccess worldAccess, BlockPos blockPos) {
			for (int x2 = -4; x2 < 5; x2++) {
				for (int y2 = 0; y2 < 9; y2++) {
					for (int z2 = -4; z2 < 5; z2++) {
						BlockPos destinationBlockPos = blockPos.add(x2, y2, z2);
						worldAccess.setBlockState(destinationBlockPos, AIR, 3);
					}
				}
			}
			
			BlockState enderChestBlockState = Blocks.ENDER_CHEST.getDefaultState();
			BlockPos randomPos = blockPos.add(random.nextInt(9) - 4, 0, random.nextInt(9) - 4);
			worldAccess.setBlockState(randomPos, enderChestBlockState, 3);
			
			// may override the ender chest in very rare circumstances
			placeCenterChestWithLootTable(worldAccess.getChunk(blockPos), blockPos, LootTables.END_CITY_TREASURE_CHEST, random, false);
		}
		
		private void placeElytra(WorldAccess worldAccess, BlockPos blockPos) {
			for (int x2 = -4; x2 < 5; x2++) {
				for (int y2 = 0; y2 < 9; y2++) {
					for (int z2 = -4; z2 < 5; z2++) {
						BlockPos destinationBlockPos = blockPos.add(x2, y2, z2);
						worldAccess.setBlockState(destinationBlockPos, AIR, 3);
					}
				}
			}
			
			worldAccess.setBlockState(blockPos, PURPUR_PILLAR, 3);
			worldAccess.setBlockState(blockPos.up(), Blocks.CHEST.getDefaultState(), 3);
			ItemStack elytraItemStack = new ItemStack(Items.ELYTRA, 1);
			
			BlockEntity blockEntity = worldAccess.getBlockEntity(blockPos.up());
			if (blockEntity instanceof ChestBlockEntity chestBlockEntity) {
				chestBlockEntity.setStack(0, elytraItemStack);
			}
		}
		
		private void placeDragonHead(WorldAccess worldAccess, BlockPos blockPos) {
			for (int x2 = -4; x2 < 5; x2++) {
				for (int y2 = 0; y2 < 9; y2++) {
					for (int z2 = -4; z2 < 5; z2++) {
						BlockPos destinationBlockPos = blockPos.add(x2, y2, z2);
						worldAccess.setBlockState(destinationBlockPos, AIR, 3);
					}
				}
			}
			
			worldAccess.setBlockState(blockPos, PURPUR_PILLAR, 3);
			worldAccess.setBlockState(blockPos.up(), PURPUR_PILLAR, 3);
			worldAccess.setBlockState(blockPos.up(2), Blocks.END_ROD.getDefaultState().with(EndRodBlock.FACING, Direction.UP), 3);
			
			int randomPosition = random.nextInt(4);
			BlockState dragonHeadBlockState;
			switch (randomPosition) {
				case 0 -> {
					dragonHeadBlockState = DRAGON_WALL_HEAD.with(WallSkullBlock.FACING, Direction.NORTH);
					worldAccess.setBlockState(blockPos.up().north(), dragonHeadBlockState, 3);
				}
				case 1 -> {
					dragonHeadBlockState = DRAGON_WALL_HEAD.with(WallSkullBlock.FACING, Direction.EAST);
					worldAccess.setBlockState(blockPos.up().east(), dragonHeadBlockState, 3);
				}
				case 2 -> {
					dragonHeadBlockState = DRAGON_WALL_HEAD.with(WallSkullBlock.FACING, Direction.SOUTH);
					worldAccess.setBlockState(blockPos.up().south(), dragonHeadBlockState, 3);
				}
				default -> {
					dragonHeadBlockState = DRAGON_WALL_HEAD.with(WallSkullBlock.FACING, Direction.WEST);
					worldAccess.setBlockState(blockPos.up().west(), dragonHeadBlockState, 3);
				}
			}
		}
	}
	
}
	
