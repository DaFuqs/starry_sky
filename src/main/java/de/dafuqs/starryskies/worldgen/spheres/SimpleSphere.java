package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.chunk.*;

import java.util.*;

import static de.dafuqs.starryskies.Support.*;

public class SimpleSphere extends Sphere<SimpleSphere.Config> {

	public SimpleSphere(Codec<Config> codec) {
		super(codec);
	}

	@Override
	public PlacedSphere generate(ConfiguredSphere<? extends Sphere<SimpleSphere.Config>, SimpleSphere.Config> configuredSphere, SimpleSphere.Config config, ChunkRandom random, DynamicRegistryManager registryManager) {
		return new Placed(configuredSphere,
				config.getSize(random), config.getDecorators(random), config.getSpawns(random),
				random, config.state);
	}

	public static class Placed extends PlacedSphere<SimpleSphere.Config> {

		private final BlockState blockState;

		public Placed(ConfiguredSphere<? extends Sphere<SimpleSphere.Config>, SimpleSphere.Config> configuredSphere, float radius, List<ConfiguredSphereDecorator<?, ?>> decorators,
					  List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random, BlockState blockState) {
			super(configuredSphere, radius, decorators, spawns, random);
			this.blockState = blockState;
		}

		@Override
		public String getDescription(DynamicRegistryManager registryManager) {
			return "+++ SimpleSphere +++" +
					"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
					"\nTemplateID: " + this.getID(registryManager) +
					"\nRadius: " + this.radius +
					"\nBlock: " + this.blockState.toString();
		}

		@Override
		public void generate(Chunk chunk, DynamicRegistryManager registryManager) {
			int chunkX = chunk.getPos().x;
			int chunkZ = chunk.getPos().z;

			random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
			int x = this.getPosition().getX();
			int y = this.getPosition().getY();
			int z = this.getPosition().getZ();

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

						chunk.setBlockState(currBlockPos, this.blockState, false);
					}
				}
			}
		}

	}

	public static class Config implements SphereConfig {

		public static final Codec<SimpleSphere.Config> CODEC = RecordCodecBuilder.create((instance) -> {
			return instance.group(SphereConfig.SharedConfig.CODEC.fieldOf("config").forGetter((config) -> {
				return config.sharedConfig;
			}), BLOCKSTATE_STRING_CODEC.fieldOf("block").forGetter((config) -> {
				return config.state;
			})).apply(instance, SimpleSphere.Config::new);
		});

		protected final SharedConfig sharedConfig;
		protected final BlockState state;

		public Config(SharedConfig sharedConfig, BlockState state) {
			this.sharedConfig = sharedConfig;
			this.state = state;
		}

		public BlockState state() {
			return this.state;
		}

		@Override
		public SharedConfig config() {
			return sharedConfig;
		}
	}

}
