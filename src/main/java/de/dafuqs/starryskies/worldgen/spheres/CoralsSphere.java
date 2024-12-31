package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.state_providers.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.loot.*;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.*;
import net.minecraft.util.*;
import net.minecraft.util.dynamic.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.floatprovider.*;
import net.minecraft.util.math.intprovider.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.stateprovider.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class CoralsSphere extends Sphere<CoralsSphere.Config> {
	
	public static final ArrayList<BlockState> LIST_FULL_CORAL_BLOCKS = new ArrayList<>() {{
		add(Blocks.BRAIN_CORAL_BLOCK.getDefaultState());
		add(Blocks.TUBE_CORAL_BLOCK.getDefaultState());
		add(Blocks.BUBBLE_CORAL_BLOCK.getDefaultState());
		add(Blocks.FIRE_CORAL_BLOCK.getDefaultState());
		add(Blocks.HORN_CORAL_BLOCK.getDefaultState());
	}};
	public static final ArrayList<BlockState> LIST_WATERLOGGABLE_CORAL_BLOCKS = new ArrayList<>() {{
		add(Blocks.BRAIN_CORAL.getDefaultState().with(CoralParentBlock.WATERLOGGED, true));
		add(Blocks.TUBE_CORAL.getDefaultState().with(CoralParentBlock.WATERLOGGED, true));
		add(Blocks.BUBBLE_CORAL.getDefaultState().with(CoralParentBlock.WATERLOGGED, true));
		add(Blocks.FIRE_CORAL.getDefaultState().with(CoralParentBlock.WATERLOGGED, true));
		add(Blocks.HORN_CORAL.getDefaultState().with(CoralParentBlock.WATERLOGGED, true));
		
		add(Blocks.BRAIN_CORAL_FAN.getDefaultState().with(CoralParentBlock.WATERLOGGED, true));
		add(Blocks.TUBE_CORAL_FAN.getDefaultState().with(CoralParentBlock.WATERLOGGED, true));
		add(Blocks.BUBBLE_CORAL_FAN.getDefaultState().with(CoralParentBlock.WATERLOGGED, true));
		add(Blocks.FIRE_CORAL_FAN.getDefaultState().with(CoralParentBlock.WATERLOGGED, true));
		add(Blocks.HORN_CORAL_FAN.getDefaultState().with(CoralParentBlock.WATERLOGGED, true));
		
		add(Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.WATERLOGGED, true).with(SeaPickleBlock.PICKLES, 1));
		add(Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.WATERLOGGED, true).with(SeaPickleBlock.PICKLES, 2));
		add(Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.WATERLOGGED, true).with(SeaPickleBlock.PICKLES, 3));
		add(Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.WATERLOGGED, true).with(SeaPickleBlock.PICKLES, 4));
	}};
	protected static BlockState WATER = Blocks.WATER.getDefaultState();
	
	public CoralsSphere(Codec<CoralsSphere.Config> codec) {
		super(codec);
	}
	
	@Override
	public PlacedSphere<?> generate(ConfiguredSphere<? extends Sphere<CoralsSphere.Config>, Config> configuredSphere, Config config, ChunkRandom random, DynamicRegistryManager registryManager, BlockPos pos, float radius) {
		@Nullable RegistryKey<LootTable> lootTable = null;
		if (config.treasureEntry.isPresent() && random.nextLong() < config.treasureEntry.get().chance()) {
			lootTable = config.treasureEntry.get().lootTable();
		}
		
		return new CoralsSphere.Placed(configuredSphere, radius, configuredSphere.getDecorators(random), configuredSphere.getSpawns(random), random, config.shellBlock.getForSphere(random, pos), config.shellThickness.get(random), lootTable);
	}
	
	public static class Config extends SphereConfig {
		
		public static final Codec<CoralsSphere.Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				SphereConfig.CONFIG_CODEC.forGetter((config) -> config),
				SphereStateProvider.CODEC.fieldOf("shell_block").forGetter((config) -> config.shellBlock),
				IntProvider.POSITIVE_CODEC.fieldOf("shell_thickness").forGetter((config) -> config.shellThickness),
				Codecs.POSITIVE_FLOAT.fieldOf("hole_in_bottom_chance").forGetter((config) -> config.holeInBottomChance),
				TreasureChestEntry.CODEC.optionalFieldOf("treasure_chest").forGetter((config) -> config.treasureEntry)
		).apply(instance, (sphereConfig, shellBlock, shellThickness, holeInBottomChance, treasureEntry) -> new Config(sphereConfig.size, sphereConfig.decorators, sphereConfig.spawns, sphereConfig.generation, shellBlock, shellThickness, holeInBottomChance, treasureEntry)));
		
		protected final SphereStateProvider shellBlock;
		protected final IntProvider shellThickness;
		protected final float holeInBottomChance;
		private final Optional<TreasureChestEntry> treasureEntry;
		
		public Config(FloatProvider size, Map<RegistryEntry<ConfiguredSphereDecorator<?, ?>>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation, SphereStateProvider shellBlock, IntProvider shellThickness, float holeInBottomChance, Optional<TreasureChestEntry> treasureEntry) {
			super(size, decorators, spawns, generation);
			this.shellBlock = shellBlock;
			this.shellThickness = shellThickness;
			this.holeInBottomChance = holeInBottomChance;
			this.treasureEntry = treasureEntry;
		}
		
	}
	
	public static class Placed extends PlacedSphere<CoralsSphere.Config> {
		
		private final BlockStateProvider shellBlock;
		private final float shellRadius;
		@Nullable
		private final RegistryKey<LootTable> chestLootTable;
		
		public Placed(ConfiguredSphere<? extends Sphere<CoralsSphere.Config>, CoralsSphere.Config> configuredSphere, float radius, List<RegistryEntry<ConfiguredSphereDecorator<?, ?>>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
					  BlockStateProvider shellBlock, float shellRadius, @Nullable RegistryKey<LootTable> chestLootTable) {
			super(configuredSphere, radius, decorators, spawns, random);
			this.shellBlock = shellBlock;
			this.shellRadius = shellRadius;
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
			
			BlockPos.Mutable currBlockPos = new BlockPos.Mutable();
			for (int x2 = Math.max(chunkX * 16, x - ceiledRadius); x2 <= maxX; x2++) {
				for (int y2 = y - ceiledRadius; y2 <= y + ceiledRadius; y2++) {
					for (int z2 = Math.max(chunkZ * 16, z - ceiledRadius); z2 <= maxZ; z2++) {
						long d = Math.round(Support.getDistance(x, y, z, x2, y2, z2));
						if (d > this.radius) {
							continue;
						}
						currBlockPos.set(x2, y2, z2);
						
						if (d == 0 && hasChest) {
							placeCenterChestWithLootTable(chunk, currBlockPos.toImmutable(), this.chestLootTable, random, true);
						} else if (d <= (this.radius - this.shellRadius - 1)) {
							int rand = random.nextInt(7);
							if (rand < 2) {
								BlockState coral = getRandomCoralBlock(random);
								if (rand == 0 && chunk.getBlockState(currBlockPos.down()).getBlock() == Blocks.WATER) {
									chunk.setBlockState(currBlockPos.down(), coral, false);
									chunk.setBlockState(currBlockPos, getRandomWaterLoggableBlock(random), false);
								} else {
									chunk.setBlockState(currBlockPos, coral, false);
								}
							} else {
								chunk.setBlockState(currBlockPos, WATER, false);
							}
						} else if (d <= (this.radius - this.shellRadius)) {
							chunk.setBlockState(currBlockPos, WATER, false);
						} else {
							chunk.setBlockState(currBlockPos, this.shellBlock.get(random, currBlockPos), false);
						}
					}
				}
			}
		}
		
		public BlockState getRandomCoralBlock(ChunkRandom random) {
			return LIST_FULL_CORAL_BLOCKS.get(random.nextInt(LIST_FULL_CORAL_BLOCKS.size()));
		}
		
		public BlockState getRandomWaterLoggableBlock(ChunkRandom random) {
			return LIST_WATERLOGGABLE_CORAL_BLOCKS.get(random.nextInt(LIST_WATERLOGGABLE_CORAL_BLOCKS.size()));
		}
		
		@Override
		public String getDescription(DynamicRegistryManager registryManager) {
			return "+++ CoralSphere +++" +
					"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
					"\nTemplateID: " + this.getID(registryManager) +
					"\nRadius: " + this.radius +
					"\nShell: " + this.shellBlock.toString() + " (Radius: " + this.shellRadius + ")";
		}
	}
	
}
	
