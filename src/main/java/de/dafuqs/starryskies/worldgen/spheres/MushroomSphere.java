package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.floatprovider.*;
import net.minecraft.util.math.intprovider.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;

import java.util.*;

public class MushroomSphere extends Sphere<MushroomSphere.Config> {
	
	public MushroomSphere(Codec<MushroomSphere.Config> codec) {
		super(codec);
	}
	
	@Override
	public PlacedSphere<?> generate(ConfiguredSphere<? extends Sphere<MushroomSphere.Config>, Config> configuredSphere, Config config, ChunkRandom random, DynamicRegistryManager registryManager) {
		return new MushroomSphere.Placed(configuredSphere, configuredSphere.getSize(random), configuredSphere.getDecorators(random), configuredSphere.getSpawns(random), random, config.stemBlock, config.mushroomBlock, config.shellThickness.get(random));
	}
	
	public static class Config extends SphereConfig {
		
		public static final Codec<MushroomSphere.Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				SphereConfig.CONFIG_CODEC.forGetter((config) -> config),
				BlockState.CODEC.fieldOf("stem_block").forGetter((config) -> config.stemBlock),
				BlockState.CODEC.fieldOf("mushroom_block").forGetter((config) -> config.mushroomBlock),
				IntProvider.POSITIVE_CODEC.fieldOf("shell_thickness").forGetter((config) -> config.shellThickness)
		).apply(instance, (sphereConfig, stemBlock, mushroomBlock, shellThickness) -> new Config(sphereConfig.size, sphereConfig.decorators, sphereConfig.spawns, sphereConfig.generation, stemBlock, mushroomBlock, shellThickness)));
		
		protected final BlockState stemBlock;
		protected final BlockState mushroomBlock;
		protected final IntProvider shellThickness;
		
		public Config(FloatProvider size, Map<ConfiguredSphereDecorator<?, ?>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation, BlockState stemBlock, BlockState mushroomBlock, IntProvider shellThickness) {
			super(size, decorators, spawns, generation);
			this.stemBlock = stemBlock;
			this.mushroomBlock = mushroomBlock;
			this.shellThickness = shellThickness;
		}
		
	}
	
	public static class Placed extends PlacedSphere<MushroomSphere.Config> {
		
		private final BlockState stemBlock;
		private final BlockState mushroomBlock;
		private final float shellRadius;
		
		public Placed(ConfiguredSphere<? extends Sphere<MushroomSphere.Config>, MushroomSphere.Config> configuredSphere, float radius, List<ConfiguredSphereDecorator<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
					  BlockState stemBlock, BlockState mushroomBlock, float shellRadius) {
			super(configuredSphere, radius, decorators, spawns, random);
			this.stemBlock = stemBlock;
			this.mushroomBlock = mushroomBlock;
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
			
			// see: HugeRedMushroomFeature
			BlockState placementBlockstateInner = this.mushroomBlock.with(Properties.UP, false).with(Properties.NORTH, false).with(Properties.EAST, false).with(Properties.SOUTH, false).with(Properties.WEST, false).with(Properties.DOWN, false);
			
			
			BlockPos.Mutable currBlockPos = new BlockPos.Mutable();
			for (int x2 = Math.max(chunkX * 16, x - ceiledRadius); x2 <= maxX; x2++) {
				for (int y2 = y - ceiledRadius; y2 <= y + ceiledRadius; y2++) {
					for (int z2 = Math.max(chunkZ * 16, z - ceiledRadius); z2 <= maxZ; z2++) {
						long d = Math.round(Support.getDistance(x, y, z, x2, y2, z2));
						if (d > this.radius) {
							continue;
						}
						currBlockPos.set(x2, y2, z2);
						
						long rounded = Math.round(d);
						if (rounded <= (this.radius - this.shellRadius)) {
							chunk.setBlockState(currBlockPos, this.stemBlock, false);
						} else if (d <= this.radius - 0.5) {
							chunk.setBlockState(currBlockPos, placementBlockstateInner, false);
						} else {
							// not perfectly correct, but eh
							BlockState placementBlockstateOuter = this.mushroomBlock.with(net.minecraft.state.property.Properties.UP, true).with(net.minecraft.state.property.Properties.NORTH, true).with(net.minecraft.state.property.Properties.EAST, true).with(net.minecraft.state.property.Properties.SOUTH, true).with(net.minecraft.state.property.Properties.WEST, true).with(Properties.DOWN, true);
							chunk.setBlockState(currBlockPos, placementBlockstateOuter, false);
						}
					}
				}
			}
		}
		
		@Override
		public String getDescription(DynamicRegistryManager registryManager) {
			return "+++ MushroomSphere +++" +
					"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
					"\nTemplateID: " + this.getID(registryManager) +
					"\nRadius: " + this.radius +
					"\nMushroom: " + this.mushroomBlock.toString() + " (Radius: " + this.shellRadius + ")" +
					"\nStem: " + this.stemBlock.toString();
		}
	}
	
}
	
