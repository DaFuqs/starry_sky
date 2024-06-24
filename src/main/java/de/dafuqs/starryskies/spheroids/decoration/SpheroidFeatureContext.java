package de.dafuqs.starryskies.spheroids.decoration;

import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class SpheroidFeatureContext<FC extends SpheroidFeatureConfig> {
	
	private final StructureWorldAccess world;
	private final Random random;
	private final Spheroid spheroid;
	private final ChunkPos chunkPos;
	private final FC config;
	
	public SpheroidFeatureContext(StructureWorldAccess world, Random random, ChunkPos chunkPos, Spheroid spheroid, FC config) {
		this.world = world;
		this.random = random;
		this.chunkPos = chunkPos;
		this.spheroid = spheroid;
		this.config = config;
	}
	
	public StructureWorldAccess getWorld() {
		return this.world;
	}
	
	public Random getRandom() {
		return this.random;
	}
	
	public Spheroid getSpheroid() {
		return this.spheroid;
	}
	
	public ChunkPos getChunkPos() {
		return this.chunkPos;
	}
	
	public FC getConfig() {
		return this.config;
	}
}
