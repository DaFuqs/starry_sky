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

public class ShellSphere extends Sphere<ShellSphere.Config> {
	
	public ShellSphere(Codec<ShellSphere.Config> codec) {
		super(codec);
	}
	
	@Override
	public PlacedSphere<?> generate(ConfiguredSphere<? extends Sphere<ShellSphere.Config>, Config> configuredSphere, Config config, ChunkRandom random, DynamicRegistryManager registryManager) {
		return new ShellSphere.Placed(configuredSphere, configuredSphere.getSize(random), configuredSphere.getDecorators(random), configuredSphere.getSpawns(random), random, config.innerBlock, config.shellBlock, config.shellThickness.get(random));
	}
	
	public static class Config extends SphereConfig {
		
		public static final Codec<ShellSphere.Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				SphereConfig.CONFIG_CODEC.forGetter((config) -> config),
				BlockStateProvider.TYPE_CODEC.fieldOf("main_block").forGetter((config) -> config.innerBlock),
				BlockStateProvider.TYPE_CODEC.fieldOf("shell_block").forGetter((config) -> config.shellBlock),
				IntProvider.POSITIVE_CODEC.fieldOf("shell_thickness").forGetter((config) -> config.shellThickness)
		).apply(instance, (sphereConfig, state) -> new ShellSphere.Config(sphereConfig.size, sphereConfig.decorators, sphereConfig.spawns, sphereConfig.generation, state)));
		
		protected BlockStateProvider innerBlock;
		protected BlockStateProvider shellBlock;
		protected IntProvider shellThickness;
		
		public Config(FloatProvider size, Map<ConfiguredSphereDecorator<?, ?>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation, BlockStateProvider innerBlock, BlockStateProvider shellBlock, IntProvider shellThickness) {
			super(size, decorators, spawns, generation);
			this.innerBlock = innerBlock;
			this.shellBlock = shellBlock;
			this.shellThickness = shellThickness;
		}
		
	}
	
	public static class Placed extends PlacedSphere<ShellSphere.Config> {
		
		private final BlockStateProvider innerBlock;
		private final BlockStateProvider shellBlock;
		private final float shellRadius;
		
		public Placed(ConfiguredSphere<? extends Sphere<ShellSphere.Config>, ShellSphere.Config> configuredSphere, float radius, List<ConfiguredSphereDecorator<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
					  BlockStateProvider innerBlock, BlockStateProvider shellBlock, float shellRadius) {
			super(configuredSphere, radius, decorators, spawns, random);
			this.innerBlock = innerBlock;
			this.shellBlock = shellBlock;
			this.shellRadius = shellRadius;
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
			
			BlockPos.Mutable currBlockPos = new BlockPos.Mutable();
			for (int x2 = Math.max(chunkX * 16, x - ceiledRadius); x2 <= maxX; x2++) {
				for (int y2 = y - ceiledRadius; y2 <= y + ceiledRadius; y2++) {
					for (int z2 = Math.max(chunkZ * 16, z - ceiledRadius); z2 <= maxZ; z2++) {
						long d = Math.round(Support.getDistance(x, y, z, x2, y2, z2));
						if (d > this.radius) {
							continue;
						}
						currBlockPos.set(x2, y2, z2);
						
						if (d <= (this.radius - this.shellRadius)) {
							chunk.setBlockState(currBlockPos, this.innerBlock.get(random, currBlockPos), false);
						} else {
							chunk.setBlockState(currBlockPos, this.shellBlock.get(random, currBlockPos), false);
						}
					}
				}
			}
		}
		
		@Override
		public String getDescription(DynamicRegistryManager registryManager) {
			StringBuilder s = new StringBuilder("+++ ShellSphere +++" +
					"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
					"\nTemplateID: " + this.getID(registryManager) +
					"\nRadius: " + this.radius +
					"\nShell: " + this.shellBlock.toString() + " (Radius: " + this.shellRadius + ")" +
					"\nCore: " + this.innerBlock.toString());
			
			return s.toString();
		}
	}
	
}
	
