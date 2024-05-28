package de.dafuqs.starryskies.spheroids.spheroids;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;

import java.util.*;

public class OceanMonumentSpheroid extends Spheroid {
	
	private final BlockState water = Blocks.WATER.getDefaultState();
	private final BlockState prismarine = Blocks.PRISMARINE.getDefaultState();
	private final BlockState prismarine_bricks = Blocks.PRISMARINE_BRICKS.getDefaultState();
	private final BlockState dark_prismarine = Blocks.DARK_PRISMARINE.getDefaultState();
	private final BlockState sea_lantern = Blocks.SEA_LANTERN.getDefaultState();
	private final BlockState treasure = Blocks.WET_SPONGE.getDefaultState();
	
	private final int coreRadius;
	private final int shellRadius;
	
	private final ArrayList<BlockPos> guardianPositions = new ArrayList<>();
	
	public OceanMonumentSpheroid(Spheroid.Template<?> template, float radius, List<SpheroidDecorator> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
								 int coreRadius, int shellRadius) {
		
		super(template, radius, decorators, spawns, random);
		this.coreRadius = coreRadius;
		this.shellRadius = shellRadius;
	}
	
	public static class Template extends Spheroid.Template<Template.Config> {

		public record Config(int minShellRadius, int maxShellRadius, int minCoreRadius, int maxCoreRadius) {
			public static MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							Codec.INT.fieldOf("min_shell_size").forGetter(Config::minShellRadius),
							Codec.INT.fieldOf("max_shell_size").forGetter(Config::maxShellRadius),
							Codec.INT.fieldOf("min_core_size").forGetter(Config::minCoreRadius),
							Codec.INT.fieldOf("max_core_size").forGetter(Config::maxCoreRadius)
					).apply(instance, Config::new)
			);
		}

		public static MapCodec<Template> CODEC = createCodec(Config.CODEC, Template::new);
		
		private final int minShellRadius;
		private final int maxShellRadius;
		private final int minCoreRadius;
		private final int maxCoreRadius;
		
		public Template(SharedConfig shared, Config config) {
			super(shared);
			this.minShellRadius = config.minShellRadius;
			this.maxShellRadius = config.maxShellRadius;
			this.minCoreRadius = config.minCoreRadius;
			this.maxCoreRadius = config.maxCoreRadius;
		}

		@Override
		public SpheroidTemplateType<Template> getType() {
			return SpheroidTemplateType.OCEAN_MONUMENT;
		}

		@Override
		public Config config() {
			return new Config(minShellRadius, maxShellRadius, minCoreRadius, maxCoreRadius);
		}

		@Override
		public OceanMonumentSpheroid generate(ChunkRandom random) {
			int treasureRadius = Support.getRandomBetween(random, this.minCoreRadius, this.maxCoreRadius);
			int shellRadius = Support.getRandomBetween(random, this.minShellRadius, this.maxShellRadius);
			return new OceanMonumentSpheroid(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, treasureRadius, shellRadius);
		}
		
	}
	
	@Override
	public String getDescription() {
		return "+++ OceanMonumentSpheroid +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.template.getID() +
				"\nRadius: " + this.radius +
				"\nTreasure: " + this.treasure.toString() + " (Radius: " + this.coreRadius + ")";
	}
	
	@Override
	public void generate(Chunk chunk) {
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;
		
		int x = this.getPosition().getX();
		int y = this.getPosition().getY();
		int z = this.getPosition().getZ();
		
		float shellDistance = this.radius - this.shellRadius;
		
		random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
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
					
					if (d <= this.coreRadius) {
						chunk.setBlockState(currBlockPos, this.treasure, false);
					} else if (d < coreRadius + 3) {
						chunk.setBlockState(currBlockPos, water, false);
					} else if (d == coreRadius + 3) {
						if (Math.abs(x2 - x) < 2 || Math.abs(z2 - z) < 2) {
							chunk.setBlockState(currBlockPos, water, false);
						} else {
							chunk.setBlockState(currBlockPos, dark_prismarine, false);
						}
					} else if (d <= shellDistance) {
						if (y2 % 10 == 0 || x2 % 10 == 0 || z2 % 10 == 0) {
							if ((y2 - y) % 6 == 0 && ((x2 - x) % 4 == 2 || (z2 - z) % 4 == 0)) {
								chunk.setBlockState(currBlockPos, this.sea_lantern, false);
							} else {
								chunk.setBlockState(currBlockPos, this.prismarine_bricks, false);
							}
						} else {
							if (y2 % 10 == 5 && x2 % 10 == 5 && z2 % 10 == 5) {
								guardianPositions.add(currBlockPos);
							}
							chunk.setBlockState(currBlockPos, this.water, false);
						}
					} else {
						if (y2 % 2 == 0) {
							chunk.setBlockState(currBlockPos, this.prismarine, false);
						} else {
							chunk.setBlockState(currBlockPos, this.prismarine_bricks, false);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void populateEntities(ChunkPos chunkPos, ChunkRegion chunkRegion, ChunkRandom chunkRandom) {
		for (BlockPos guardianPosition : guardianPositions) {
			if (Support.isBlockPosInChunkPos(chunkPos, guardianPosition)) {
				
				MobEntity mobentity;
				if (random.nextFloat() < 0.08) {
					mobentity = EntityType.ELDER_GUARDIAN.create(chunkRegion.toServerWorld());
				} else {
					mobentity = EntityType.GUARDIAN.create(chunkRegion.toServerWorld());
				}
				
				if (mobentity != null) {
					float width = mobentity.getWidth();
					double xLength = MathHelper.clamp(guardianPosition.getX(), (double) chunkPos.getStartX() + (double) width, (double) chunkPos.getStartX() + 16.0D - (double) width);
					double zLength = MathHelper.clamp(guardianPosition.getZ(), (double) chunkPos.getStartZ() + (double) width, (double) chunkPos.getStartZ() + 16.0D - (double) width);
					
					try {
						mobentity.refreshPositionAndAngles(xLength, guardianPosition.getY(), zLength, random.nextFloat() * 360.0F, 0.0F);
						mobentity.setPersistent();
						if (mobentity.canSpawn(chunkRegion, SpawnReason.CHUNK_GENERATION) && mobentity.canSpawn(chunkRegion)) {
							mobentity.initialize(chunkRegion, chunkRegion.getLocalDifficulty(mobentity.getBlockPos()), SpawnReason.CHUNK_GENERATION, null);
							boolean success = chunkRegion.spawnEntity(mobentity);
							if (!success) {
								return;
							}
						}
					} catch (Exception exception) {
                        StarrySkies.LOGGER.warn("Failed to spawn mob on sphere {}\nException: {}", this.getDescription(), exception);
					}
				}
			}
		}
	}
	
}
