package de.dafuqs.starryskies.spheroids.spheroids;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;

import java.util.*;

import static de.dafuqs.starryskies.Support.*;

public class GeodeSpheroid extends Spheroid {
	
	private final BlockState innerBlockState;
	private final BlockState innerSpecklesBlockState;
	private final float speckleChance;
	private final BlockState middleBlockState;
	private final BlockState outerBlockState;
	
	public GeodeSpheroid(Spheroid.Template<?> template, float radius, List<ConfiguredSpheroidFeature<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
						 BlockState innerBlockState, BlockState innerSpecklesBlockState, float speckleChance, BlockState middleBlockState, BlockState outerBlockState) {
		
		super(template, radius, decorators, spawns, random);
		
		this.innerBlockState = innerBlockState;
		this.innerSpecklesBlockState = innerSpecklesBlockState;
		this.speckleChance = speckleChance;
		this.middleBlockState = middleBlockState;
		this.outerBlockState = outerBlockState;
	}
	
	@Override
	public String getDescription(DynamicRegistryManager registryManager) {
		return "+++ GeodeSpheroid +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.getID(registryManager) +
				"\nRadius: " + this.radius +
				"\nInnerBlock: " + this.innerBlockState +
				"\nInnerSpecklesBlock: " + this.innerSpecklesBlockState +
				"\nSpeckleChance: " + this.speckleChance +
				"\nMiddleBlock: " + this.middleBlockState +
				"\nOuterBlock: " + this.outerBlockState;
	}
	
	@Override
	public void generate(Chunk chunk, DynamicRegistryManager registryManager) {
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;
		
		int x = this.getPosition().getX();
		int y = this.getPosition().getY();
		int z = this.getPosition().getZ();
		
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
					
					if (d < this.radius - 4) {
						// nothing
					} else if (d < this.radius - 3) {
						if (random.nextFloat() < speckleChance) {
							chunk.setBlockState(currBlockPos, innerSpecklesBlockState, false);
						} else {
							chunk.setBlockState(currBlockPos, innerBlockState, false);
						}
					} else if (d < this.radius - 2) {
						chunk.setBlockState(currBlockPos, middleBlockState, false);
					} else if (d < this.radius - 1) {
						chunk.setBlockState(currBlockPos, outerBlockState, false);
					}
				}
			}
		}
	}
	
	public static class Template extends Spheroid.Template<Template.Config> {
		
		public static final MapCodec<Template> CODEC = createCodec(Config.CODEC, Template::new);
		private final BlockState innerBlockState;
		private final BlockState innerSpecklesBlockState;
		private final float speckleChance;
		private final BlockState middleBlockState;
		private final BlockState outerBlockState;
		
		public Template(SharedConfig shared, Config config) {
			super(shared);
			this.innerBlockState = config.innerBlockState;
			this.innerSpecklesBlockState = config.innerSpecklesBlockState;
			this.speckleChance = config.speckleChance;
			this.middleBlockState = config.middleBlockState;
			this.outerBlockState = config.outerBlockState;
		}
		
		@Override
		public SpheroidTemplateType<Template> getType() {
			return SpheroidTemplateType.GEODE;
		}
		
		@Override
		public Config config() {
			return new Config(innerBlockState, innerSpecklesBlockState, speckleChance, middleBlockState, outerBlockState);
		}
		
		@Override
		public GeodeSpheroid generate(ChunkRandom random, DynamicRegistryManager registryManager) {
			return new GeodeSpheroid(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, innerBlockState, innerSpecklesBlockState, speckleChance, middleBlockState, outerBlockState);
		}
		
		public record Config(BlockState innerBlockState, BlockState innerSpecklesBlockState, float speckleChance,
							 BlockState middleBlockState, BlockState outerBlockState) {
			public static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							BLOCKSTATE_STRING_CODEC.fieldOf("inner_block").forGetter(Config::innerBlockState),
							BLOCKSTATE_STRING_CODEC.fieldOf("inner_speckles_block").forGetter(Config::innerSpecklesBlockState),
							Codec.FLOAT.fieldOf("inner_speckles_block_chance").forGetter(Config::speckleChance),
							BLOCKSTATE_STRING_CODEC.fieldOf("middle_block").forGetter(Config::middleBlockState),
							BLOCKSTATE_STRING_CODEC.fieldOf("outer_block").forGetter(Config::outerBlockState)
					).apply(instance, Config::new)
			);
		}
		
	}
	
}
	
