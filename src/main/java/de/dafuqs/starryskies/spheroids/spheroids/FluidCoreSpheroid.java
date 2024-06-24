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

import java.util.*;

public class FluidCoreSpheroid extends Spheroid {
	
	private final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
	private final BlockState fluidBlock;
	private final BlockState shellBlock;
	private final float shellRadius;
	private final float fillAmount;
	private final boolean holeInBottom;
	private final BlockState coreBlock;
	private float coreRadius;
	
	public FluidCoreSpheroid(Spheroid.Template<?> template, float radius, List<ConfiguredSpheroidFeature<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
							 BlockState fluidBlock, BlockState shellBlock, float shellRadius, float fillAmount, boolean holeInBottom, BlockState coreBlock, float coreRadius) {
		
		super(template, radius, decorators, spawns, random);
		this.fluidBlock = fluidBlock;
		this.shellBlock = shellBlock;
		this.shellRadius = shellRadius;
		this.fillAmount = fillAmount;
		this.holeInBottom = holeInBottom;
		this.coreBlock = coreBlock;
		
		if (this.coreBlock != null) {
			this.coreRadius = coreRadius;
		} else {
			this.coreRadius = 0;
		}
		
		if (this.coreRadius >= this.radius - this.shellRadius - 1) {
			this.coreRadius = this.radius - this.shellRadius - 2;
		}
	}
	
	@Override
	public String getDescription() {
		return "+++ FluidCoreSpheroid +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.template.getID() +
				"\nRadius: " + this.radius +
				"\nShell: " + this.shellBlock.toString() + "(Radius: " + this.shellRadius + ")" +
				"\nLiquid: " + this.fluidBlock.toString() +
				"\nCore: " + this.coreBlock + "(Radius: " + this.coreRadius + ")" +
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
					} else if (d <= this.coreRadius) {
						chunk.setBlockState(currBlockPos, this.coreBlock, false);
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
	
	
	public static class Template extends Spheroid.Template<Template.Config> {
		
		public static final MapCodec<Template> CODEC = createCodec(Config.CODEC, Template::new);
		private final Fluid fluid;
		private final float minFillAmount;
		private final float maxFillAmount;
		private final float holeInBottomChance;
		private final BlockStateSupplier shellBlock;
		private final int minShellRadius;
		private final int maxShellRadius;
		private final BlockStateSupplier coreBlock;
		private final int minCoreRadius;
		private final int maxCoreRadius;
		
		public Template(SharedConfig shared, Config config) {
			super(shared);
			this.fluid = config.fluid;
			this.minFillAmount = config.minFillAmount;
			this.maxFillAmount = config.maxFillAmount;
			this.holeInBottomChance = config.holeInBottomChance;
			this.shellBlock = config.shellBlock;
			this.minShellRadius = config.minShellRadius;
			this.maxShellRadius = config.maxShellRadius;
			this.coreBlock = config.coreBlock;
			this.minCoreRadius = config.minCoreRadius;
			this.maxCoreRadius = config.maxCoreRadius;
		}
		
		@Override
		public SpheroidTemplateType<Template> getType() {
			return SpheroidTemplateType.CORE_FLUID;
		}
		
		@Override
		public Config config() {
			return new Config(fluid, minFillAmount, maxFillAmount, holeInBottomChance,
					shellBlock, minShellRadius, maxShellRadius,
					coreBlock, minCoreRadius, maxCoreRadius);
		}
		
		@Override
		public FluidCoreSpheroid generate(ChunkRandom random) {
			int shellRadius = Support.getRandomBetween(random, this.minShellRadius, this.maxShellRadius);
			int coreRadius = Support.getRandomBetween(random, this.minCoreRadius, this.maxCoreRadius);
			float fillAmount = Support.getRandomBetween(random, this.minFillAmount, this.maxFillAmount);
			boolean holeInBottom = random.nextFloat() < this.holeInBottomChance;
			BlockState fluidBlockState = this.fluid.getDefaultState().getBlockState();
			return new FluidCoreSpheroid(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, fluidBlockState, shellBlock.get(random), shellRadius, fillAmount, holeInBottom, coreBlock.get(random), coreRadius);
		}
		
		public record Config(Fluid fluid, float minFillAmount, float maxFillAmount, float holeInBottomChance,
							 BlockStateSupplier shellBlock, int minShellRadius, int maxShellRadius,
							 BlockStateSupplier coreBlock, int minCoreRadius, int maxCoreRadius) {
			public static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							Registries.FLUID.getCodec().fieldOf("fluid").forGetter(Config::fluid),
							Codec.FLOAT.fieldOf("min_fill_amount").forGetter(Config::minFillAmount),
							Codec.FLOAT.fieldOf("max_fill_amount").forGetter(Config::maxFillAmount),
							Codec.FLOAT.fieldOf("hole_in_bottom_chance").forGetter(Config::holeInBottomChance),
							BlockStateSupplier.CODEC.fieldOf("shell_block").forGetter(Config::shellBlock),
							Codec.INT.fieldOf("min_shell_size").forGetter(Config::minShellRadius),
							Codec.INT.fieldOf("max_shell_size").forGetter(Config::maxShellRadius),
							BlockStateSupplier.CODEC.fieldOf("core_block").forGetter(Config::coreBlock),
							Codec.INT.fieldOf("min_core_size").forGetter(Config::minCoreRadius),
							Codec.INT.fieldOf("max_core_size").forGetter(Config::maxCoreRadius)
					).apply(instance, Config::new)
			);
		}
		
	}
	
}
