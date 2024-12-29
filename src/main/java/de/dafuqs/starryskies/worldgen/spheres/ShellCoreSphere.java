package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.floatprovider.*;
import net.minecraft.util.math.intprovider.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.stateprovider.*;

import java.util.*;

public class ShellCoreSphere extends Sphere<ShellCoreSphere.Config> {
	
	public ShellCoreSphere(Codec<ShellCoreSphere.Config> codec) {
		super(codec);
	}
	
	@Override
	public PlacedSphere<?> generate(ConfiguredSphere<? extends Sphere<ShellCoreSphere.Config>, Config> configuredSphere, Config config, ChunkRandom random, DynamicRegistryManager registryManager) {
		return new ShellCoreSphere.Placed(configuredSphere, configuredSphere.getSize(random), configuredSphere.getDecorators(random), configuredSphere.getSpawns(random), random, config.mainBlock, config.shellBlock, config.shellThickness.get(random), config.coreBlock, config.coreRadius.get(random));
	}
	
	public static class Config extends SphereConfig {
		
		public static final Codec<ShellCoreSphere.Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				SphereConfig.CONFIG_CODEC.forGetter((config) -> config),
				BlockStateProvider.TYPE_CODEC.fieldOf("main_block").forGetter((config) -> config.mainBlock),
				BlockStateProvider.TYPE_CODEC.fieldOf("shell_block").forGetter((config) -> config.shellBlock),
				IntProvider.POSITIVE_CODEC.fieldOf("shell_thickness").forGetter((config) -> config.shellThickness),
				BlockStateProvider.TYPE_CODEC.fieldOf("core_block").forGetter((config) -> config.coreBlock),
				FloatProvider.createValidatedCodec(1.0F, 32.0F).fieldOf("core_radius").forGetter((config) -> config.coreRadius)
		).apply(instance, (sphereConfig, mainBlock, shellBlock, shellThickness, coreBlock, coreRadius) -> new Config(sphereConfig.size, sphereConfig.decorators, sphereConfig.spawns, sphereConfig.generation, mainBlock, shellBlock, shellThickness, coreBlock, coreRadius)));
		
		protected final BlockStateProvider mainBlock;
		protected final BlockStateProvider shellBlock;
		protected final IntProvider shellThickness;
		private final BlockStateProvider coreBlock;
		private final FloatProvider coreRadius;
		
		public Config(FloatProvider size, Map<ConfiguredSphereDecorator<?, ?>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation, BlockStateProvider mainBlock, BlockStateProvider shellBlock, IntProvider shellThickness, BlockStateProvider coreBlock, FloatProvider coreRadius) {
			super(size, decorators, spawns, generation);
			this.mainBlock = mainBlock;
			this.shellBlock = shellBlock;
			this.shellThickness = shellThickness;
			this.coreBlock = coreBlock;
			this.coreRadius = coreRadius;
		}
		
	}
	
	public static class Placed extends PlacedSphere<ShellCoreSphere.Config> {
		
		private final BlockStateProvider mainBlock;
		private final BlockStateProvider shellBlock;
		private final float shellRadius;
		private final BlockStateProvider coreBlock;
		private final float coreRadius;
		
		public Placed(ConfiguredSphere<? extends Sphere<ShellCoreSphere.Config>, ShellCoreSphere.Config> configuredSphere, float radius, List<ConfiguredSphereDecorator<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
					  BlockStateProvider mainBlock, BlockStateProvider shellBlock, float shellRadius, BlockStateProvider coreBlock, float coreRadius) {
			super(configuredSphere, radius, decorators, spawns, random);
			this.shellBlock = shellBlock;
			this.shellRadius = shellRadius;
			this.mainBlock = mainBlock;
			this.coreBlock = coreBlock;
			this.coreRadius = coreRadius;
		}
		
		@Override
		public void generate(Chunk chunk, DynamicRegistryManager registryManager) {
			int chunkX = chunk.getPos().x;
			int chunkZ = chunk.getPos().z;
			random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
			int x = this.getPosition().getX();
			int y = this.getPosition().getY();
			int z = this.getPosition().getZ();
			
			int ceiledRadius = (int) Math.ceil(this.radius);
			int maxX = Math.min(chunkX * 16 + 15, x + ceiledRadius);
			int maxZ = Math.min(chunkZ * 16 + 15, z + ceiledRadius);
			
			float liquidRadius = this.radius - this.shellRadius;
			
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
						} else if (d <= this.radius - this.shellRadius) {
							chunk.setBlockState(currBlockPos, this.mainBlock.get(random, currBlockPos), false);
						} else {
							chunk.setBlockState(currBlockPos, this.shellBlock.get(random, currBlockPos), false);
						}
					}
				}
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
	}
	
}
	
