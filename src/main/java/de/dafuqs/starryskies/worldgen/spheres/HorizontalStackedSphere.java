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
import net.minecraft.util.math.floatprovider.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;

import java.util.*;

public class HorizontalStackedSphere extends Sphere<HorizontalStackedSphere.Config> {
	
	public HorizontalStackedSphere(Codec<Config> codec) {
		super(codec);
	}
	
	@Override
	public PlacedSphere<?> generate(ConfiguredSphere<? extends Sphere<HorizontalStackedSphere.Config>, HorizontalStackedSphere.Config> configuredSphere, HorizontalStackedSphere.Config config, ChunkRandom random, DynamicRegistryManager registryManager, BlockPos pos, float radius) {
		return new Placed(configuredSphere, radius, configuredSphere.getDecorators(random), configuredSphere.getSpawns(random), random, config.states);
	}
	
	public static class Config extends SphereConfig {
		
		public static final Codec<HorizontalStackedSphere.Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				SphereConfig.CONFIG_CODEC.forGetter((config) -> config),
				BlockState.CODEC.listOf().fieldOf("blocks").forGetter((config) -> config.states)
		).apply(instance, (sphereConfig, states) -> new Config(sphereConfig.size, sphereConfig.decorators, sphereConfig.spawns, sphereConfig.generation, states)));
		
		protected final List<BlockState> states;
		
		public Config(FloatProvider size, Map<ConfiguredSphereDecorator<?, ?>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation, List<BlockState> states) {
			super(size, decorators, spawns, generation);
			this.states = states;
		}
		
	}
	
	public static class Placed extends PlacedSphere<HorizontalStackedSphere.Config> {
		
		private final List<BlockState> states;
		
		public Placed(ConfiguredSphere<? extends Sphere<HorizontalStackedSphere.Config>, HorizontalStackedSphere.Config> configuredSphere, float radius, List<ConfiguredSphereDecorator<?, ?>> decorators,
					  List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random, List<BlockState> states) {
			super(configuredSphere, radius, decorators, spawns, random);
			this.states = states;
		}
		
		@Override
		public String getDescription(DynamicRegistryManager registryManager) {
			return "+++ HoprizontalStackedSphere +++" +
					"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
					"\nTemplateID: " + this.getID(registryManager) +
					"\nRadius: " + this.radius;
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
			for (int y2 = y - ceiledRadius; y2 <= y + ceiledRadius; y2++) {
				float currentSphereHeight = y - y2 + ceiledRadius;
				int currentBlockStateIndex = (int) ((currentSphereHeight * states.size() - 1) / (ceiledRadius * 2));
				BlockState currentBlockState = this.states.get(currentBlockStateIndex);
				
				for (int x2 = Math.max(chunkX * 16, x - ceiledRadius); x2 <= maxX; x2++) {
					for (int z2 = Math.max(chunkZ * 16, z - ceiledRadius); z2 <= maxZ; z2++) {
						long d = Math.round(Support.getDistance(x, y, z, x2, y2, z2));
						if (d > this.radius) {
							continue;
						}
						
						currBlockPos.set(x2, y2, z2);
						chunk.setBlockState(currBlockPos, currentBlockState, false);
					}
				}
			}
		}
		
	}
	
}
