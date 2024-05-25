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

import static de.dafuqs.starryskies.Support.BLOCKSTATE_STRING_CODEC;

public class CaveSpheroid extends Spheroid {
	
	private final BlockState coreBlock = Blocks.CAVE_AIR.getDefaultState();
	private final BlockState caveFloorBlock;
	private final BlockState topBlock;
	private final BlockState bottomBlock;
	private final BlockState shellBlock;
	private final float shellRadius;
	RegistryKey<LootTable> chestLootTable;
	
	public CaveSpheroid(Spheroid.Template<?> template, float radius, List<SpheroidDecorator> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
						BlockState caveFloorBlock, BlockState shellBlock, float shellRadius, BlockState topBlock, BlockState bottomBlock, RegistryKey<LootTable> chestLootTable) {
		
		super(template, radius, decorators, spawns, random);
		
		this.caveFloorBlock = caveFloorBlock;
		this.shellBlock = shellBlock;
		this.shellRadius = shellRadius;
		this.topBlock = topBlock;
		this.bottomBlock = bottomBlock;
		this.chestLootTable = chestLootTable;
	}
	
	public static class Template extends Spheroid.Template<Template.Config> {

		public record Config(BlockStateSupplier shellBlock, int minShellRadius, int maxShellRadius,
							 Optional<BlockState> caveFloorBlock, Optional<BlockState> topBlock, Optional<BlockState> bottomBlock,
							 Optional<Chest> chest) {
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
							BlockStateSupplier.CODEC.fieldOf("shell_block").forGetter(Config::shellBlock),
							Codec.INT.fieldOf("min_shell_size").forGetter(Config::minShellRadius),
							Codec.INT.fieldOf("max_shell_size").forGetter(Config::maxShellRadius),
							BLOCKSTATE_STRING_CODEC.lenientOptionalFieldOf("cave_floor_block").forGetter(Config::caveFloorBlock),
							BLOCKSTATE_STRING_CODEC.lenientOptionalFieldOf("top_block").forGetter(Config::topBlock),
							BLOCKSTATE_STRING_CODEC.lenientOptionalFieldOf("bottom_block").forGetter(Config::bottomBlock),
							Chest.CODEC.lenientOptionalFieldOf("treasure_chest").forGetter(Config::chest)
					).apply(instance, Config::new)
			);
		}

		public static final MapCodec<Template> CODEC = createCodec(Config.CODEC, Template::new);
		
		private final BlockStateSupplier shellBlock;
		private final int minShellRadius;
		private final int maxShellRadius;
		private final BlockState caveFloorBlock;
		private final BlockState topBlock;
		private final BlockState bottomBlock;
		private final RegistryKey<LootTable> lootTable;
		private final float lootTableChance;

		public Template(SharedConfig shared, Config config) {
			super(shared);
			this.shellBlock = config.shellBlock;
			this.minShellRadius = config.minShellRadius;
			this.maxShellRadius = config.maxShellRadius;
			this.caveFloorBlock = config.caveFloorBlock.orElse(null);
			this.topBlock = config.topBlock.orElse(null);
			this.bottomBlock = config.bottomBlock.orElse(null);
			var chest = config.chest.orElse(null);
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
			return SpheroidTemplateType.CAVE;
		}

		@Override
		public Config config() {
			return new Config(shellBlock, minShellRadius, maxShellRadius, Optional.ofNullable(caveFloorBlock),
					Optional.ofNullable(topBlock), Optional.ofNullable(bottomBlock),
					Optional.of(new Config.Chest(lootTable, lootTableChance)));
		}

		@Override
		public CaveSpheroid generate(ChunkRandom random) {
			int shellRadius = Support.getRandomBetween(random, this.minShellRadius, this.maxShellRadius);
			
			RegistryKey<LootTable> lootTable = null;
			if (random.nextFloat() < lootTableChance) {
				lootTable = this.lootTable;
			}
			return new CaveSpheroid(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, caveFloorBlock, shellBlock.get(random), shellRadius, topBlock, bottomBlock, lootTable);
		}
		
	}
	
	@Override
	public String getDescription() {
		String s = "+++ CaveSpheroid +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.template.getID() +
				"\nRadius: " + this.radius +
				"\nShellBlock: " + this.shellBlock +
				"\nShellRadius: " + this.shellRadius +
				"\nCaveFloorBlock: " + this.caveFloorBlock;
		
		if (this.topBlock != null) {
			s += "\nTopBlock: " + this.topBlock;
		}
		if (this.bottomBlock != null) {
			s += "\nBottomBlock: " + this.bottomBlock;
		}
		return s;
	}
	
	@Override
	public void generate(Chunk chunk) {
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;
		
		int x = this.getPosition().getX();
		int y = this.getPosition().getY();
		int z = this.getPosition().getZ();
		
		boolean hasChest = this.chestLootTable != null;
		
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
					
					if (d > this.radius - 1) {
						if (bottomBlock != null && isBottomBlock(d, x2, y2, z2)) {
							chunk.setBlockState(currBlockPos, this.bottomBlock, false);
						} else if (topBlock != null && isTopBlock(d, x2, y2, z2)) {
							chunk.setBlockState(currBlockPos, this.topBlock, false);
						} else {
							chunk.setBlockState(currBlockPos, this.shellBlock, false);
						}
					} else if (isAboveCaveFloorBlock(d, x2, y2, z2, shellRadius)) {
						if (this.caveFloorBlock == null) {
							chunk.setBlockState(currBlockPos.down(), this.shellBlock, false);
						} else {
							chunk.setBlockState(currBlockPos.down(), this.caveFloorBlock, false);
						}
						if (hasChest && x2 - x == 0 && z2 - z == 0) {
							placeCenterChestWithLootTable(chunk, currBlockPos, chestLootTable, random, false);
						}
					} else if (d <= this.radius - this.shellRadius) {
						chunk.setBlockState(currBlockPos, this.coreBlock, false); // always CAVE_AIR
					} else if (d < this.radius) {
						chunk.setBlockState(currBlockPos, this.shellBlock, false);
					}
				}
			}
		}
	}
	
}
