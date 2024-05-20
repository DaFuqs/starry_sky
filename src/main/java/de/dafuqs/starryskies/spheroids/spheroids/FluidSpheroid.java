package de.dafuqs.starryskies.spheroids.spheroids;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.fluid.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;

import java.util.*;

public class FluidSpheroid extends Spheroid {
	
	private final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
	private final BlockState fluidBlock;
	private final BlockState shellBlock;
	private final float shellRadius;
	private final float fillAmount;
	private final boolean holeInBottom;
	
	public FluidSpheroid(Spheroid.Template template, float radius, List<SpheroidDecorator> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
						 BlockState fluidBlock, BlockState shellBlock, float shellRadius, float fillAmount, boolean holeInBottom) {
		
		super(template, radius, decorators, spawns, random);
		this.fluidBlock = fluidBlock;
		this.shellBlock = shellBlock;
		this.shellRadius = shellRadius;
		this.fillAmount = fillAmount;
		this.holeInBottom = holeInBottom;
	}
	
	public static class Template extends Spheroid.Template {
		
		private final Fluid fluid;
		private final BlockStateSupplier shellBlock;
		
		private final int minShellRadius;
		private final int maxShellRadius;
		private final float minFillAmount;
		private final float maxFillAmount;
		private final float holeInBottomChance;
		
		public Template(Identifier identifier, JsonObject data) throws CommandSyntaxException {
			super(identifier, data);
			
			JsonObject typeData = JsonHelper.getObject(data, "type_data");
			this.fluid = Registries.FLUID.get(Identifier.tryParse(JsonHelper.getString(typeData, "fluid")));
			this.minShellRadius = JsonHelper.getInt(typeData, "min_shell_size");
			this.maxShellRadius = JsonHelper.getInt(typeData, "max_shell_size");
			this.minFillAmount = JsonHelper.getFloat(typeData, "min_fill_amount");
			this.maxFillAmount = JsonHelper.getFloat(typeData, "max_fill_amount");
			this.holeInBottomChance = JsonHelper.getFloat(typeData, "hole_in_bottom_chance");
			this.shellBlock = BlockStateSupplier.of(typeData.get("shell_block"));
		}
		
		@Override
		public FluidSpheroid generate(ChunkRandom random) {
			int shellRadius = Support.getRandomBetween(random, this.minShellRadius, this.maxShellRadius);
			float fillAmount = Support.getRandomBetween(random, this.minFillAmount, this.maxFillAmount);
			boolean holeInBottom = random.nextFloat() < this.holeInBottomChance;
			BlockState fluidBlockState = this.fluid.getDefaultState().getBlockState();
			return new FluidSpheroid(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, fluidBlockState, shellBlock.get(random), shellRadius, fillAmount, holeInBottom);
		}
		
	}
	
	public String getDescription() {
		return "+++ FluidSpheroid +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.template.getID() +
				"\nRadius: " + this.radius +
				"\nShell: " + this.shellBlock.toString() + "(Radius: " + this.shellRadius + ")" +
				"\nLiquid: " + this.fluidBlock.toString() +
				"\nFill Percent: " + this.fillAmount +
				"\nHole in bottom: " + this.holeInBottom;
	}
	
	@Override
	public void generate(Chunk chunk) {
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;
		
		int x = this.getPosition().getX();
		int y = this.getPosition().getY();
		int z = this.getPosition().getZ();
		
		float liquidRadius = this.radius - this.shellRadius;
		float maxLiquidY = y + (this.fillAmount * liquidRadius * 2 - liquidRadius);
		
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
					
					if (this.holeInBottom && (x - x2) == 0 && (z - z2) == 0 && (y - y2 + 1) >= liquidRadius) {
						chunk.setBlockState(new BlockPos(currBlockPos), this.fluidBlock, false);
						chunk.markBlockForPostProcessing(currBlockPos); // making it drop down after generation
					} else if (d <= liquidRadius) {
						if (y2 <= maxLiquidY) {
							chunk.setBlockState(currBlockPos, this.fluidBlock, false);
						} else {
							chunk.setBlockState(currBlockPos, CAVE_AIR, false);
						}
					} else {
						chunk.setBlockState(currBlockPos, this.shellBlock, false);
					}
				}
			}
		}
	}
	
}
