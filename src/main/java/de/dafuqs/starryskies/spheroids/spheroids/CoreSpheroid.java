package de.dafuqs.starryskies.spheroids.spheroids;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.stateprovider.*;

import java.util.*;

public class CoreSpheroid extends Spheroid {
	
	private final BlockStateProvider coreBlock;
	private final BlockStateProvider shellBlock;
	private float coreRadius;
	
	public CoreSpheroid(Spheroid.Template<?> template, float radius, List<ConfiguredSpheroidFeature<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
						BlockStateProvider coreBlock, BlockStateProvider shellBlock, float coreRadius) {
		
		super(template, radius, decorators, spawns, random);
		this.coreBlock = coreBlock;
		this.shellBlock = shellBlock;
		this.coreRadius = coreRadius;
		
		if (this.coreRadius >= this.radius) {
			this.coreRadius = this.radius - 1;
		}
	}
	
	@Override
	public String getDescription(DynamicRegistryManager registryManager) {
		return "+++ CoreSpheroid +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.getID(registryManager) +
				"\nRadius: " + this.radius +
				"\nShell: " + this.shellBlock.toString() +
				"\nCore: " + this.coreBlock.toString() + " (Radius: " + this.coreRadius + ")";
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
						chunk.setBlockState(currBlockPos, this.coreBlock.get(random, currBlockPos), false);
					} else {
						chunk.setBlockState(currBlockPos, this.shellBlock.get(random, currBlockPos), false);
					}
				}
			}
		}
	}
	
	public static class Template extends Spheroid.Template<Template.Config> {
		
		public static final MapCodec<Template> CODEC = createCodec(Config.CODEC, Template::new);
		
		private final BlockStateProvider coreBlock;
		private final BlockStateProvider shellBlock;
		private final int minCoreRadius;
		private final int maxCoreRadius;
		
		public Template(SharedConfig shared, Config config) {
			super(shared);
			this.coreBlock = config.coreBlock;
			this.shellBlock = config.shellBlock;
			this.minCoreRadius = config.minCoreRadius;
			this.maxCoreRadius = config.maxCoreRadius;
		}
		
		@Override
		public SpheroidTemplateType<Template> getType() {
			return SpheroidTemplateType.CORE;
		}
		
		@Override
		public Config config() {
			return new Config(coreBlock, shellBlock, minCoreRadius, maxCoreRadius);
		}
		
		@Override
		public CoreSpheroid generate(ChunkRandom random, DynamicRegistryManager registryManager) {
			float radius = randomBetween(random, minSize, maxSize);
			int coreRadius = Support.getRandomBetween(random, this.minCoreRadius, this.maxCoreRadius);
			coreRadius = Math.min(coreRadius, (int) radius - 1);
			return new CoreSpheroid(this, radius, selectDecorators(random), selectSpawns(random), random, coreBlock, shellBlock, coreRadius);
		}
		
		public record Config(BlockStateProvider coreBlock, BlockStateProvider shellBlock, int minCoreRadius,
							 int maxCoreRadius) {
			public static final MapCodec<Template.Config> CODEC = RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							BlockStateProvider.TYPE_CODEC.fieldOf("core_block").forGetter(Config::coreBlock),
							BlockStateProvider.TYPE_CODEC.fieldOf("main_block").forGetter(Config::shellBlock),
							Codec.INT.fieldOf("min_core_size").forGetter(Config::minCoreRadius),
							Codec.INT.fieldOf("max_core_size").forGetter(Config::maxCoreRadius)
					).apply(instance, Config::new)
			);
		}
		
	}
	
}
