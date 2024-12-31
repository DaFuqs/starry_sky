package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.*;
import net.minecraft.structure.*;
import net.minecraft.util.*;
import net.minecraft.util.collection.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.floatprovider.*;
import net.minecraft.util.math.intprovider.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;

import java.util.*;

public class StrongholdSphere extends Sphere<StrongholdSphere.Config> {
	
	private static final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();
	private static final BlockState STONE_BRICKS = Blocks.STONE_BRICKS.getDefaultState();
	private static final BlockState INFESTED_STONE_BRICKS = Blocks.INFESTED_STONE_BRICKS.getDefaultState();
	private static final BlockState MOSSY_STONE_BRICKS = Blocks.MOSSY_STONE_BRICKS.getDefaultState();
	private static final BlockState OAK_PLANKS = Blocks.OAK_PLANKS.getDefaultState();
	private static final BlockState END_PORTAL_FRAME = Blocks.END_PORTAL_FRAME.getDefaultState();
	private static final BlockState LAVA = Blocks.LAVA.getDefaultState();
	private static final BlockState IRON_BARS = Blocks.IRON_BARS.getDefaultState();
	private static final BlockState BOOKSHELF = Blocks.BOOKSHELF.getDefaultState();
	
	public StrongholdSphere(Codec<StrongholdSphere.Config> codec) {
		super(codec);
	}
	
	@Override
	public PlacedSphere<?> generate(ConfiguredSphere<? extends Sphere<StrongholdSphere.Config>, Config> configuredSphere, Config config, ChunkRandom random, DynamicRegistryManager registryManager, BlockPos pos, float radius) {
		return new StrongholdSphere.Placed(configuredSphere, radius, configuredSphere.getDecorators(random), configuredSphere.getSpawns(random), random, config.shellThickness.get(random));
	}
	
	public static class Config extends SphereConfig {
		
		public static final Codec<StrongholdSphere.Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				SphereConfig.CONFIG_CODEC.forGetter((config) -> config),
				IntProvider.POSITIVE_CODEC.fieldOf("shell_thickness").forGetter((config) -> config.shellThickness)
		).apply(instance, (sphereConfig, shellThickness) -> new Config(sphereConfig.size, sphereConfig.decorators, sphereConfig.spawns, sphereConfig.generation, shellThickness)));
		
		protected final IntProvider shellThickness;
		
		public Config(FloatProvider size, Map<RegistryEntry<ConfiguredSphereDecorator<?, ?>>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation, IntProvider shellThickness) {
			super(size, decorators, spawns, generation);
			this.shellThickness = shellThickness;
		}
		
	}
	
	public static class Placed extends PlacedSphere<StrongholdSphere.Config> {
		
		private final float shellRadius;
		private final Identifier centerStructureId = Identifier.ofVanilla("fossil/skull_1");
		private final DataPool<Identifier> outerStructureIds = DataPool.<Identifier>builder().build();
		
		public Placed(ConfiguredSphere<? extends Sphere<StrongholdSphere.Config>, StrongholdSphere.Config> configuredSphere, float radius, List<RegistryEntry<ConfiguredSphereDecorator<?, ?>>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random, float shellRadius) {
			super(configuredSphere, radius, decorators, spawns, random);
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
						
						if (d <= shellRadius) {
							if (y2 % 10 == (this.position.getY() + 8) % 10 || x2 % 10 == (this.position.getX() + 5) % 10 || z2 % 10 == (this.position.getZ() + 5) % 10) {
								if ((y2 - y) % 6 == 0 && ((x2 - x) % 4 == 2 || (z2 - z) % 4 == 0)) {
									chunk.setBlockState(currBlockPos, MOSSY_STONE_BRICKS, false);
								} else {
									chunk.setBlockState(currBlockPos, STONE_BRICKS, false);
								}
							}
						} else {
							if (y2 % 2 == 0) {
								if (x2 % 5 == 0) {
									chunk.setBlockState(currBlockPos, INFESTED_STONE_BRICKS, false);
								} else {
									chunk.setBlockState(currBlockPos, STONE_BRICKS, false);
								}
							} else {
								if (x2 % 2 == 0 && z2 % 2 == 0) {
									chunk.setBlockState(currBlockPos, MOSSY_STONE_BRICKS, false);
								} else {
									chunk.setBlockState(currBlockPos, STONE_BRICKS, false);
								}
							}
						}
					}
				}
			}
		}
		
		@Override
		public String getDescription(DynamicRegistryManager registryManager) {
			return "+++ StrongholdSphere +++" +
					"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
					"\nTemplateID: " + this.getID(registryManager) +
					"\nRadius: " + this.radius +
					"\nShellRadius: " + this.shellRadius;
		}
		
		@Override
		public void decorate(StructureWorldAccess world, BlockPos origin, Random random) {
			super.decorate(world, origin, random);
			
			StructureTemplate template = world.getServer().getStructureTemplateManager().getTemplate(centerStructureId).orElse(null);
			if (template != null) {
				StructurePlacementData structurePlacementData = new StructurePlacementData().setRotation(BlockRotation.random(random)).setIgnoreEntities(false);
				BlockPos blockPos = this.position;
				template.place(world, blockPos, blockPos, structurePlacementData, StructureBlockBlockEntity.createRandom(this.position.asLong()), 2);
			}
		}
		
	}
	
}

