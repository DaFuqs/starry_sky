package de.dafuqs.starryskies.spheroids.spheroids;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.command.argument.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;

import java.util.*;

import static de.dafuqs.starryskies.Support.BLOCKSTATE_STRING_CODEC;
import static de.dafuqs.starryskies.Support.BLOCK_RESULT_CODEC;

public class ShellSpheroid extends Spheroid {
	
	protected BlockState innerBlock;
	protected BlockState shellBlock;
	protected float shellRadius;
	private final LinkedHashMap<BlockArgumentParser.BlockResult, Float> shellSpeckleBlockStates;
	
	public ShellSpheroid(Spheroid.Template<?> template, float radius, List<SpheroidDecorator> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
						 BlockState innerBlock, BlockState shellBlock, float shellRadius, LinkedHashMap<BlockArgumentParser.BlockResult, Float> shellSpeckleBlockStates) {
		
		super(template, radius, decorators, spawns, random);
		this.radius = radius;
		this.innerBlock = innerBlock;
		this.shellBlock = shellBlock;
		this.shellRadius = shellRadius;
		this.shellSpeckleBlockStates = shellSpeckleBlockStates;
	}
	
	public static class Template extends Spheroid.Template<Template.Config> {

		public record Config(BlockState innerBlock, BlockStateSupplier shellBlock, int minShellRadius, int maxShellRadius, Map<BlockArgumentParser.BlockResult, Float> shellSpeckleBlockStates) {
			public static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							BLOCKSTATE_STRING_CODEC.fieldOf("main_block").forGetter(Config::innerBlock),
							BlockStateSupplier.CODEC.fieldOf("shell_block").forGetter(Config::shellBlock),
							Codec.INT.fieldOf("min_shell_size").forGetter(Config::minShellRadius),
							Codec.INT.fieldOf("max_shell_size").forGetter(Config::maxShellRadius),
							Codec.unboundedMap(BLOCK_RESULT_CODEC, Codec.FLOAT).fieldOf("shell_speckles").forGetter(Config::shellSpeckleBlockStates)
					).apply(instance, Config::new)
			);
		};

		public static final MapCodec<Template> CODEC = createCodec(Config.CODEC, Template::new);
		
		private final BlockState innerBlock;
		private final BlockStateSupplier shellBlock;
		private final int minShellRadius;
		private final int maxShellRadius;
		private final LinkedHashMap<BlockArgumentParser.BlockResult, Float> shellSpeckleBlockStates = new LinkedHashMap<>();

		public Template(SharedConfig shared, Config config) {
			super(shared);
			this.innerBlock = config.innerBlock;
			this.shellBlock = config.shellBlock;
			this.minShellRadius = config.minShellRadius;
			this.maxShellRadius = config.maxShellRadius;
			this.shellSpeckleBlockStates.putAll(config.shellSpeckleBlockStates);
		}

		@Override
		public SpheroidTemplateType<Template> getType() {
			return SpheroidTemplateType.SHELL;
		}

		@Override
		public Config config() {
			return new Config(innerBlock, shellBlock, minShellRadius, maxShellRadius, shellSpeckleBlockStates);
		}

		@Override
		public ShellSpheroid generate(ChunkRandom random) {
			return new ShellSpheroid(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, innerBlock, shellBlock.get(random), randomBetween(random, minShellRadius, maxShellRadius), shellSpeckleBlockStates);
		}
		
	}
	
	public String getDescription() {
		StringBuilder s = new StringBuilder("+++ ShellSpheroid +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.template.getID() +
				"\nRadius: " + this.radius +
				"\nShell: " + this.shellBlock.toString() + " (Radius: " + this.shellRadius + ")" +
				"\nCore: " + this.innerBlock.toString());
		
		for (Map.Entry<BlockArgumentParser.BlockResult, Float> speckle : this.shellSpeckleBlockStates.entrySet()) {
			s.append("\nShell: ").append(speckle.getKey().toString()).append(" (Radius: ").append(speckle.getValue()).append(")");
		}
		
		return s.toString();
	}
	
	@Override
	public void generate(Chunk chunk) {
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;
		
		int x = this.getPosition().getX();
		int y = this.getPosition().getY();
		int z = this.getPosition().getZ();
		
		boolean hasSpeckles = hasSpeckles();
		
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
					
					if (d <= (this.radius - this.shellRadius)) {
						chunk.setBlockState(currBlockPos, this.innerBlock, false);
					} else {
						if (hasSpeckles) {
							boolean set = false;
							BlockState finalBlockState = shellBlock;
							for (Map.Entry<BlockArgumentParser.BlockResult, Float> shellSpeckleBlockState : shellSpeckleBlockStates.entrySet()) {
								if (random.nextFloat() < shellSpeckleBlockState.getValue()) {
									setBlockResult(chunk, currBlockPos, shellSpeckleBlockState.getKey());
									set = true;
									break;
								}
							}
							if (!set) {
								chunk.setBlockState(currBlockPos, finalBlockState, false);
							}
						} else {
							chunk.setBlockState(currBlockPos, this.shellBlock, false);
						}
					}
				}
			}
		}
	}
	
	private boolean hasSpeckles() {
		return !this.shellSpeckleBlockStates.isEmpty();
	}
	
}
