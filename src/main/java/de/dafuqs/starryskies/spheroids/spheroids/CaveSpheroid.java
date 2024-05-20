package de.dafuqs.starryskies.spheroids.spheroids;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.*;
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

public class CaveSpheroid extends Spheroid {
	
	private final BlockState coreBlock = Blocks.CAVE_AIR.getDefaultState();
	private final BlockState caveFloorBlock;
	private final BlockState topBlock;
	private final BlockState bottomBlock;
	private final BlockState shellBlock;
	private final float shellRadius;
	RegistryKey<LootTable> chestLootTable;
	
	public CaveSpheroid(Spheroid.Template template, float radius, List<SpheroidDecorator> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
						BlockState caveFloorBlock, BlockState shellBlock, float shellRadius, BlockState topBlock, BlockState bottomBlock, RegistryKey<LootTable> chestLootTable) {
		
		super(template, radius, decorators, spawns, random);
		
		this.caveFloorBlock = caveFloorBlock;
		this.shellBlock = shellBlock;
		this.shellRadius = shellRadius;
		this.topBlock = topBlock;
		this.bottomBlock = bottomBlock;
		this.chestLootTable = chestLootTable;
	}
	
	public static class Template extends Spheroid.Template {
		
		private final BlockStateSupplier shellBlock;
		private final int minShellRadius;
		private final int maxShellRadius;
		private BlockState caveFloorBlock = null;
		private BlockState topBlock = null;
		private BlockState bottomBlock = null;
		private RegistryKey<LootTable> lootTable = null;
		private float lootTableChance = 0;
		
		public Template(Identifier identifier, JsonObject data) throws CommandSyntaxException {
			super(identifier, data);
			
			JsonObject typeData = JsonHelper.getObject(data, "type_data");
			this.minShellRadius = JsonHelper.getInt(typeData, "min_shell_size");
			this.maxShellRadius = JsonHelper.getInt(typeData, "max_shell_size");
			this.shellBlock = BlockStateSupplier.of(typeData.get("shell_block"));
			if (JsonHelper.hasString(typeData, "top_block")) {
				this.topBlock = StarrySkies.getStateFromString(JsonHelper.getString(typeData, "top_block"));
			}
			if (JsonHelper.hasString(typeData, "bottom_block")) {
				this.bottomBlock = StarrySkies.getStateFromString(JsonHelper.getString(typeData, "bottom_block"));
			}
			if (JsonHelper.hasString(typeData, "cave_floor_block")) {
				this.caveFloorBlock = StarrySkies.getStateFromString(JsonHelper.getString(typeData, "cave_floor_block"));
			}
			if (JsonHelper.hasJsonObject(typeData, "treasure_chest")) {
				JsonObject treasureChestObject = JsonHelper.getObject(typeData, "treasure_chest");
				this.lootTable = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.tryParse(JsonHelper.getString(treasureChestObject, "loot_table")));
				this.lootTableChance = JsonHelper.getFloat(treasureChestObject, "chance");
			}
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
