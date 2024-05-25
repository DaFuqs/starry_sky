package de.dafuqs.starryskies.spheroids.spheroids;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;

import java.util.*;

import static de.dafuqs.starryskies.Support.BLOCKSTATE_STRING_CODEC;

public class ModularSpheroid extends Spheroid {
	
	private final BlockState mainBlock;
	private final BlockState topBlock;
	private final BlockState bottomBlock;
	
	public ModularSpheroid(Spheroid.Template<?> template, float radius, List<SpheroidDecorator> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
						   BlockState mainBlock, BlockState topBlock, BlockState bottomBlock) {
		
		super(template, radius, decorators, spawns, random);
		this.mainBlock = mainBlock;
		this.topBlock = topBlock;
		this.bottomBlock = bottomBlock;
	}
	
	public static class Template extends Spheroid.Template<Template.Config> {

		public record Config(BlockState mainBlock, BlockState topBlock, BlockState bottomBlock) {
			public static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							BLOCKSTATE_STRING_CODEC.fieldOf("main_block").forGetter(Config::mainBlock),
							BLOCKSTATE_STRING_CODEC.lenientOptionalFieldOf("top_block", null).forGetter(Config::topBlock),
							BLOCKSTATE_STRING_CODEC.lenientOptionalFieldOf("bottom_block", null).forGetter(Config::bottomBlock)
					).apply(instance, Config::new)
			);
		}

		public static final MapCodec<Template> CODEC = createCodec(Config.CODEC, Template::new);
		
		private final BlockState mainBlock;
		private final BlockState topBlock;
		private final BlockState bottomBlock;

		public Template(SharedConfig shared, Config config) {
			super(shared);
			this.mainBlock = config.mainBlock;
			this.topBlock = config.topBlock;
			this.bottomBlock = config.bottomBlock;
		}

		@Override
		public SpheroidTemplateType<Template> getType() {
			return SpheroidTemplateType.MODULAR;
		}

		@Override
		public Config config() {
			return new Config(mainBlock, topBlock, bottomBlock);
		}

		@Override
		public ModularSpheroid generate(ChunkRandom random) {
			return new ModularSpheroid(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, mainBlock, topBlock, bottomBlock);
		}
		
	}
	
	public String getDescription() {
		String s = "+++ ModularSpheroid +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.template.getID() +
				"\nRadius: " + this.radius +
				"\nMaterial: " + this.mainBlock.toString();
		
		if (this.topBlock != null) {
			s += "\nTopBlock: " + this.topBlock;
		}
		if (this.bottomBlock != null) {
			s += "\nBottomBlock: " + this.bottomBlock;
		}
		return s;
	}
	
	public void generate(Chunk chunk) {
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
					
					if (this.bottomBlock != null && isBottomBlock(d, x2, y2, z2)) {
						chunk.setBlockState(currBlockPos, this.bottomBlock, false);
					} else if (this.topBlock != null && isTopBlock(d, x2, y2, z2)) {
						chunk.setBlockState(currBlockPos, this.topBlock, false);
					} else {
						chunk.setBlockState(currBlockPos, this.mainBlock, false);
					}
				}
			}
		}
	}
	
}
