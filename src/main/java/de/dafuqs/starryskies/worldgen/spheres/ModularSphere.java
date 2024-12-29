package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.state_providers.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.floatprovider.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.stateprovider.*;

import java.util.*;

public class ModularSphere extends Sphere<ModularSphere.Config> {
	
	public ModularSphere(Codec<ModularSphere.Config> codec) {
		super(codec);
	}
	
	@Override
	public PlacedSphere<?> generate(ConfiguredSphere<? extends Sphere<ModularSphere.Config>, Config> configuredSphere, Config config, ChunkRandom random, DynamicRegistryManager registryManager, BlockPos pos, float radius) {
		BlockStateProvider mainProvider = config.mainBlock.getForSphere(random, pos);
		
		return new ModularSphere.Placed(configuredSphere, radius, configuredSphere.getDecorators(random), configuredSphere.getSpawns(random), random,
				mainProvider,
				config.topBlock.isPresent() ? config.topBlock.get().getForSphere(random, pos) : mainProvider,
				config.bottomBlock.isPresent() ? config.bottomBlock.get().getForSphere(random, pos) : mainProvider
		);
	}
	
	public static class Config extends SphereConfig {
		
		public static final Codec<ModularSphere.Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				SphereConfig.CONFIG_CODEC.forGetter((config) -> config),
				SphereStateProvider.CODEC.fieldOf("main_block").forGetter((config) -> config.mainBlock),
				SphereStateProvider.CODEC.optionalFieldOf("top_block").forGetter((config) -> config.topBlock),
				SphereStateProvider.CODEC.optionalFieldOf("bottom_block").forGetter((config) -> config.bottomBlock)
		).apply(instance, (sphereConfig, mainBlock, topBlock, bottomBlock) -> new Config(sphereConfig.size, sphereConfig.decorators, sphereConfig.spawns, sphereConfig.generation, mainBlock, topBlock, bottomBlock)));
		
		protected final SphereStateProvider mainBlock;
		protected final Optional<SphereStateProvider> topBlock;
		protected final Optional<SphereStateProvider> bottomBlock;
		
		public Config(FloatProvider size, Map<ConfiguredSphereDecorator<?, ?>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation, SphereStateProvider mainBlock, Optional<SphereStateProvider> topBlock, Optional<SphereStateProvider> bottomBlock) {
			super(size, decorators, spawns, generation);
			this.mainBlock = mainBlock;
			this.topBlock = topBlock;
			this.bottomBlock = bottomBlock;
		}
		
	}
	
	public static class Placed extends PlacedSphere<ModularSphere.Config> {
		
		private final BlockStateProvider mainBlock;
		private final BlockStateProvider topBlock;
		private final BlockStateProvider bottomBlock;
		
		public Placed(ConfiguredSphere<? extends Sphere<ModularSphere.Config>, ModularSphere.Config> configuredSphere, float radius, List<ConfiguredSphereDecorator<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
					  BlockStateProvider mainBlock, BlockStateProvider topBlock, BlockStateProvider bottomBlock) {
			super(configuredSphere, radius, decorators, spawns, random);
			this.mainBlock = mainBlock;
			this.topBlock = topBlock;
			this.bottomBlock = bottomBlock;
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
						
						if (this.bottomBlock != null && isBottomBlock(d, x2, y2, z2)) {
							chunk.setBlockState(currBlockPos, this.bottomBlock.get(random, currBlockPos), false);
						} else if (this.topBlock != null && isTopBlock(d, x2, y2, z2)) {
							chunk.setBlockState(currBlockPos, this.topBlock.get(random, currBlockPos), false);
						} else {
							chunk.setBlockState(currBlockPos, this.mainBlock.get(random, currBlockPos), false);
						}
					}
				}
			}
		}
		
		@Override
		public String getDescription(DynamicRegistryManager registryManager) {
			return "+++ ModularSphere +++" +
					"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
					"\nTemplateID: " + this.getID(registryManager) +
					"\nRadius: " + this.radius +
					"\nMaterial: " + this.mainBlock +
					"\nTopBlock: " + this.topBlock +
					"\nBottomBlock: " + this.bottomBlock;
		}
	}
	
}
	
