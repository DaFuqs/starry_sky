package de.dafuqs.starryskies.spheroids.spheroids;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.command.argument.*;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.*;
import net.minecraft.loot.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public abstract class Spheroid implements Serializable {
	
	protected Spheroid.Template<?> template;
	protected float radius;
	protected List<ConfiguredSpheroidFeature<?, ?>> decorators;
	protected List<Pair<EntityType<?>, Integer>> spawns;
	
	protected BlockPos position;
	protected ChunkRandom random;
	
	public Spheroid(Spheroid.Template<?> template, float radius, List<ConfiguredSpheroidFeature<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random) {
		this.template = template;
		this.radius = radius;
		this.decorators = decorators;
		this.spawns = spawns;
		this.random = random;
	}
	
	public Identifier getID(DynamicRegistryManager registryManager) {
		Registry<Spheroid.Template<?>> registry = registryManager.get(StarryRegistryKeys.SPHEROID_TEMPLATE);
		return registry.getId(this.template);
	}
	
	public abstract void generate(Chunk chunk, DynamicRegistryManager registryManager);
	
	public BlockPos getPosition() {
		return position;
	}
	
	public void setPosition(BlockPos position) {
		this.position = position;
	}
	
	public int getRadius() {
		return Math.round(radius);
	}
	
	public abstract String getDescription(DynamicRegistryManager registryManager);
	
	public boolean isInChunk(@NotNull ChunkPos chunkPos) {
		int radius = getRadius();
		int xMin = this.position.getX() - radius - 16;
		int xMax = this.position.getX() + radius + 15;
		int zMin = this.position.getZ() - radius - 16;
		int zMax = this.position.getZ() + radius + 15;
		return (chunkPos.getStartX() >= xMin && chunkPos.getEndX() <= xMax) && (chunkPos.getStartZ() >= zMin && chunkPos.getEndZ() <= zMax);
	}
	
	public boolean isCenterInChunk(@NotNull ChunkPos chunkPos) {
		return (this.getPosition().getX() >= chunkPos.getStartX()
				&& this.getPosition().getX() <= chunkPos.getStartX() + 15
				&& this.getPosition().getZ() >= chunkPos.getStartZ()
				&& this.getPosition().getZ() <= chunkPos.getStartZ() + 15);
	}
	
	public void decorate(StructureWorldAccess world, BlockPos origin, Random random) {
		if (!this.decorators.isEmpty()) {
			for (ConfiguredSpheroidFeature<?, ?> decorator : this.decorators) {
				StarrySkies.LOGGER.debug("Decorator: {}", decorator.getClass());
				try {
					decorator.generate(world, random, origin, this);
				} catch (RuntimeException e) {
					// Are we asking a region for a chunk out of bounds? ಠ_ಠ
				}
				StarrySkies.LOGGER.debug("Decorator finished");
			}
		}
	}
	
	protected boolean isTopBlock(long d, double x, double y, double z) {
		if (d > this.radius - 1) {
			long dist2 = Math.round(Support.getDistance(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ(), x, y + 1, z));
			return dist2 > this.radius;
		} else {
			return false;
		}
	}
	
	protected boolean isBottomBlock(long d, double x, double y, double z) {
		if (d > this.radius - 1) {
			long dist2 = Math.round(Support.getDistance(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ(), x, y - 1, z));
			return dist2 > this.radius;
		} else {
			return false;
		}
	}
	
	protected boolean isAboveCaveFloorBlock(long d, double x, double y, double z, float shellRadius) {
		int distance1 = (int) Math.round(Support.getDistance(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ(), x, y - 1, z));
		return d == (this.radius - shellRadius) && distance1 > (this.radius - shellRadius);
	}
	
	protected void setBlockResult(Chunk chunk, BlockPos pos, BlockArgumentParser.BlockResult block) {
		chunk.setBlockState(pos, block.blockState(), false);
		if (block.blockState().getBlock() instanceof BlockEntityProvider blockEntityProvider) {
			BlockEntity blockEntity = blockEntityProvider.createBlockEntity(pos, block.blockState());
			if (blockEntity != null) {
				chunk.setBlockEntity(blockEntity);
				blockEntity.read(block.nbt(), StarrySkies.registryManager);
			}
		}
	}
	
	protected void placeCenterChestWithLootTable(Chunk chunk, BlockPos blockPos, RegistryKey<LootTable> lootTable, Random random, boolean waterLogged) {
		BlockState chestBlockState;
		if (waterLogged) {
			chestBlockState = Blocks.CHEST.getDefaultState().with(ChestBlock.WATERLOGGED, true);
		} else {
			chestBlockState = Blocks.CHEST.getDefaultState();
		}
		chunk.setBlockState(blockPos, chestBlockState, false);
		
		LootableContainerBlockEntity blockEntity = new ChestBlockEntity(blockPos, chestBlockState);
		chunk.setBlockEntity(new ChestBlockEntity(blockPos, chestBlockState));
		blockEntity.setLootTable(lootTable, random.nextLong());
	}
	
	public void populateEntities(ChunkPos chunkPos, ChunkRegion chunkRegion, ChunkRandom chunkRandom) {
		if (isCenterInChunk(chunkPos)) {
			StarrySkies.LOGGER.debug("Populating entities for spheroid in chunk x:{} z:{} (StartX:{} StartZ:{}) {}", chunkPos.x, chunkPos.z, chunkPos.getStartX(), chunkPos.getStartZ(), this.getDescription(chunkRegion.getRegistryManager()));
			for (Pair<EntityType<?>, Integer> spawnEntry : spawns) {
				
				int xCord = chunkPos.getStartX();
				int zCord = chunkPos.getStartZ();
				
				chunkRandom.setPopulationSeed(chunkRegion.getSeed(), xCord, zCord);
				
				for (int i = 0; i < spawnEntry.getRight(); i++) {
					int startingX = this.getPosition().getX();
					int startingY = this.getPosition().getY() + this.getRadius() + 1;
					int startingZ = this.getPosition().getZ();
					int minHeight = this.getPosition().getY() - this.getRadius();
					BlockPos.Mutable blockPos = new BlockPos.Mutable(startingX, startingY, startingZ);
					int height = Support.getLowerGroundBlock(chunkRegion, blockPos, minHeight) + 1;
					
					if (height != 0) {
						Entity entity = spawnEntry.getLeft().create(chunkRegion.toServerWorld());
						if (entity != null) {
							float width = entity.getWidth();
							double xPos = MathHelper.clamp(startingX, (double) xCord + (double) width, (double) xCord + 16.0D - (double) width);
							double zLength = MathHelper.clamp(startingZ, (double) zCord + (double) width, (double) zCord + 16.0D - (double) width);
							
							try {
								entity.refreshPositionAndAngles(xPos, height, zLength, chunkRandom.nextFloat() * 360.0F, 0.0F);
								if (entity instanceof MobEntity mobentity) {
									if (mobentity.canSpawn(chunkRegion, SpawnReason.CHUNK_GENERATION) && mobentity.canSpawn(chunkRegion)) {
										mobentity.initialize(chunkRegion, chunkRegion.getLocalDifficulty(mobentity.getBlockPos()), SpawnReason.CHUNK_GENERATION, null);
										boolean success = chunkRegion.spawnEntity(mobentity);
										if (!success) {
											return;
										}
									}
								}
							} catch (Exception exception) {
								StarrySkies.LOGGER.warn("Failed to spawn mob on sphere{}\nException: {}", this.getDescription(chunkRegion.getRegistryManager()), exception);
							}
						}
					}
				}
			}
			StarrySkies.LOGGER.debug("Finished populating");
		}
	}
	
	protected void placeSpawner(@NotNull WorldAccess worldAccess, BlockPos blockPos, EntityType<?> entityType) {
		worldAccess.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 3);
		BlockEntity blockEntity = worldAccess.getBlockEntity(blockPos);
		if (blockEntity instanceof MobSpawnerBlockEntity) {
			((MobSpawnerBlockEntity) blockEntity).getLogic().setEntityId(entityType, null, worldAccess.getRandom(), blockPos);
		}
	}
	
	public Spheroid.Template<?> getTemplate() {
		return this.template;
	}
	
	public static abstract class Template<C> {
		
		protected final int minSize;
		protected final int maxSize;
		protected final Map<ConfiguredSpheroidFeature<?, ?>, Float> decorators;
		protected final List<SpheroidEntitySpawnDefinition> spawns;
		
		public Template(SharedConfig sharedConfig) {
			this.minSize = sharedConfig.minSize;
			this.maxSize = sharedConfig.maxSize;
			this.decorators = sharedConfig.decorators;
			this.spawns = sharedConfig.spawns;
		}
		
		protected static <C, P extends Spheroid.Template<C>> MapCodec<P> createCodec(MapCodec<C> configCodec, BiFunction<SharedConfig, C, P> constructor) {
			return RecordCodecBuilder.mapCodec(instance -> instance.group(
					Spheroid.Template.SharedConfig.CODEC.forGetter(Spheroid.Template::sharedConfig),
					configCodec.fieldOf("type_data").forGetter(Spheroid.Template::config)
			).apply(instance, constructor));
		}
		
		protected static float randomBetween(Random random, int min, int max) {
			return min + random.nextFloat() * (max - min);
		}
		
		public abstract SpheroidTemplateType<? extends Template<C>> getType();
		
		public SharedConfig sharedConfig() {
			return new SharedConfig(minSize, maxSize, decorators, spawns);
		}
		
		public abstract C config();
		
		protected List<ConfiguredSpheroidFeature<?, ?>> selectDecorators(Random random) {
			List<ConfiguredSpheroidFeature<?, ?>> decorators = new ArrayList<>();
			for (Map.Entry<ConfiguredSpheroidFeature<?, ?>, Float> entry : this.decorators.entrySet()) {
				if (random.nextFloat() < entry.getValue()) {
					decorators.add(entry.getKey());
				}
			}
			return decorators;
		}
		
		protected List<Pair<EntityType<?>, Integer>> selectSpawns(Random random) {
			List<Pair<EntityType<?>, Integer>> spawns = new ArrayList<>();
			for (SpheroidEntitySpawnDefinition entry : this.spawns) {
				if (random.nextFloat() < entry.chance) {
					int count = Support.getRandomBetween(random, entry.minCount, entry.maxCount);
					spawns.add(new Pair<>(entry.entityType, count));
				}
			}
			return spawns;
		}
		
		public abstract Spheroid generate(ChunkRandom systemRandom, DynamicRegistryManager registryManager);
		
		public record SharedConfig(int minSize, int maxSize, Map<ConfiguredSpheroidFeature<?, ?>, Float> decorators,
								   List<SpheroidEntitySpawnDefinition> spawns) {
			public static final MapCodec<SharedConfig> CODEC = RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							Codec.INT.fieldOf("min_size").forGetter(SharedConfig::minSize),
							Codec.INT.fieldOf("max_size").forGetter(SharedConfig::maxSize),
							new Support.FailSoftMapCodec<>(ConfiguredSpheroidFeature.CODEC, Codec.FLOAT).fieldOf("decorators").forGetter(SharedConfig::decorators),
							SpheroidEntitySpawnDefinition.CODEC.listOf().fieldOf("spawns").forGetter(SharedConfig::spawns)
					).apply(instance, SharedConfig::new)
			);
		}
		
	}
	
}
