package de.dafuqs.starryskies.spheroids.spheroids;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;

import java.util.*;

public class GeodeSpheroid extends Spheroid {
	
	private final BlockState innerBlockState;
	private final BlockState innerSpecklesBlockState;
	private final float speckleChance;
	private final BlockState middleBlockSate;
	private final BlockState outerBlockState;
	
	public GeodeSpheroid(Spheroid.Template template, float radius, List<SpheroidDecorator> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
						 BlockState innerBlockState, BlockState innerSpecklesBlockState, float speckleChance, BlockState middleBlockSate, BlockState outerBlockState) {
		
		super(template, radius, decorators, spawns, random);
		
		this.innerBlockState = innerBlockState;
		this.innerSpecklesBlockState = innerSpecklesBlockState;
		this.speckleChance = speckleChance;
		this.middleBlockSate = middleBlockSate;
		this.outerBlockState = outerBlockState;
	}
	
	public static class Template extends Spheroid.Template {
		
		private final BlockState innerBlockState;
		private final BlockState innerSpecklesBlockState;
		private final float speckleChance;
		private final BlockState middleBlockSate;
		private final BlockState outerBlockState;
		
		public Template(Identifier identifier, JsonObject data) throws CommandSyntaxException {
			super(identifier, data);
			
			JsonObject typeData = JsonHelper.getObject(data, "type_data");
			this.innerBlockState = StarrySkies.getStateFromString(JsonHelper.getString(typeData, "inner_block"));
			this.innerSpecklesBlockState = StarrySkies.getStateFromString(JsonHelper.getString(typeData, "inner_speckles_block"));
			this.speckleChance = JsonHelper.getFloat(typeData, "inner_speckles_block_chance");
			this.middleBlockSate = StarrySkies.getStateFromString(JsonHelper.getString(typeData, "middle_block"));
			this.outerBlockState = StarrySkies.getStateFromString(JsonHelper.getString(typeData, "outer_block"));
		}
		
		@Override
		public GeodeSpheroid generate(ChunkRandom random) {
			return new GeodeSpheroid(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, innerBlockState, innerSpecklesBlockState, speckleChance, middleBlockSate, outerBlockState);
		}
		
	}
	
	@Override
	public String getDescription() {
		return "+++ GeodeSpheroid +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.template.getID() +
				"\nRadius: " + this.radius +
				"\nInnerBlock: " + this.innerBlockState +
				"\nInnerSpecklesBlock: " + this.innerSpecklesBlockState +
				"\nSpeckleChance: " + this.speckleChance +
				"\nMiddleBlock: " + this.middleBlockSate +
				"\nOuterBlock: " + this.outerBlockState;
	}
	
	@Override
	public void generate(Chunk chunk) {
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;
		
		int x = this.getPosition().getX();
		int y = this.getPosition().getY();
		int z = this.getPosition().getZ();
		
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
					
					if (d < this.radius - 4) {
						// nothing
					} else if (d < this.radius - 3) {
						if (random.nextFloat() < speckleChance) {
							chunk.setBlockState(currBlockPos, innerSpecklesBlockState, false);
						} else {
							chunk.setBlockState(currBlockPos, innerBlockState, false);
						}
					} else if (d < this.radius - 2) {
						chunk.setBlockState(currBlockPos, middleBlockSate, false);
					} else if (d < this.radius - 1) {
						chunk.setBlockState(currBlockPos, outerBlockState, false);
					}
				}
			}
		}
	}
	
}
