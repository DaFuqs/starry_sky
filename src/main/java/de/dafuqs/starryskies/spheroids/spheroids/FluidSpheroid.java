package de.dafuqs.starryskies.spheroids.spheroids;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.fluid.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.stateprovider.*;

import java.util.*;

public class FluidSpheroid extends Spheroid {
	
	private final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
	private final BlockState fluidBlock;
	private final BlockStateProvider shellBlock;
	private final float shellRadius;
	private final float fillAmount;
	private final boolean holeInBottom;
	
	public FluidSpheroid(Spheroid.Template<?> template, float radius, List<ConfiguredSpheroidFeature<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
						 BlockState fluidBlock, BlockStateProvider shellBlock, float shellRadius, float fillAmount, boolean holeInBottom) {
		
		super(template, radius, decorators, spawns, random);
		this.fluidBlock = fluidBlock;
		this.shellBlock = shellBlock;
		this.shellRadius = shellRadius;
		this.fillAmount = fillAmount;
		this.holeInBottom = holeInBottom;
	}
	
	@Override
	public String getDescription(DynamicRegistryManager registryManager) {
		return "+++ FluidSpheroid +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.getID(registryManager) +
				"\nRadius: " + this.radius +
				"\nShell: " + this.shellBlock.toString() + "(Radius: " + this.shellRadius + ")" +
				"\nLiquid: " + this.fluidBlock.toString() +
				"\nFill Percent: " + this.fillAmount +
				"\nHole in bottom: " + this.holeInBottom;
	}
	
	@Override
	public void generate(Chunk chunk, DynamicRegistryManager registryManager) {
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
						chunk.setBlockState(currBlockPos, this.shellBlock.get(random, currBlockPos), false);
					}
				}
			}
		}
	}
	
	public static class Template extends Spheroid.Template<Template.Config> {
		
		public static final MapCodec<Template> CODEC = createCodec(Config.CODEC, Template::new);
		private final Fluid fluid;
		private final BlockStateProvider shellBlock;
		private final int minShellRadius;
		private final int maxShellRadius;
		private final float minFillAmount;
		private final float maxFillAmount;
		private final float holeInBottomChance;
		
		public Template(SharedConfig shared, Config config) {
			super(shared);
			this.fluid = config.fluid;
			this.shellBlock = config.shellBlock;
			this.minShellRadius = config.minShellRadius;
			this.maxShellRadius = config.maxShellRadius;
			this.minFillAmount = config.minFillAmount;
			this.maxFillAmount = config.maxFillAmount;
			this.holeInBottomChance = config.holeInBottomChance;
		}
		
		@Override
		public SpheroidTemplateType<Template> getType() {
			return SpheroidTemplateType.FLUID;
		}
		
		@Override
		public Config config() {
			return new Config(fluid, shellBlock, minShellRadius, maxShellRadius,
					minFillAmount, maxFillAmount, holeInBottomChance);
		}
		
		@Override
		public FluidSpheroid generate(ChunkRandom random, DynamicRegistryManager registryManager) {
			int shellRadius = Support.getRandomBetween(random, this.minShellRadius, this.maxShellRadius);
			float fillAmount = Support.getRandomBetween(random, this.minFillAmount, this.maxFillAmount);
			boolean holeInBottom = random.nextFloat() < this.holeInBottomChance;
			BlockState fluidBlockState = this.fluid.getDefaultState().getBlockState();
			return new FluidSpheroid(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, fluidBlockState, shellBlock, shellRadius, fillAmount, holeInBottom);
		}
		
		public record Config(Fluid fluid, BlockStateProvider shellBlock, int minShellRadius, int maxShellRadius,
							 float minFillAmount, float maxFillAmount, float holeInBottomChance) {
			public static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							Registries.FLUID.getCodec().fieldOf("fluid").forGetter(Config::fluid),
							BlockStateProvider.TYPE_CODEC.fieldOf("shell_block").forGetter(Config::shellBlock),
							Codec.INT.fieldOf("min_shell_size").forGetter(Config::minShellRadius),
							Codec.INT.fieldOf("max_shell_size").forGetter(Config::maxShellRadius),
							Codec.FLOAT.fieldOf("min_fill_amount").forGetter(Config::minFillAmount),
							Codec.FLOAT.fieldOf("max_fill_amount").forGetter(Config::maxFillAmount),
							Codec.FLOAT.fieldOf("hole_in_bottom_chance").forGetter(Config::holeInBottomChance)
					).apply(instance, Config::new)
			);
		}
		
	}
	
}
