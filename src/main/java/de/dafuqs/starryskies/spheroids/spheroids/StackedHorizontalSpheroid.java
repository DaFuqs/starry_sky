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

public class StackedHorizontalSpheroid extends Spheroid {
	
	private final List<BlockState> stripesBlockStates;
	
	public StackedHorizontalSpheroid(Spheroid.Template template, float radius, List<SpheroidDecorator> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
									 List<BlockState> stripesBlockStates) {
		
		super(template, radius, decorators, spawns, random);
		this.stripesBlockStates = stripesBlockStates;
	}
	
	public static class Template extends Spheroid.Template {
		
		private final List<BlockState> stripesBlockStates = new ArrayList<>();
		
		public Template(Identifier identifier, JsonObject data) throws CommandSyntaxException {
			super(identifier, data);
			
			JsonObject typeData = JsonHelper.getObject(data, "type_data");
			for (JsonElement e : JsonHelper.getArray(typeData, "blocks")) {
				BlockState state = StarrySkies.getStateFromString(e.getAsString());
				stripesBlockStates.add(state);
			}
		}
		
		@Override
		public StackedHorizontalSpheroid generate(ChunkRandom random) {
			return new StackedHorizontalSpheroid(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, stripesBlockStates);
		}
		
	}
	
	@Override
	public String getDescription() {
		return "+++ StripesSpheroid +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.template.getID() +
				"\nRadius: " + this.radius +
				"\nStripes Blocks ( + " + stripesBlockStates.size() + "): " + this.stripesBlockStates;
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
		for (int y2 = y - ceiledRadius; y2 <= y + ceiledRadius; y2++) {
			
			float currentSpheroidHeight = y - y2 + ceiledRadius;
			int currentBlockStateIndex = (int) ((currentSpheroidHeight * stripesBlockStates.size() - 1) / (ceiledRadius * 2));
			
			BlockState currentBlockState = this.stripesBlockStates.get(currentBlockStateIndex);
			
			BlockPos.Mutable currBlockPos = new BlockPos.Mutable();
			for (int x2 = Math.max(chunkX * 16, x - ceiledRadius); x2 <= Math.min(chunkX * 16 + 15, x + ceiledRadius); x2++) {
				for (int z2 = Math.max(chunkZ * 16, z - ceiledRadius); z2 <= Math.min(chunkZ * 16 + 15, z + ceiledRadius); z2++) {
					long d = Math.round(Support.getDistance(x, y, z, x2, y2, z2));
					if (d > this.radius) {
						continue;
					}
					currBlockPos.set(x2, y2, z2);
					
					chunk.setBlockState(currBlockPos, currentBlockState, false);
				}
			}
		}
	}
	
}