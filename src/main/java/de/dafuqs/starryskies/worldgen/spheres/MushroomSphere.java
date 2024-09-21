package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;

import java.util.*;

import static de.dafuqs.starryskies.Support.*;

public class MushroomSphere extends PlacedSphere {

	BlockState stemBlock;
	BlockState mushroomBlock;
	float shellRadius;

	public MushroomSphere(ConfiguredSphere<?> template, float radius, List<ConfiguredSphereDecorator<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
						  BlockState stemBlock, BlockState mushroomBlock, float shellRadius) {

		super(template, radius, decorators, spawns, random);

		this.stemBlock = stemBlock;
		this.mushroomBlock = mushroomBlock;
		this.shellRadius = shellRadius;
	}

	@Override
	public String getDescription(DynamicRegistryManager registryManager) {
		return "+++ MushroomSphere +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.getID(registryManager) +
				"\nRadius: " + this.radius +
				"\nShell: " + this.mushroomBlock.toString() + " (Radius: " + this.shellRadius + ")" +
				"\nCore: " + this.stemBlock.toString();
	}

	@Override
	public void generate(Chunk chunk, DynamicRegistryManager registryManager) {
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

	public static class Config extends ConfiguredSphere<Config.Config> {

		public static final MapCodec<MushroomSphere.Config> CODEC = createCodec(MushroomSphere.Config.Config.CODEC, MushroomSphere.Config::new);
		private final BlockState stemBlock;
		private final BlockState mushroomBlock;
		private final int minShellRadius;
		private final int maxShellRadius;

		public Config(SharedConfig shared, Config config) {
			super(shared);
			this.stemBlock = config.stemBlock;
			this.mushroomBlock = config.mushroomBlock;
			this.minShellRadius = config.minShellRadius;
			this.maxShellRadius = config.maxShellRadius;
		}

		@Override
		public Sphere<MushroomSphere.Config> getType() {
			return Sphere.MUSHROOM;
		}

		@Override
		public Config config() {
			return new Config(stemBlock, mushroomBlock, minShellRadius, maxShellRadius);
		}

		@Override
		public MushroomSphere generate(ChunkRandom random, DynamicRegistryManager registryManager) {
			return new MushroomSphere(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, stemBlock, mushroomBlock, randomBetween(random, minShellRadius, maxShellRadius));
		}

		public record Config(BlockState stemBlock, BlockState mushroomBlock, int minShellRadius, int maxShellRadius) {
			public static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							BLOCKSTATE_STRING_CODEC.fieldOf("stem_block").forGetter(MushroomSphere.Config.Config::stemBlock),
							BLOCKSTATE_STRING_CODEC.fieldOf("mushroom_block").forGetter(MushroomSphere.Config.Config::mushroomBlock),
							Codec.INT.fieldOf("min_shell_size").forGetter(MushroomSphere.Config.Config::minShellRadius),
							Codec.INT.fieldOf("max_shell_size").forGetter(MushroomSphere.Config.Config::maxShellRadius)
					).apply(instance, MushroomSphere.Config.Config::new)
			);
		}

	}

}
	
