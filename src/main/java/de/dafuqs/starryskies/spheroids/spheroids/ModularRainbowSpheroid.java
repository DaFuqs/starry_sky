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

public class ModularRainbowSpheroid extends Spheroid {
	
	private final List<BlockState> rainbowBlocks;
	private final List<BlockState> topBlocks;
	private final List<BlockState> bottomBlocks;
	
	public ModularRainbowSpheroid(Spheroid.Template template, float radius, List<SpheroidDecorator> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
								  List<BlockState> rainbowBlocks, List<BlockState> topBlocks, List<BlockState> bottomBlocks) {
		
		super(template, radius, decorators, spawns, random);
		this.rainbowBlocks = rainbowBlocks;
		this.topBlocks = topBlocks;
		this.bottomBlocks = bottomBlocks;
	}
	
	public static class Template extends Spheroid.Template {
		
		private final List<BlockState> rainbowBlocks = new ArrayList<>();
		private final List<BlockState> topBlocks = new ArrayList<>();
		private final List<BlockState> bottomBlocks = new ArrayList<>();
		
		public Template(Identifier identifier, JsonObject data) throws CommandSyntaxException {
			super(identifier, data);
			
			JsonObject typeData = JsonHelper.getObject(data, "type_data");
			for (JsonElement e : JsonHelper.getArray(typeData, "blocks")) {
				BlockState state = StarrySkies.getStateFromString(e.getAsString());
				this.rainbowBlocks.add(state);
			}
			for (JsonElement e : JsonHelper.getArray(typeData, "top_blocks")) {
				BlockState state = StarrySkies.getStateFromString(e.getAsString());
				this.topBlocks.add(state);
			}
			for (JsonElement e : JsonHelper.getArray(typeData, "bottom_blocks")) {
				BlockState state = StarrySkies.getStateFromString(e.getAsString());
				this.bottomBlocks.add(state);
			}
		}
		
		@Override
		public ModularRainbowSpheroid generate(ChunkRandom random) {
			return new ModularRainbowSpheroid(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, rainbowBlocks, topBlocks, bottomBlocks);
		}
		
	}
	
	@Override
	public String getDescription() {
		return "+++ ModularRainbowSpheroid +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.template.getID() +
				"\nRadius: " + this.radius +
				"\nRainbow Blocks ( + " + this.rainbowBlocks.size() + "): " + this.rainbowBlocks +
				"\nBottom Blocks ( + " + this.bottomBlocks.size() + "): " + this.rainbowBlocks +
				"\nTop Blocks ( + " + this.topBlocks.size() + "): " + this.rainbowBlocks;
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
					
					int rainbowBlockMod = Math.abs(x2) + Math.abs(y2) + Math.abs(z2);
					if (d > this.radius - 1) {
						if (bottomBlocks != null && isBottomBlock(d, x2, y2, z2)) {
							int currentBlockID = rainbowBlockMod % this.bottomBlocks.size();
							chunk.setBlockState(currBlockPos, this.bottomBlocks.get(currentBlockID), false);
						} else if (topBlocks != null && isTopBlock(d, x2, y2, z2)) {
							int currentBlockID = rainbowBlockMod % this.topBlocks.size();
							chunk.setBlockState(currBlockPos, this.topBlocks.get(currentBlockID), false);
						} else {
							int currentBlockID = rainbowBlockMod % this.rainbowBlocks.size();
							BlockState currentBlockState = this.rainbowBlocks.get(currentBlockID);
							chunk.setBlockState(currBlockPos, currentBlockState, false);
						}
					} else {
						int currentBlockID = rainbowBlockMod % this.rainbowBlocks.size();
						BlockState currentBlockState = this.rainbowBlocks.get(currentBlockID);
						chunk.setBlockState(currBlockPos, currentBlockState, false);
					}
				}
			}
		}
	}
	
}