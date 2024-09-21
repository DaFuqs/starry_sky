package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.stateprovider.*;

import java.util.*;

import static de.dafuqs.starryskies.Support.*;

public class ShellCoreSphere extends PlacedSphere {

	private final BlockState coreBlock;
	private final BlockState mainBlock;
	private final BlockStateProvider shellBlock;
	private final float coreRadius;
	private final float shellRadius;

	public ShellCoreSphere(ConfiguredSphere<?> template, float radius, List<ConfiguredSphereDecorator<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
						   BlockState coreBlock, BlockState mainBlock, BlockStateProvider shellBlock, float coreRadius, float shellRadius) {

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

	@Override
	public String getDescription(DynamicRegistryManager registryManager) {
		return "+++ DoubleCoreSphere +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.getID(registryManager) +
				"\nRadius: " + this.radius +
				"\nMain Block: " + this.mainBlock.toString() +
				"\nShell BLock: " + this.shellBlock.toString() + " (Radius: " + this.shellRadius + ")" +
				"\nCore Block: " + this.coreBlock.toString() + " (Radius: " + this.coreRadius + ")";
	}

	@Override
	public void generate(Chunk chunk, DynamicRegistryManager registryManager) {
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
						chunk.setBlockState(currBlockPos, this.shellBlock.get(random, currBlockPos), false);
					}
				}
			}
		}
	}

	public static class Config extends ConfiguredSphere<Config.Config> {

		public static final MapCodec<ShellCoreSphere.Config> CODEC = createCodec(ShellCoreSphere.Config.Config.CODEC, ShellCoreSphere.Config::new);
		private final BlockState mainBlock;
		private final BlockState coreBlock;
		private final BlockStateProvider shellBlock;
		private final int minCoreRadius;
		private final int maxCoreRadius;
		private final int minShellRadius;
		private final int maxShellRadius;

		public Config(SphereConfig.SharedConfig shared, Config config) {
			super(shared);
			this.mainBlock = config.mainBlock;
			this.coreBlock = config.coreBlock;
			this.shellBlock = config.shellBlock;
			this.minCoreRadius = config.minCoreRadius;
			this.maxCoreRadius = config.maxCoreRadius;
			this.minShellRadius = config.minShellRadius;
			this.maxShellRadius = config.maxShellRadius;
		}

		@Override
		public Sphere<ShellCoreSphere.Config> getType() {
			return Sphere.SHELL_CORE;
		}

		@Override
		public Config config() {
			return new Config(mainBlock, coreBlock, shellBlock, minCoreRadius, maxCoreRadius, minShellRadius, maxShellRadius);
		}

		@Override
		public ShellCoreSphere generate(ChunkRandom random, DynamicRegistryManager registryManager) {
			return new ShellCoreSphere(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, coreBlock, mainBlock,
					shellBlock, randomBetween(random, minCoreRadius, maxCoreRadius), randomBetween(random, minShellRadius, maxShellRadius));
		}

		public record Config(BlockState mainBlock, BlockState coreBlock, BlockStateProvider shellBlock,
							 int minCoreRadius, int maxCoreRadius, int minShellRadius, int maxShellRadius) {
			public static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							BLOCKSTATE_STRING_CODEC.fieldOf("main_block").forGetter(ShellCoreSphere.Config.Config::mainBlock),
							BLOCKSTATE_STRING_CODEC.fieldOf("core_block").forGetter(ShellCoreSphere.Config.Config::coreBlock),
							BlockStateProvider.TYPE_CODEC.fieldOf("shell_block").forGetter(ShellCoreSphere.Config.Config::shellBlock),
							Codec.INT.fieldOf("min_core_size").forGetter(ShellCoreSphere.Config.Config::minCoreRadius),
							Codec.INT.fieldOf("max_core_size").forGetter(ShellCoreSphere.Config.Config::maxCoreRadius),
							Codec.INT.fieldOf("min_shell_size").forGetter(ShellCoreSphere.Config.Config::minShellRadius),
							Codec.INT.fieldOf("max_shell_size").forGetter(ShellCoreSphere.Config.Config::maxShellRadius)
					).apply(instance, ShellCoreSphere.Config.Config::new)
			);
		}

	}

}
	
