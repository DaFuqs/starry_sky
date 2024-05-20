package de.dafuqs.starryskies.spheroids.spheroids;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;

import java.util.*;

public class ShellCoreSpheroid extends Spheroid {
	
	private final BlockState coreBlock;
	private final BlockState mainBlock;
	private final BlockState shellBlock;
	private final float coreRadius;
	private final float shellRadius;
	
	public ShellCoreSpheroid(Spheroid.Template template, float radius, List<SpheroidDecorator> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
							 BlockState coreBlock, BlockState mainBlock, BlockState shellBlock, float coreRadius, float shellRadius) {
		
		super(template, radius, decorators, spawns, random);
		this.coreBlock = coreBlock;
		this.mainBlock = mainBlock;
		this.shellBlock = shellBlock;
		this.shellRadius = shellRadius;
		
		if (radius <= shellRadius + coreRadius) { //inner core radius <= 0
			this.coreRadius = Math.max(1, radius - shellRadius - 2); //Reduce inner core up to a min of 1
		} else {
			this.coreRadius = coreRadius;
		}
	}
	
	public static class Template extends Spheroid.Template {
		
		private final BlockState mainBlock;
		private final BlockState coreBlock;
		private final BlockStateSupplier shellBlock;
		private final int minCoreRadius;
		private final int maxCoreRadius;
		private final int minShellRadius;
		private final int maxShellRadius;
		
		public Template(Identifier identifier, JsonObject data) throws CommandSyntaxException {
			super(identifier, data);
			
			JsonObject typeData = JsonHelper.getObject(data, "type_data");
			this.minShellRadius = JsonHelper.getInt(typeData, "min_shell_size");
			this.maxShellRadius = JsonHelper.getInt(typeData, "max_shell_size");
			this.minCoreRadius = JsonHelper.getInt(typeData, "min_core_size");
			this.maxCoreRadius = JsonHelper.getInt(typeData, "max_core_size");
			this.mainBlock = StarrySkies.getStateFromString(JsonHelper.getString(typeData, "main_block"));
			this.coreBlock = StarrySkies.getStateFromString(JsonHelper.getString(typeData, "core_block"));
			this.shellBlock = BlockStateSupplier.of(typeData.get("shell_block"));
		}
		
		@Override
		public ShellCoreSpheroid generate(ChunkRandom random) {
			return new ShellCoreSpheroid(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, coreBlock, mainBlock, shellBlock.get(random), randomBetween(random, minCoreRadius, maxCoreRadius), randomBetween(random, minShellRadius, maxShellRadius));
		}
		
	}
	
	@Override
	public String getDescription() {
		return "+++ DoubleCoreSpheroid +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.template.getID() +
				"\nRadius: " + this.radius +
				"\nMain Block: " + this.mainBlock.toString() +
				"\nShell BLock: " + this.shellBlock.toString() + " (Radius: " + this.shellRadius + ")" +
				"\nCore Block: " + this.coreBlock.toString() + " (Radius: " + this.coreRadius + ")";
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
					
					if (d <= this.coreRadius) {
						chunk.setBlockState(currBlockPos, this.coreBlock, false);
					} else if (d <= this.radius - this.shellRadius) {
						chunk.setBlockState(currBlockPos, this.mainBlock, false);
					} else {
						chunk.setBlockState(currBlockPos, this.shellBlock, false);
					}
				}
			}
		}
	}
	
}
