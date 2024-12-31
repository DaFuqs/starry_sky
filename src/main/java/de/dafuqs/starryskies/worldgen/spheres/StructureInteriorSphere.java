package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.state_providers.*;
import de.dafuqs.starryskies.worldgen.*;
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
import net.minecraft.world.gen.stateprovider.*;

import java.util.*;

public class StructureInteriorSphere extends ShellSphere<StructureInteriorSphere.Config> {
	
	private final static int STRUCTURE_SIZE = 9;
	
	public StructureInteriorSphere(Codec<StructureInteriorSphere.Config> codec) {
		super(codec);
	}
	
	@Override
	public PlacedSphere<?> generate(ConfiguredSphere<? extends Sphere<Config>, Config> configuredSphere, Config config, ChunkRandom random, DynamicRegistryManager registryManager, BlockPos pos, float radius) {
		return new Placed(configuredSphere, radius, configuredSphere.getDecorators(random), configuredSphere.getSpawns(random), random,
				config.innerBlock.getForSphere(random, pos), config.shellBlock.getForSphere(random, pos), config.shellThickness.get(random), config.shellThickness.get(random), config.centerStructures, config.outerStructures);
	}
	
	public static class Config extends ShellSphere.Config {
		
		public static final Codec<StructureInteriorSphere.Config> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				SphereConfig.CONFIG_CODEC.forGetter((config) -> config),
				SphereStateProvider.CODEC.fieldOf("main_block").forGetter((config) -> config.innerBlock),
				SphereStateProvider.CODEC.fieldOf("shell_block").forGetter((config) -> config.shellBlock),
				IntProvider.POSITIVE_CODEC.fieldOf("shell_thickness").forGetter((config) -> config.shellThickness),
				DataPool.createCodec(Identifier.CODEC).fieldOf("center_structures").forGetter((config) -> config.centerStructures),
				DataPool.createCodec(Identifier.CODEC).fieldOf("structures").forGetter((config) -> config.outerStructures)
		).apply(instance, (sphereConfig, innerBlock, shellBlock, shellThickness, centerStructures, outerStructures)
				-> new Config(sphereConfig.size, sphereConfig.decorators, sphereConfig.spawns, sphereConfig.generation, innerBlock, shellBlock, shellThickness, centerStructures, outerStructures)));
		
		protected final IntProvider shellThickness;
		protected final DataPool<Identifier> centerStructures;
		protected final DataPool<Identifier> outerStructures;
		
		public Config(FloatProvider size, Map<RegistryEntry<ConfiguredSphereDecorator<?, ?>>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation,
					  SphereStateProvider innerBlock, SphereStateProvider shellBlock, IntProvider shellThickness, DataPool<Identifier> centerStructures, DataPool<Identifier> outerStructures) {
			super(size, decorators, spawns, generation, innerBlock, shellBlock, shellThickness);
			this.shellThickness = shellThickness;
			this.centerStructures = centerStructures;
			this.outerStructures = outerStructures;
		}
	}
	
	public static class Placed extends ShellSphere.Placed<StructureInteriorSphere.Config> {
		
		protected final float shellRadius;
		// These should all be 9x9x9 in size
		protected final DataPool<Identifier> centerStructures;
		protected final DataPool<Identifier> outerStructures;
		
		public Placed(ConfiguredSphere<? extends Sphere<Config>, Config> configuredSphere, float radius, List<RegistryEntry<ConfiguredSphereDecorator<?, ?>>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
					  BlockStateProvider innerBlock, BlockStateProvider shellBlock, int shellRadius, float shellRadius1, DataPool<Identifier> centerStructures, DataPool<Identifier> outerStructures) {
			super(configuredSphere, radius, decorators, spawns, random, innerBlock, shellBlock, shellRadius);
			this.shellRadius = shellRadius1;
			this.centerStructures = centerStructures;
			this.outerStructures = outerStructures;
		}
		
		@Override
		public String getDescription(DynamicRegistryManager registryManager) {
			return "+++ StructureInteriorSphere +++" +
					"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
					"\nTemplateID: " + this.getID(registryManager) +
					"\nRadius: " + this.radius +
					"\nShell: " + this.shellBlock.toString() + " (Radius: " + this.shellRadius + ")" +
					"\nCore: " + this.innerBlock.toString();
		}
		
		@Override
		public void decorate(StructureWorldAccess world, BlockPos origin, Random random) {
			super.decorate(world, origin, random);
			
			StructureTemplateManager templateManager = world.getServer().getStructureTemplateManager();
			
			ChunkPos chunkPos = new ChunkPos(origin);
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			
			int pivot = (STRUCTURE_SIZE - 1) / 2;
			int iMaxRadius = (int) (radius - shellRadius) - pivot - 1;
			int step = STRUCTURE_SIZE + 1;
			int start = ((int) (radius - shellRadius) / step) * step;
			
			for (int x2 = position.getX() - start; x2 <= position.getX() + start; x2 += step) {
				for (int y2 = position.getY() - start; y2 <= position.getY() + start; y2 += step) {
					for (int z2 = position.getZ() - start; z2 <= position.getZ() + start; z2 += step) {
						long d = Math.round(Support.getDistance(position.getX(), position.getY(), position.getZ(), x2, y2, z2));
						if (d < iMaxRadius) {
							mutable.set(x2, y2, z2);
							if (Support.isBlockPosInChunkPos(chunkPos, mutable)) {
								DataPool<Identifier> structurePool = d == 0 ? centerStructures : outerStructures;
								Identifier structureId = structurePool.getDataOrEmpty(random).get();
								StructureTemplate template = templateManager.getTemplate(structureId).orElse(null);
								if (template != null) {
									BlockPos set = mutable.set(x2 - pivot, y2, z2 - pivot).toImmutable();
									// TODO: what about giving them a random rotation via BlockRotation.random(random)? (need to adjust the pos, though)
									StructurePlacementData structurePlacementData = new StructurePlacementData().setRotation(BlockRotation.NONE).setIgnoreEntities(false);
									template.place(world, set, set, structurePlacementData, StructureBlockBlockEntity.createRandom(this.position.asLong()), 2);
								} else {
									StarrySkies.LOGGER.error("Trying to place a nonexistent structure template: {}", structureId);
								}
							}
						}
					}
				}
			}
		}
	}
	
}