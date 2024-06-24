package de.dafuqs.starryskies.spheroids.spheroids;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.loot.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;

import java.util.*;

public class NetherFortressSpheroid extends Spheroid {
	
	private final int shellRadius;
	private final ArrayList<BlockPos> interiorDecoratorPositions = new ArrayList<>();
	
	private final BlockState NETHER_BRICKS = Blocks.NETHER_BRICKS.getDefaultState();
	private final BlockState AIR = Blocks.AIR.getDefaultState();
	private final BlockState NETHER_WART = Blocks.NETHER_WART.getDefaultState();
	private final BlockState SOUL_SAND = Blocks.SOUL_SAND.getDefaultState();
	private final BlockState NETHER_BRICK_FENCE = Blocks.NETHER_BRICK_FENCE.getDefaultState();
	private final BlockState LAVA = Blocks.LAVA.getDefaultState();
	
	public NetherFortressSpheroid(Spheroid.Template<?> template, float radius, List<ConfiguredSpheroidFeature<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
								  int shellRadius) {
		
		super(template, radius, decorators, spawns, random);
		this.shellRadius = shellRadius;
	}
	
	@Override
	public String getDescription() {
		return "+++ NetherFortressSpheroid +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.template.getID() +
				"\nRadius: " + this.radius +
				"\nShellRadius: " + this.shellRadius;
	}
	
	@Override
	public void generate(Chunk chunk) {
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;
		
		int x = this.getPosition().getX();
		int y = this.getPosition().getY();
		int z = this.getPosition().getZ();
		
		float shellDistance = this.radius - this.shellRadius;
		
		random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
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
					
					if (d <= shellDistance) {
						chunk.setBlockState(currBlockPos, NETHER_BRICKS, false);
					}
					if (d < this.getRadius() - 10 && (y2 % 10 == (this.position.getY() + 9) % 10 && x2 % 10 == (this.position.getX()) % 10 && z2 % 10 == (this.position.getZ()) % 10)) {
						interiorDecoratorPositions.add(currBlockPos);
					}
				}
			}
		}
	}
	
	/**
	 * NetherFortressSpheroid uses the decorator to place all the
	 * internal rooms more easily
	 *
	 * @param world  The world
	 * @param random The decoration random
	 */
	@Override
	public void decorate(StructureWorldAccess world, BlockPos origin, Random random) {
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
	
	public static class Template extends Spheroid.Template<NetherFortressSpheroid.Template.Config> {
		
		public static MapCodec<NetherFortressSpheroid.Template> CODEC = createCodec(NetherFortressSpheroid.Template.Config.CODEC, NetherFortressSpheroid.Template::new);
		private final int minShellRadius;
		private final int maxShellRadius;
		
		public Template(SharedConfig shared, NetherFortressSpheroid.Template.Config config) {
			super(shared);
			this.minShellRadius = config.minShellRadius;
			this.maxShellRadius = config.maxShellRadius;
		}
		
		@Override
		public SpheroidTemplateType<NetherFortressSpheroid.Template> getType() {
			return SpheroidTemplateType.NETHER_FORTRESS;
		}
		
		@Override
		public NetherFortressSpheroid.Template.Config config() {
			return new NetherFortressSpheroid.Template.Config(minShellRadius, maxShellRadius);
		}
		
		@Override
		public NetherFortressSpheroid generate(ChunkRandom random) {
			int shellRadius = Support.getRandomBetween(random, this.minShellRadius, this.maxShellRadius);
			return new NetherFortressSpheroid(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, shellRadius);
		}
		
		public record Config(int minShellRadius, int maxShellRadius) {
			public static MapCodec<NetherFortressSpheroid.Template.Config> CODEC = RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							Codec.INT.fieldOf("min_shell_size").forGetter(NetherFortressSpheroid.Template.Config::minShellRadius),
							Codec.INT.fieldOf("max_shell_size").forGetter(NetherFortressSpheroid.Template.Config::maxShellRadius)
					).apply(instance, NetherFortressSpheroid.Template.Config::new)
			);
		}
		
	}
	
}
