package de.dafuqs.starryskies.spheroids.spheroids;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;

import java.util.*;

import static de.dafuqs.starryskies.Support.*;

public class MushroomSpheroid extends Spheroid {
	
	BlockState stemBlock;
	BlockState mushroomBlock;
	float shellRadius;
	
	public MushroomSpheroid(Spheroid.Template<?> template, float radius, List<ConfiguredSpheroidFeature<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
							BlockState stemBlock, BlockState mushroomBlock, float shellRadius) {
		
		super(template, radius, decorators, spawns, random);
		
		this.stemBlock = stemBlock;
		this.mushroomBlock = mushroomBlock;
		this.shellRadius = shellRadius;
	}
	
	@Override
	public String getDescription() {
		return "+++ MushroomSpheroid +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.template.getID() +
				"\nRadius: " + this.radius +
				"\nShell: " + this.mushroomBlock.toString() + " (Radius: " + this.shellRadius + ")" +
				"\nCore: " + this.stemBlock.toString();
	}
	
	@Override
	public void generate(Chunk chunk) {
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;
		
		int x = this.getPosition().getX();
		int y = this.getPosition().getY();
		int z = this.getPosition().getZ();
		random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
		
		// see: HugeRedMushroomFeature
		BlockState placementBlockstateInner = this.mushroomBlock.with(Properties.UP, false).with(Properties.NORTH, false).with(Properties.EAST, false).with(Properties.SOUTH, false).with(Properties.WEST, false).with(Properties.DOWN, false);
		
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
					
					long rounded = Math.round(d);
					if (rounded <= (this.radius - this.shellRadius)) {
						chunk.setBlockState(currBlockPos, this.stemBlock, false);
					} else if (d <= this.radius - 0.5) {
						chunk.setBlockState(currBlockPos, placementBlockstateInner, false);
					} else {
						// not perfectly correct, but eh
						BlockState placementBlockstateOuter = this.mushroomBlock.with(Properties.UP, true).with(Properties.NORTH, true).with(Properties.EAST, true).with(Properties.SOUTH, true).with(Properties.WEST, true).with(Properties.DOWN, true);
						chunk.setBlockState(currBlockPos, placementBlockstateOuter, false);
					}
				}
			}
		}
	}
	
	public static class Template extends Spheroid.Template<Template.Config> {
		
		public static final MapCodec<Template> CODEC = createCodec(Config.CODEC, Template::new);
		private final BlockState stemBlock;
		private final BlockState mushroomBlock;
		private final int minShellRadius;
		private final int maxShellRadius;
		
		public Template(SharedConfig shared, Config config) {
			super(shared);
			this.stemBlock = config.stemBlock;
			this.mushroomBlock = config.mushroomBlock;
			this.minShellRadius = config.minShellRadius;
			this.maxShellRadius = config.maxShellRadius;
		}
		
		@Override
		public SpheroidTemplateType<Template> getType() {
			return SpheroidTemplateType.MUSHROOM;
		}
		
		@Override
		public Config config() {
			return new Config(stemBlock, mushroomBlock, minShellRadius, maxShellRadius);
		}
		
		@Override
		public MushroomSpheroid generate(ChunkRandom random) {
			return new MushroomSpheroid(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, stemBlock, mushroomBlock, randomBetween(random, minShellRadius, maxShellRadius));
		}
		
		public record Config(BlockState stemBlock, BlockState mushroomBlock, int minShellRadius, int maxShellRadius) {
			public static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							BLOCKSTATE_STRING_CODEC.fieldOf("stem_block").forGetter(Config::stemBlock),
							BLOCKSTATE_STRING_CODEC.fieldOf("mushroom_block").forGetter(Config::mushroomBlock),
							Codec.INT.fieldOf("min_shell_size").forGetter(Config::minShellRadius),
							Codec.INT.fieldOf("max_shell_size").forGetter(Config::maxShellRadius)
					).apply(instance, Config::new)
			);
		}
		
	}
	
}
	
