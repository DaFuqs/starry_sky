package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.state_providers.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.floatprovider.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.stateprovider.*;

import java.util.*;

public class CoreSphere extends Sphere<CoreSphere.Config> {
	
	public CoreSphere(Codec<CoreSphere.Config> codec) {
		super(codec);
	}
	
	@Override
	public PlacedSphere<?> generate(ConfiguredSphere<? extends Sphere<CoreSphere.Config>, Config> configuredSphere, Config config, ChunkRandom random, DynamicRegistryManager registryManager, BlockPos pos, float radius) {
		return new CoreSphere.Placed(configuredSphere, radius, configuredSphere.getDecorators(random), configuredSphere.getSpawns(random), random, config.mainBlock.getForSphere(random, pos), config.coreBlock.getForSphere(random, pos), config.coreRadius.get(random));
	}
	
	public static class Config extends SphereConfig {
		
		public static final Codec<CoreSphere.Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				SphereConfig.CONFIG_CODEC.forGetter((config) -> config),
				SphereStateProvider.CODEC.fieldOf("main_block").forGetter((config) -> config.mainBlock),
				SphereStateProvider.CODEC.fieldOf("core_block").forGetter((config) -> config.coreBlock),
				FloatProvider.createValidatedCodec(1.0F, 32.0F).fieldOf("core_radius").forGetter((config) -> config.coreRadius)
		).apply(instance, (sphereConfig, mainBlock, coreBlock, shellThickness) -> new Config(sphereConfig.size, sphereConfig.decorators, sphereConfig.spawns, sphereConfig.generation, mainBlock, coreBlock, shellThickness)));
		
		protected final SphereStateProvider mainBlock;
		protected final SphereStateProvider coreBlock;
		protected final FloatProvider coreRadius;
		
		public Config(FloatProvider size, Map<RegistryEntry<ConfiguredSphereDecorator<?, ?>>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation, SphereStateProvider coreBlock, SphereStateProvider mainBlock, FloatProvider coreRadius) {
			super(size, decorators, spawns, generation);
			this.coreBlock = coreBlock;
			this.mainBlock = mainBlock;
			this.coreRadius = coreRadius;
		}
		
	}
	
	public static class Placed extends PlacedSphere<CoreSphere.Config> {
		
		private final BlockStateProvider mainBlock;
		private final BlockStateProvider coreBlock;
		private final float coreRadius;
		
		public Placed(ConfiguredSphere<? extends Sphere<CoreSphere.Config>, CoreSphere.Config> configuredSphere, float radius, List<RegistryEntry<ConfiguredSphereDecorator<?, ?>>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
					  BlockStateProvider coreBlock, BlockStateProvider mainBlock, float coreRadius) {
			super(configuredSphere, radius, decorators, spawns, random);
			this.coreBlock = coreBlock;
			this.mainBlock = mainBlock;
			this.coreRadius = coreRadius;
		}
		
		@Override
		public void generate(Chunk chunk, DynamicRegistryManager registryManager) {
			int chunkX = chunk.getPos().x;
			int chunkZ = chunk.getPos().z;
			random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
			BlockPos spherePos = this.getPosition();
			int x = spherePos.getX();
			int y = spherePos.getY();
			int z = spherePos.getZ();
			
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
						
						if (d < this.coreRadius) {
							chunk.setBlockState(currBlockPos, this.coreBlock.get(random, currBlockPos), false);
						} else {
							chunk.setBlockState(currBlockPos, this.mainBlock.get(random, currBlockPos), false);
						}
					}
				}
			}
		}
		
		@Override
		public String getDescription(DynamicRegistryManager registryManager) {
			return "+++ CoreSphere +++" +
					"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
					"\nTemplateID: " + this.getID(registryManager) +
					"\nRadius: " + this.radius +
					"\nMain: " + this.mainBlock.toString() +
					"\nCore: " + this.coreBlock.toString() + " (Radius: " + this.coreRadius + ")";
		}
	}
	
}
	
