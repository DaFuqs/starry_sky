package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.*;
import net.minecraft.util.*;
import net.minecraft.util.dynamic.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.floatprovider.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;

import java.util.*;

public class GeodeSphere extends Sphere<GeodeSphere.Config> {
	
	public GeodeSphere(Codec<GeodeSphere.Config> codec) {
		super(codec);
	}
	
	@Override
	public PlacedSphere<?> generate(ConfiguredSphere<? extends Sphere<GeodeSphere.Config>, Config> configuredSphere, Config config, ChunkRandom random, DynamicRegistryManager registryManager, BlockPos pos, float radius) {
		return new GeodeSphere.Placed(configuredSphere, radius, configuredSphere.getDecorators(random), configuredSphere.getSpawns(random), random, config.innerBlockState, config.innerSpecklesBlockState, config.speckleChance, config.middleBlockState, config.outerBlockState);
	}
	
	public static class Config extends SphereConfig {
		
		public static final Codec<GeodeSphere.Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				SphereConfig.CONFIG_CODEC.forGetter((config) -> config),
				BlockState.CODEC.fieldOf("inner_block").forGetter((config) -> config.innerBlockState),
				BlockState.CODEC.fieldOf("inner_speckles_block").forGetter((config) -> config.innerSpecklesBlockState),
				Codecs.POSITIVE_FLOAT.fieldOf("inner_speckles_block_chance").forGetter((config) -> config.speckleChance),
				BlockState.CODEC.fieldOf("middle_block").forGetter((config) -> config.middleBlockState),
				BlockState.CODEC.fieldOf("outer_block").forGetter((config) -> config.outerBlockState)
		).apply(instance, (sphereConfig, innerBlockState, innerSpecklesBlockState, speckleChance, middleBlockState, outerBlockState) -> new Config(sphereConfig.size, sphereConfig.decorators, sphereConfig.spawns, sphereConfig.generation, innerBlockState, innerSpecklesBlockState, speckleChance, middleBlockState, outerBlockState)));
		
		private final BlockState innerBlockState;
		private final BlockState innerSpecklesBlockState;
		private final float speckleChance;
		private final BlockState middleBlockState;
		private final BlockState outerBlockState;
		
		public Config(FloatProvider size, Map<RegistryEntry<ConfiguredSphereDecorator<?, ?>>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation,
					  BlockState innerBlockState, BlockState innerSpecklesBlockState, float speckleChance, BlockState middleBlockState, BlockState outerBlockState) {
			super(size, decorators, spawns, generation);
			this.innerBlockState = innerBlockState;
			this.innerSpecklesBlockState = innerSpecklesBlockState;
			this.speckleChance = speckleChance;
			this.middleBlockState = middleBlockState;
			this.outerBlockState = outerBlockState;
		}
		
	}
	
	public static class Placed extends PlacedSphere<GeodeSphere.Config> {
		
		private final BlockState innerBlockState;
		private final BlockState innerSpecklesBlockState;
		private final float speckleChance;
		private final BlockState middleBlockState;
		private final BlockState outerBlockState;
		
		public Placed(ConfiguredSphere<? extends Sphere<GeodeSphere.Config>, GeodeSphere.Config> configuredSphere, float radius, List<RegistryEntry<ConfiguredSphereDecorator<?, ?>>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
					  BlockState innerBlockState, BlockState innerSpecklesBlockState, float speckleChance, BlockState middleBlockState, BlockState outerBlockState) {
			super(configuredSphere, radius, decorators, spawns, random);
			this.innerBlockState = innerBlockState;
			this.innerSpecklesBlockState = innerSpecklesBlockState;
			this.speckleChance = speckleChance;
			this.middleBlockState = middleBlockState;
			this.outerBlockState = outerBlockState;
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
						
						if (d < this.radius - 4) {
							// nothing
						} else if (d < this.radius - 3) {
							if (random.nextFloat() < speckleChance) {
								chunk.setBlockState(currBlockPos, innerSpecklesBlockState, false);
							} else {
								chunk.setBlockState(currBlockPos, innerBlockState, false);
							}
						} else if (d < this.radius - 2) {
							chunk.setBlockState(currBlockPos, middleBlockState, false);
						} else if (d < this.radius - 1) {
							chunk.setBlockState(currBlockPos, outerBlockState, false);
						}
					}
				}
			}
		}
		
		@Override
		public String getDescription(DynamicRegistryManager registryManager) {
			return "+++ GeodeSphere +++" +
					"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
					"\nTemplateID: " + this.getID(registryManager) +
					"\nRadius: " + this.radius +
					"\nInnerBlock: " + this.innerBlockState +
					"\nInnerSpecklesBlock: " + this.innerSpecklesBlockState +
					"\nSpeckleChance: " + this.speckleChance +
					"\nMiddleBlock: " + this.middleBlockState +
					"\nOuterBlock: " + this.outerBlockState;
		}
	}
	
}
	
