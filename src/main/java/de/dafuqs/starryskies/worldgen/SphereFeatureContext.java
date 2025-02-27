package de.dafuqs.starryskies.worldgen;

import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class SphereFeatureContext<FC extends SphereDecoratorConfig> {

	private final StructureWorldAccess world;
	private final Random random;
	private final PlacedSphere<?> sphere;
	private final ChunkPos chunkPos;
	private final FC config;
	
	public SphereFeatureContext(StructureWorldAccess world, Random random, ChunkPos chunkPos, PlacedSphere<?> sphere, FC config) {
		this.world = world;
		this.random = random;
		this.chunkPos = chunkPos;
		this.sphere = sphere;
		this.config = config;
	}

	public StructureWorldAccess getWorld() {
		return this.world;
	}

	public Random getRandom() {
		return this.random;
	}
	
	public PlacedSphere<?> getSphere() {
		return this.sphere;
	}

	public ChunkPos getChunkPos() {
		return this.chunkPos;
	}

	public FC getConfig() {
		return this.config;
	}
}
