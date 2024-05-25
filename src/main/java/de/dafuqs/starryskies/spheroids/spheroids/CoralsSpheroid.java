package de.dafuqs.starryskies.spheroids.spheroids;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.loot.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;

import java.util.*;

public class CoralsSpheroid extends Spheroid {
	
	protected BlockState shellBlock;
	protected float shellRadius;
	protected BlockState WATER = Blocks.WATER.getDefaultState();
	protected RegistryKey<LootTable> centerChestLootTable;
	
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
	
	public CoralsSpheroid(Spheroid.Template<?> template, float radius, List<SpheroidDecorator> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
						  BlockState shellBlock, float shellRadius, RegistryKey<LootTable> centerChestLootTable) {
		
		super(template, radius, decorators, spawns, random);
		this.shellBlock = shellBlock;
		this.shellRadius = shellRadius;
		this.centerChestLootTable = centerChestLootTable;
	}
	
	public static class Template extends Spheroid.Template<Template.Config> {

		public record Config(int minShellRadius, int maxShellRadius, BlockStateSupplier validShellBlocks,
							 Chest chest) {
			public record Chest(RegistryKey<LootTable> lootTable, float lootTableChance) {
				public static final Codec<Chest> CODEC = RecordCodecBuilder.create(
						instance -> instance.group(
								RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).fieldOf("loot_table").forGetter(Chest::lootTable),
								Codec.FLOAT.fieldOf("chance").forGetter(Chest::lootTableChance)
						).apply(instance, Chest::new)
				);
			}
			public static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							Codec.INT.fieldOf("min_shell_size").forGetter(Config::minShellRadius),
							Codec.INT.fieldOf("max_shell_size").forGetter(Config::maxShellRadius),
							BlockStateSupplier.CODEC.fieldOf("shell_block").forGetter(Config::validShellBlocks),
							Chest.CODEC.lenientOptionalFieldOf("treasure_chest", null).forGetter(Config::chest)
					).apply(instance, Config::new)
			);
		}

		public static final MapCodec<Template> CODEC = createCodec(Config.CODEC, Template::new);
		
		private final BlockStateSupplier validShellBlocks;
		private final int minShellRadius;
		private final int maxShellRadius;
		private final RegistryKey<LootTable> lootTable;
		private final float lootTableChance;

		public Template(SharedConfig shared, Config config) {
			super(shared);
			this.validShellBlocks = config.validShellBlocks;
			this.minShellRadius = config.minShellRadius;
			this.maxShellRadius = config.maxShellRadius;
			var chest = config.chest;
			if (chest != null) {
				this.lootTable = chest.lootTable;
				this.lootTableChance = chest.lootTableChance;
			} else {
				this.lootTable = null;
				this.lootTableChance = 0;
			}
		}

		@Override
		public SpheroidTemplateType<Template> getType() {
			return SpheroidTemplateType.CORALS;
		}

		@Override
		public Config config() {
			return new Config(minShellRadius, maxShellRadius, validShellBlocks,
					new Config.Chest(lootTable, lootTableChance));
		}

		@Override
		public CoralsSpheroid generate(ChunkRandom random) {
			int shellRadius = Support.getRandomBetween(random, this.minShellRadius, this.maxShellRadius);
			BlockState shellBlockState = validShellBlocks.get(random);
			RegistryKey<LootTable> lootTable = random.nextFloat() < lootTableChance ? this.lootTable : null;
			return new CoralsSpheroid(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, shellBlockState, shellRadius, lootTable);
		}
		
	}
	
	public String getDescription() {
		return "+++ CoralSpheroid +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.template.getID() +
				"\nRadius: " + this.radius +
				"\nShell: " + this.shellBlock.toString() + " (Radius: " + this.shellRadius + ")";
	}
	
	@Override
	public void generate(Chunk chunk) {
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;
		
		int x = this.getPosition().getX();
		int y = this.getPosition().getY();
		int z = this.getPosition().getZ();
		
		boolean hasChest = this.centerChestLootTable != null;
		
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
					
					if (d == 0 && hasChest) {
						placeCenterChestWithLootTable(chunk, currBlockPos, this.centerChestLootTable, random, true);
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
						chunk.setBlockState(currBlockPos, this.shellBlock, false);
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
	
}
