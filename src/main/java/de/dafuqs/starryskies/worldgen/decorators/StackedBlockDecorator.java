package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class StackedBlockDecorator extends SphereDecorator<StackedBlockDecoratorConfig> {

	public StackedBlockDecorator(Codec<StackedBlockDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(SphereFeatureContext<StackedBlockDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		PlacedSphere sphere = context.getSpheroid();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		StackedBlockDecoratorConfig config = context.getConfig();

		for (BlockPos bp : getTopBlocks(world, origin, sphere)) {
			if (random.nextFloat() < config.chance()) {
				int height = Support.getRandomBetween(random, config.minHeight(), config.maxHeight());
				for (int i = 0; i < height; i++) {
					if (config.block().canPlaceAt(world, bp.up(i + 1))) {
						world.setBlockState(bp.up(i + 1), config.block(), Block.NOTIFY_ALL);
					}
				}
			}
		}

		return true;
	}

}