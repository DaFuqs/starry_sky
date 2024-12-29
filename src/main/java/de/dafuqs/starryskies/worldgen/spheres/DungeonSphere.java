package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.floatprovider.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.stateprovider.*;

import java.util.*;

public class DungeonSphere extends Sphere<DungeonSphere.Config> {
	
	public DungeonSphere(Codec<DungeonSphere.Config> codec) {
		super(codec);
	}
	
	@Override
	public PlacedSphere<?> generate(ConfiguredSphere<? extends Sphere<DungeonSphere.Config>, Config> configuredSphere, Config config, ChunkRandom random, DynamicRegistryManager registryManager) {
		return new DungeonSphere.Placed(configuredSphere, configuredSphere.getSize(random), configuredSphere.getDecorators(random), configuredSphere.getSpawns(random), random,
				config.shellBlock, config.topBlock.orElseGet(() -> config.shellBlock), config.bottomBlock.orElseGet(() -> config.shellBlock), config.caveFloorBlock.orElseGet(() -> config.shellBlock), config.shellThickness.get(random), config.treasureEntry, Registries.ENTITY_TYPE.get(config.entityType));
	}
	
	public static class Config extends SphereConfig {
		
		public static final Codec<DungeonSphere.Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				SphereConfig.CONFIG_CODEC.forGetter((config) -> config),
				BlockStateProvider.TYPE_CODEC.fieldOf("shell_block").forGetter((config) -> config.shellBlock),
				BlockStateProvider.TYPE_CODEC.optionalFieldOf("top_block").forGetter((config) -> config.topBlock),
				BlockStateProvider.TYPE_CODEC.optionalFieldOf("bottom_block").forGetter((config) -> config.bottomBlock),
				BlockStateProvider.TYPE_CODEC.optionalFieldOf("cave_floor_block").forGetter((config) -> config.bottomBlock),
				FloatProvider.createValidatedCodec(1.0F, 32.0F).fieldOf("shell_thickness").forGetter((config) -> config.shellThickness),
				TreasureChestEntry.CODEC.fieldOf("treasure_chest").forGetter((config) -> config.treasureEntry),
				RegistryKey.createCodec(RegistryKeys.ENTITY_TYPE).fieldOf("entity_type").forGetter((config) -> config.entityType)
		).apply(instance, (sphereConfig, shellBlock, topBlock, bottomBlock, caveFloorBlock, shellRadius, treasureEntry, entityType) -> new DungeonSphere.Config(sphereConfig.size, sphereConfig.decorators, sphereConfig.spawns, sphereConfig.generation, shellBlock, topBlock, bottomBlock, caveFloorBlock, shellRadius, treasureEntry, entityType)));
		
		private final BlockStateProvider shellBlock;
		private final Optional<BlockStateProvider> topBlock;
		private final Optional<BlockStateProvider> bottomBlock;
		private final Optional<BlockStateProvider> caveFloorBlock;
		private final FloatProvider shellThickness;
		private final TreasureChestEntry treasureEntry;
		private final RegistryKey<EntityType<?>> entityType;
		
		public Config(FloatProvider size, Map<ConfiguredSphereDecorator<?, ?>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation, BlockStateProvider shellBlock,
					  Optional<BlockStateProvider> topBlock, Optional<BlockStateProvider> bottomBlock, Optional<BlockStateProvider> caveFloorBlock, FloatProvider shellThickness, TreasureChestEntry treasureEntry, RegistryKey<EntityType<?>> entityType) {
			super(size, decorators, spawns, generation);
			
			this.shellBlock = shellBlock;
			this.topBlock = topBlock;
			this.bottomBlock = bottomBlock;
			this.caveFloorBlock = caveFloorBlock;
			this.shellThickness = shellThickness;
			this.treasureEntry = treasureEntry;
			this.entityType = entityType;
		}
		
	}
	
	public static class Placed extends PlacedSphere<DungeonSphere.Config> {
		
		private static final BlockState CHEST_STATE = Blocks.CHEST.getDefaultState();
		private static final BlockState CORE_STATE = Blocks.CAVE_AIR.getDefaultState();
		
		private final BlockStateProvider shellBlock;
		private final BlockStateProvider topBlock;
		private final BlockStateProvider bottomBlock;
		private final BlockStateProvider caveFloorBlock;
		private final float shellThickness;
		private final EntityType<?> entityType;
		private final TreasureChestEntry treasureEntry;
		
		public Placed(ConfiguredSphere<? extends Sphere<Config>, Config> configuredSphere, float radius, List<ConfiguredSphereDecorator<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
					  BlockStateProvider shellBlock, BlockStateProvider topBlock, BlockStateProvider bottomBlock, BlockStateProvider caveFloorBlock, float shellRadius, TreasureChestEntry treasureEntry, EntityType<?> entityType) {
			super(configuredSphere, radius, decorators, spawns, random);
			this.shellBlock = shellBlock;
			this.topBlock = topBlock;
			this.bottomBlock = bottomBlock;
			this.caveFloorBlock = caveFloorBlock;
			this.shellThickness = shellRadius;
			this.treasureEntry = treasureEntry;
			this.entityType = entityType;
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
						
						if (d == 0) {
							chunk.setBlockState(currBlockPos, Blocks.SPAWNER.getDefaultState(), false);
							chunk.setBlockEntity(new MobSpawnerBlockEntity(currBlockPos, Blocks.SPAWNER.getDefaultState()));
							if (chunk.getBlockEntity(currBlockPos) instanceof MobSpawnerBlockEntity mobSpawnerBlockEntity) {
								mobSpawnerBlockEntity.getLogic().setEntityId(this.entityType, null, random, currBlockPos);
							}
						} else if (isAboveCaveFloorBlock(d, x2, y2, z2, shellThickness)) {
							if (random.nextFloat() < treasureEntry.chance()) {
								chunk.setBlockState(currBlockPos, CHEST_STATE, false);
								chunk.setBlockEntity(new ChestBlockEntity(currBlockPos, CHEST_STATE));
								if (chunk.getBlockEntity(currBlockPos) instanceof ChestBlockEntity chestBlockEntity) {
									chestBlockEntity.setLootTable(treasureEntry.lootTable(), random.nextLong());
								}
							}
							chunk.setBlockState(currBlockPos.down(), this.caveFloorBlock.get(random, currBlockPos), false);
						} else if (d > this.radius - 1) {
							if (isBottomBlock(d, x2, y2, z2)) {
								chunk.setBlockState(currBlockPos, this.bottomBlock.get(random, currBlockPos), false);
							} else if (isTopBlock(d, x2, y2, z2)) {
								chunk.setBlockState(currBlockPos, this.topBlock.get(random, currBlockPos), false);
							} else {
								chunk.setBlockState(currBlockPos, this.shellBlock.get(random, currBlockPos), false);
							}
						} else if (d <= this.radius - this.shellThickness) {
							chunk.setBlockState(currBlockPos, CORE_STATE, false);
						} else if (d < this.radius) {
							chunk.setBlockState(currBlockPos, this.shellBlock.get(random, currBlockPos), false);
						}
					}
				}
			}
		}
		
		@Override
		public String getDescription(DynamicRegistryManager registryManager) {
			return "+++ DungeonSphere +++" +
					"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
					"\nTemplateID: " + this.getID(registryManager) +
					"\nRadius: " + this.radius +
					"\nShellBlock: " + this.shellBlock +
					"\nShellRadius: " + this.shellThickness +
					"\nEntityType: " + this.entityType.getName();
		}
	}
	
}
	
