package de.dafuqs.starryskies.dimension;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.registry.entry.*;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;
import net.minecraft.world.biome.source.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.noise.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.concurrent.*;

public class StarrySkyChunkGenerator extends ChunkGenerator {
	
	private final SystemGenerator systemGenerator;
	
	// Dimension Type values
	private final SpheroidDimensionType spheroidDimensionType;
	private final int floorHeight;
	private final BlockState floorBlockState;
	private final BlockState bottomBlockState;

	public static final MapCodec<StarrySkyChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
			(instance) -> instance.group(BiomeSource.CODEC.fieldOf("biome_source")
					.forGetter((generator) -> generator.biomeSource), Codecs.NONNEGATIVE_INT.fieldOf("settings")
					.forGetter((generator) -> generator.spheroidDimensionType.ordinal())
			).apply(instance, StarrySkyChunkGenerator::new));
	
	public StarrySkyChunkGenerator(BiomeSource biomeSource, int spheroidDimensionTypeOrdinal) {
		super(biomeSource);
		this.spheroidDimensionType = SpheroidDimensionType.values()[spheroidDimensionTypeOrdinal];
		this.systemGenerator = new SystemGenerator(spheroidDimensionType);
		this.floorBlockState = spheroidDimensionType.getFloorBlockState();
		this.bottomBlockState = spheroidDimensionType.getBottomBlockState();
		this.floorHeight = spheroidDimensionType.getFloorHeight();
	}
	
	@Override
	protected MapCodec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}
	
	@Override
	public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		
		int chunkPosStartX = chunkPos.getStartX();
		int chunkPosStartZ = chunkPos.getStartZ();
		
		// Generate floor if set
		if (floorHeight > 0) {
			for (int y = 0; y <= getSeaLevel(); y++) {
				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						chunk.setBlockState(new BlockPos(chunkPosStartX + x, y, chunkPosStartZ + z), getSeaBlock(y), false);
					}
				}
			}
		}
	}
	
	@Override
	public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess world, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {
		// no carver
	}
	
	private BlockState getSeaBlock(int heightY) {
		if (heightY == 0) {
			return bottomBlockState;
		} else {
			return floorBlockState;
		}
	}
	
	@Override
	public int getWorldHeight() {
		return StarrySkies.starryWorld.getHeight();
	}
	
	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
		placeSpheroids(chunk); // generate spheres
		return CompletableFuture.completedFuture(chunk);
	}
	
	@Override
	public void populateEntities(@NotNull ChunkRegion chunkRegion) {
		ChunkPos chunkPos = chunkRegion.getCenterPos();
		RegistryEntry<Biome> biome = chunkRegion.getBiome(chunkPos.getStartPos().withY(chunkRegion.getTopY() - 1));
		ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(RandomSeed.getSeed()));
		chunkRandom.setPopulationSeed(chunkRegion.getSeed(), chunkPos.getStartX(), chunkPos.getStartZ());
		SpawnHelper.populateEntities(chunkRegion, biome, chunkPos, chunkRandom);
		
		List<Spheroid> localSystem = systemGenerator.getSystemAtChunkPos(chunkPos.x, chunkPos.z);
		for (Spheroid spheroid : localSystem) {
			spheroid.populateEntities(chunkPos, chunkRegion, chunkRandom);
		}
	}
	
	@Override
	public int getSeaLevel() {
		return floorHeight;
	}
	
	@Override
	public int getMinimumY() {
		return 0;
	}
	
	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
		return floorHeight;
	}
	
	@Override
	public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
	
	}
	
	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
		BlockState[] states = new BlockState[world.getHeight()];
		Arrays.fill(states, Blocks.AIR.getDefaultState());
		return new VerticalBlockSample(world.getBottomY(), states);
	}
	
	public void placeSpheroids(@NotNull Chunk chunk) {
		ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(StarrySkies.starryWorld.getSeed()));
		chunkRandom.setCarverSeed(StarrySkies.starryWorld.getSeed(), chunk.getPos().getRegionX(), chunk.getPos().getRegionZ());
		
		List<Spheroid> localSystem = systemGenerator.getSystemAtChunkPos(chunk.getPos().x, chunk.getPos().z);
		for (Spheroid spheroid : localSystem) {
			if (spheroid.isInChunk(chunk.getPos())) {
                StarrySkies.LOGGER.debug("Generating spheroid in chunk x:{} z:{} (StartX:{} StartZ:{}) {}", chunk.getPos().x, chunk.getPos().z, chunk.getPos().getStartX(), chunk.getPos().getStartZ(), spheroid.getDescription());
				spheroid.generate(chunk);
				StarrySkies.LOGGER.debug("Generation Finished.");
			}
		}
	}
	
}