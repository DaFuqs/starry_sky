package de.dafuqs.starryskies.spheroids.spheroids;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.*;
import net.minecraft.loot.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;

import java.util.*;

public class DungeonSpheroid extends Spheroid {
	
	private final EntityType<?> entityType;
	private final BlockState shellBlock;
	private final float shellRadius;
	
	public DungeonSpheroid(Spheroid.Template template, float radius, List<SpheroidDecorator> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
						   EntityType<?> entityType, BlockState shellBlock, float shellRadius) {
		
		super(template, radius, decorators, spawns, random);
		
		this.entityType = entityType;
		this.shellBlock = shellBlock;
		this.shellRadius = shellRadius;
	}
	
	public static class Template extends Spheroid.Template {
		
		private final EntityType<?> entityType;
		private final BlockStateSupplier shellBlock;
		private final int minShellRadius;
		private final int maxShellRadius;
		
		public Template(Identifier identifier, JsonObject data) throws CommandSyntaxException {
			super(identifier, data);
			
			JsonObject typeData = JsonHelper.getObject(data, "type_data");
			this.entityType = Registries.ENTITY_TYPE.get(Identifier.tryParse(JsonHelper.getString(typeData, "entity_type")));
			this.minShellRadius = JsonHelper.getInt(typeData, "min_shell_size");
			this.maxShellRadius = JsonHelper.getInt(typeData, "max_shell_size");
			this.shellBlock = BlockStateSupplier.of(typeData.get("shell_block"));
		}
		
		@Override
		public DungeonSpheroid generate(ChunkRandom random) {
			int shellRadius = Support.getRandomBetween(random, this.minShellRadius, this.maxShellRadius);
			return new DungeonSpheroid(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, entityType, shellBlock.get(random), shellRadius);
		}
		
	}
	
	@Override
	public String getDescription() {
		return "+++ DungeonSpheroid +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.template.getID() +
				"\nRadius: " + this.radius +
				"\nShellBlock: " + this.shellBlock +
				"\nShellRadius: " + this.shellRadius +
				"\nEntityType: " + this.entityType.getName();
	}
	
	@Override
	public void generate(Chunk chunk) {
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;
		
		int x = this.getPosition().getX();
		int y = this.getPosition().getY();
		int z = this.getPosition().getZ();
		
		BlockState chestBlockState = Blocks.CHEST.getDefaultState();
		
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
					
					if (d == 0) {
						chunk.setBlockState(currBlockPos, Blocks.SPAWNER.getDefaultState(), false);
						chunk.setBlockEntity(new MobSpawnerBlockEntity(currBlockPos, Blocks.SPAWNER.getDefaultState()));
						BlockEntity blockEntity_1 = chunk.getBlockEntity(currBlockPos);
						if (blockEntity_1 instanceof MobSpawnerBlockEntity mobSpawnerBlockEntity) {
							mobSpawnerBlockEntity.getLogic().setEntityId(this.entityType, null, random, currBlockPos);
						}
					} else if (d == (this.radius - this.shellRadius - 1) &&
							Math.round(Support.getDistance(x, y, z, x2, y2 - 1, z2)) == (this.radius - this.shellRadius) && random.nextInt((int) radius * 8) == 0) {
						
						chunk.setBlockState(currBlockPos, chestBlockState, false);
						chunk.setBlockEntity(new ChestBlockEntity(currBlockPos, chestBlockState));
						BlockEntity chestBlockEntity = chunk.getBlockEntity(currBlockPos);
						if (chestBlockEntity instanceof ChestBlockEntity) {
							((ChestBlockEntity) chestBlockEntity).setLootTable(LootTables.SIMPLE_DUNGEON_CHEST, random.nextLong());
						}
					} else if (d <= (this.radius - this.shellRadius)) {
						chunk.setBlockState(currBlockPos, Blocks.CAVE_AIR.getDefaultState(), false);
					} else {
						chunk.setBlockState(currBlockPos, this.shellBlock, false);
					}
				}
			}
		}
	}
	
}
