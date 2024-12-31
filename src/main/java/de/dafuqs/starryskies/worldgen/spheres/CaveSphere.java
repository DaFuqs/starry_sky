package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.state_providers.*;
import de.dafuqs.starryskies.worldgen.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.loot.*;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.floatprovider.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.stateprovider.*;
import org.jetbrains.annotations.*;

import java.awt.*;
import java.util.List;
import java.util.*;

public class CaveSphere extends Sphere<CaveSphere.Config> {
	
	public CaveSphere(Codec<CaveSphere.Config> codec) {
		super(codec);
	}
	
	@Override
	public PlacedSphere<?> generate(ConfiguredSphere<? extends Sphere<CaveSphere.Config>, Config> configuredSphere, Config config, ChunkRandom random, DynamicRegistryManager registryManager, BlockPos pos, float radius) {
		@Nullable RegistryKey<LootTable> lootTable = null;
		if (config.treasureEntry.isPresent() && random.nextLong() < config.treasureEntry.get().chance()) {
			lootTable = config.treasureEntry.get().lootTable();
		}
		
		BlockStateProvider shellProvider = config.shellBlock.getForSphere(random, pos);
		
		return new CaveSphere.Placed(configuredSphere, radius, configuredSphere.getDecorators(random), configuredSphere.getSpawns(random), random,
				shellProvider,
				config.topBlock.isPresent() ? config.topBlock.get().getForSphere(random, pos) : shellProvider,
				config.bottomBlock.isPresent() ? config.bottomBlock.get().getForSphere(random, pos) : shellProvider,
				config.caveFloorBlock.isPresent() ? config.caveFloorBlock.get().getForSphere(random, pos) : shellProvider,
				config.shellThickness.get(random), lootTable);
	}
	
	public static class Config extends SphereConfig {
		
		public static final Codec<CaveSphere.Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				SphereConfig.CONFIG_CODEC.forGetter((config) -> config),
				SphereStateProvider.CODEC.fieldOf("shell_block").forGetter((config) -> config.shellBlock),
				SphereStateProvider.CODEC.optionalFieldOf("top_block").forGetter((config) -> config.topBlock),
				SphereStateProvider.CODEC.optionalFieldOf("bottom_block").forGetter((config) -> config.bottomBlock),
				SphereStateProvider.CODEC.optionalFieldOf("cave_floor_block").forGetter((config) -> config.bottomBlock),
				FloatProvider.createValidatedCodec(1.0F, 32.0F).fieldOf("shell_thickness").forGetter((config) -> config.shellThickness),
				TreasureChestEntry.CODEC.optionalFieldOf("treasure_chest").forGetter((config) -> config.treasureEntry)
		).apply(instance, (sphereConfig, shellBlock, topBlock, bottomBlock, caveFloorBlock, shellRadius, treasureEntry) -> new Config(sphereConfig.size, sphereConfig.decorators, sphereConfig.spawns, sphereConfig.generation, shellBlock, topBlock, bottomBlock, caveFloorBlock, shellRadius, treasureEntry)));
		
		private final SphereStateProvider shellBlock;
		private final Optional<SphereStateProvider> topBlock;
		private final Optional<SphereStateProvider> bottomBlock;
		private final Optional<SphereStateProvider> caveFloorBlock;
		private final FloatProvider shellThickness;
		private final Optional<TreasureChestEntry> treasureEntry;
		
		public Config(FloatProvider size, Map<RegistryEntry<ConfiguredSphereDecorator<?, ?>>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation, SphereStateProvider shellBlock,
					  Optional<SphereStateProvider> topBlock, Optional<SphereStateProvider> bottomBlock, Optional<SphereStateProvider> caveFloorBlock, FloatProvider shellThickness, Optional<TreasureChestEntry> treasureEntry) {
			super(size, decorators, spawns, generation);
			
			this.shellBlock = shellBlock;
			this.topBlock = topBlock;
			this.bottomBlock = bottomBlock;
			this.caveFloorBlock = caveFloorBlock;
			this.shellThickness = shellThickness;
			this.treasureEntry = treasureEntry;
		}
		
	}
	
	public static class Placed extends PlacedSphere<CaveSphere.Config> {
		
		private static final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();
		
		private final BlockStateProvider shellBlock;
		private final BlockStateProvider topBlock;
		private final BlockStateProvider bottomBlock;
		private final BlockStateProvider caveFloorBlock;
		private final float shellThickness;
		private final @Nullable RegistryKey<LootTable> chestLootTable;
		
		public Placed(ConfiguredSphere<? extends Sphere<Config>, Config> configuredSphere, float radius, List<RegistryEntry<ConfiguredSphereDecorator<?, ?>>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
					  BlockStateProvider shellBlock, BlockStateProvider topBlock, BlockStateProvider bottomBlock, BlockStateProvider caveFloorBlock, float shellRadius, @Nullable RegistryKey<LootTable> chestLootTable) {
			super(configuredSphere, radius, decorators, spawns, random);
			this.shellBlock = shellBlock;
			this.topBlock = topBlock;
			this.bottomBlock = bottomBlock;
			this.caveFloorBlock = caveFloorBlock;
			this.shellThickness = shellRadius;
			this.chestLootTable = chestLootTable;
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
			
			boolean hasChest = this.chestLootTable != null;
			Map<Point, Integer> floorBlocks = new Object2ObjectArrayMap<>();
			
			BlockPos.Mutable currBlockPos = new BlockPos.Mutable();
			for (int x2 = Math.max(chunkX * 16, x - ceiledRadius); x2 <= maxX; x2++) {
				for (int y2 = y - ceiledRadius; y2 <= y + ceiledRadius; y2++) {
					for (int z2 = Math.max(chunkZ * 16, z - ceiledRadius); z2 <= maxZ; z2++) {
						long d = Math.round(Support.getDistance(x, y, z, x2, y2, z2));
						if (d > this.radius) {
							continue;
						}
						currBlockPos.set(x2, y2, z2);
						
						if (d > this.radius - 1) {
							if (isBottomBlock(d, x2, y2, z2)) {
								chunk.setBlockState(currBlockPos, this.bottomBlock.get(random, currBlockPos), false);
							} else if (isTopBlock(d, x2, y2, z2)) {
								chunk.setBlockState(currBlockPos, this.topBlock.get(random, currBlockPos), false);
							} else {
								chunk.setBlockState(currBlockPos, this.shellBlock.get(random, currBlockPos), false);
							}
						} else if (d <= this.radius - this.shellThickness) {
							Point point = new Point(x2, z2);
							if (!floorBlocks.containsKey(point)) {
								floorBlocks.put(new Point(x2, z2), y2);
								chunk.setBlockState(currBlockPos.down(), this.caveFloorBlock.get(random, currBlockPos), false);
								if (hasChest && x2 - x == 0 && z2 - z == 0) {
									placeCenterChestWithLootTable(chunk, currBlockPos.toImmutable(), chestLootTable, random, false);
								}
								
							}
						} else if (d < this.radius) {
							chunk.setBlockState(currBlockPos, this.shellBlock.get(random, currBlockPos), false);
						}
					}
				}
			}
		}
		
		@Override
		public String getDescription(DynamicRegistryManager registryManager) {
			return "+++ CaveSphere +++" +
					"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
					"\nTemplateID: " + this.getID(registryManager) +
					"\nRadius: " + this.radius +
					"\nShellBlock: " + this.shellBlock +
					"\nShellThickness: " + this.shellThickness +
					"\nCaveFloorBlock: " + this.caveFloorBlock +
					"\nTopBlock: " + this.topBlock +
					"\nBottomBlock: " + this.bottomBlock;
		}
	}
	
}
	
