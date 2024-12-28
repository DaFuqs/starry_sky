package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class HugeHangingPlantDecorator extends SphereDecorator<HugePlantDecoratorConfig> {

	public HugeHangingPlantDecorator(Codec<HugePlantDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(SphereFeatureContext<HugePlantDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		PlacedSphere<?> sphere = context.getSphere();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		HugePlantDecoratorConfig config = context.getConfig();

		for (BlockPos bp : getBottomBlocks(world, origin, sphere)) {
			if (random.nextFloat() < config.chance) {
				int thisHeight = Support.getRandomBetween(random, config.minHeight, config.maxHeight);
				for (int i = 1; i < thisHeight + 1; i++) {
					if (world.getBlockState(bp.down(i)).isAir()) {

						BlockState placementBlockState = config.block;
						if (i == 1 && config.firstBlock != null) {
							placementBlockState = config.firstBlock;
						} else if (i == thisHeight && config.lastBlock != null) {
							placementBlockState = config.lastBlock;
						}

						world.setBlockState(bp.down(i), placementBlockState, 3);
					} else {
						if (i > 1 && config.lastBlock != null) {
							world.setBlockState(bp.down(i - 1), config.lastBlock, 3);
						}
						break;
					}
				}
			}
		}

		return true;
	}

}
