package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.command.argument.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.stateprovider.*;

import java.util.*;

import static de.dafuqs.starryskies.Support.*;

public class ShellSphere extends PlacedSphere {

	private final LinkedHashMap<BlockArgumentParser.BlockResult, Float> shellSpeckleBlockStates;
	protected BlockState innerBlock;
	protected BlockStateProvider shellBlock;
	protected float shellRadius;

	public ShellSphere(ConfiguredSphere<?> template, float radius, List<ConfiguredSphereDecorator<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
					   BlockState innerBlock, BlockStateProvider shellBlock, float shellRadius, LinkedHashMap<BlockArgumentParser.BlockResult, Float> shellSpeckleBlockStates) {

		super(template, radius, decorators, spawns, random);
		this.radius = radius;
		this.innerBlock = innerBlock;
		this.shellBlock = shellBlock;
		this.shellRadius = shellRadius;
		this.shellSpeckleBlockStates = shellSpeckleBlockStates;
	}

	@Override
	public String getDescription(DynamicRegistryManager registryManager) {
		StringBuilder s = new StringBuilder("+++ ShellSphere +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.getID(registryManager) +
				"\nRadius: " + this.radius +
				"\nShell: " + this.shellBlock.toString() + " (Radius: " + this.shellRadius + ")" +
				"\nCore: " + this.innerBlock.toString());

		for (Map.Entry<BlockArgumentParser.BlockResult, Float> speckle : this.shellSpeckleBlockStates.entrySet()) {
			s.append("\nShell: ").append(speckle.getKey().toString()).append(" (Radius: ").append(speckle.getValue()).append(")");
		}

		return s.toString();
	}

	@Override
	public void generate(Chunk chunk, DynamicRegistryManager registryManager) {
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
							for (Map.Entry<BlockArgumentParser.BlockResult, Float> shellSpeckleBlockState : shellSpeckleBlockStates.entrySet()) {
								if (random.nextFloat() < shellSpeckleBlockState.getValue()) {
									setBlockResult(registryManager, chunk, currBlockPos, shellSpeckleBlockState.getKey());
									set = true;
									break;
								}
							}
							if (!set) {
								chunk.setBlockState(currBlockPos, shellBlock.get(random, currBlockPos), false);
							}
						} else {
							chunk.setBlockState(currBlockPos, this.shellBlock.get(random, currBlockPos), false);
						}
					}
				}
			}
		}
	}

	public boolean hasSpeckles() {
		return !this.shellSpeckleBlockStates.isEmpty();
	}

	public static class Config extends ConfiguredSphere<Config.Config> {
		public static final MapCodec<ShellSphere.Config> CODEC = createCodec(ShellSphere.Config.Config.CODEC, ShellSphere.Config::new);
		private final BlockState innerBlock;
		private final BlockStateProvider shellBlock;
		private final int minShellRadius;
		private final int maxShellRadius;
		private final LinkedHashMap<BlockArgumentParser.BlockResult, Float> shellSpeckleBlockStates = new LinkedHashMap<>();

		public Config(SphereConfig.SharedConfig shared, Config config) {
			super(shared);
			this.innerBlock = config.innerBlock;
			this.shellBlock = config.shellBlock;
			this.minShellRadius = config.minShellRadius;
			this.maxShellRadius = config.maxShellRadius;
			config.speckleEntry.ifPresent(speckleEntry -> this.shellSpeckleBlockStates.put(speckleEntry.result, speckleEntry.chance));
		}

		@Override
		public Sphere<ShellSphere.Config> getType() {
			return Sphere.SHELL;
		}

		@Override
		public Config config() {
			return new Config(innerBlock, shellBlock, minShellRadius, maxShellRadius, !shellSpeckleBlockStates.isEmpty() ? Optional.of(new Config.SpeckleEntry(shellSpeckleBlockStates.firstEntry())) : Optional.empty());
		}

		@Override
		public ShellSphere generate(ChunkRandom random, DynamicRegistryManager registryManager) {
			return new ShellSphere(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, innerBlock, shellBlock, randomBetween(random, minShellRadius, maxShellRadius), shellSpeckleBlockStates);
		}

		// NOTE: Special-casing singular speckle entry (de)serialization.
		public record Config(BlockState innerBlock, BlockStateProvider shellBlock, int minShellRadius,
							 int maxShellRadius, Optional<SpeckleEntry> speckleEntry) {

			public static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							BLOCKSTATE_STRING_CODEC.fieldOf("main_block").forGetter(ShellSphere.Config.Config::innerBlock),
							BlockStateProvider.TYPE_CODEC.fieldOf("shell_block").forGetter(ShellSphere.Config.Config::shellBlock),
							Codec.INT.fieldOf("min_shell_size").forGetter(ShellSphere.Config.Config::minShellRadius),
							Codec.INT.fieldOf("max_shell_size").forGetter(ShellSphere.Config.Config::maxShellRadius),
							SpeckleEntry.CODEC.codec().lenientOptionalFieldOf("shell_speckles").forGetter(ShellSphere.Config.Config::speckleEntry)
					).apply(instance, ShellSphere.Config.Config::new)
			);

			public record SpeckleEntry(BlockArgumentParser.BlockResult result, Float chance) {
				public static final MapCodec<SpeckleEntry> CODEC = RecordCodecBuilder.mapCodec(
						instance -> instance.group(
								BLOCK_RESULT_CODEC.fieldOf("block").forGetter(SpeckleEntry::result),
								Codec.FLOAT.fieldOf("chance").forGetter(SpeckleEntry::chance)
						).apply(instance, SpeckleEntry::new)
				);

				public SpeckleEntry(Map.Entry<BlockArgumentParser.BlockResult, Float> e) {
					this(e.getKey(), e.getValue());
				}
			}
		}

	}

}
	
