package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
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

public class RainbowSphere extends PlacedSphere {

	private final List<BlockState> rainbowBlocks;

	public RainbowSphere(ConfiguredSphere<?> template, float radius, List<ConfiguredSphereDecorator<?, ?>> decorators, List<Pair<EntityType<?>, Integer>> spawns, ChunkRandom random,
						 List<BlockState> rainbowBlocks) {

		super(template, radius, decorators, spawns, random);
		this.radius = radius;
		this.rainbowBlocks = rainbowBlocks;
	}

	@Override
	public String getDescription(DynamicRegistryManager registryManager) {
		return "+++ RainbowSphere +++" +
				"\nPosition: x=" + this.getPosition().getX() + " y=" + this.getPosition().getY() + " z=" + this.getPosition().getZ() +
				"\nTemplateID: " + this.getID(registryManager) +
				"\nRadius: " + this.radius +
				"\nRainbow Blocks ( + " + this.getRainbowBlockCount() + "): " + this.rainbowBlocks.toString();
	}

	public int getRainbowBlockCount() {
		return this.rainbowBlocks.size();
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

					int currentBlockID = (Math.abs(x2) + Math.abs(y2) + Math.abs(z2)) % this.getRainbowBlockCount();
					BlockState currentBlockState = this.rainbowBlocks.get(currentBlockID);
					chunk.setBlockState(currBlockPos, currentBlockState, false);
				}
			}
		}
	}

	public static class Template extends ConfiguredSphere<List<BlockState>> {

		public static final MapCodec<Template> CODEC = createCodec(BLOCKSTATE_STRING_CODEC.listOf().fieldOf("blocks"), Template::new);

		private final List<BlockState> rainbowBlocks = new ArrayList<>();

		public Template(SharedConfig shared, List<BlockState> blocks) {
			super(shared);
			this.rainbowBlocks.addAll(blocks);
		}

		@Override
		public Spheres<Template> getType() {
			return Spheres.RAINBOW;
		}

		@Override
		public List<BlockState> config() {
			return rainbowBlocks;
		}

		@Override
		public RainbowSphere generate(ChunkRandom random, DynamicRegistryManager registryManager) {
			return new RainbowSphere(this, randomBetween(random, minSize, maxSize), selectDecorators(random), selectSpawns(random), random, rainbowBlocks);
		}

	}

}