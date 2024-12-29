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
	public PlacedSphere<?> generate(ConfiguredSphere<? extends Sphere<ShellSphere.Config>, Config> configuredSphere, Config config, ChunkRandom random, DynamicRegistryManager registryManager, BlockPos pos, float radius) {
		return new ShellSphere.Placed(configuredSphere, radius, configuredSphere.getDecorators(random), configuredSphere.getSpawns(random), random, config.innerBlock.getForSphere(random, pos), config.shellBlock.getForSphere(random, pos), config.shellThickness.get(random));
	}
	
	public static class Config extends SphereConfig {
		
		public static final Codec<ShellSphere.Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				SphereConfig.CONFIG_CODEC.forGetter((config) -> config),
				SphereStateProvider.CODEC.fieldOf("main_block").forGetter((config) -> config.innerBlock),
				SphereStateProvider.CODEC.fieldOf("shell_block").forGetter((config) -> config.shellBlock),
				IntProvider.POSITIVE_CODEC.fieldOf("shell_thickness").forGetter((config) -> config.shellThickness)
		).apply(instance, (sphereConfig, innerBlock, shellBlock, shellThickness) -> new Config(sphereConfig.size, sphereConfig.decorators, sphereConfig.spawns, sphereConfig.generation, innerBlock, shellBlock, shellThickness)));
		
		protected final SphereStateProvider innerBlock;
		protected final SphereStateProvider shellBlock;
		protected final IntProvider shellThickness;
		
		public Config(FloatProvider size, Map<RegistryEntry<ConfiguredSphereDecorator<?, ?>>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation, SphereStateProvider innerBlock, SphereStateProvider shellBlock, IntProvider shellThickness) {
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
		
		public Placed(ConfiguredSphere<? extends Sphere<ShellSphere.Config>, ShellSphere.Config> configuredSphere, float radius, List<RegistryEntry<ConfiguredSphereDecorator<?, ?>>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
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
			return "+++ ShellSphere +++" +
					"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
					"\nTemplateID: " + this.getID(registryManager) +
					"\nRadius: " + this.radius +
					"\nShell: " + this.shellBlock.toString() + " (Radius: " + this.shellRadius + ")" +
					"\nCore: " + this.innerBlock.toString();
		}
	}
	
}
	
