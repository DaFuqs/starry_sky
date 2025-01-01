package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.block.enums.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;


public class SeaGreensDecorator extends SphereDecorator<SphereDecoratorConfig.DefaultSphereDecoratorConfig> {

	// those are all always waterlogged
	private static final BlockState KELP = Blocks.KELP.getDefaultState(); // the top
	private static final BlockState KELP_PLANT = Blocks.KELP_PLANT.getDefaultState(); // the middle
	private static final BlockState SEAGRASS = Blocks.SEAGRASS.getDefaultState();
	private static final BlockState TALL_SEAGRASS_UPPER = Blocks.TALL_SEAGRASS.getDefaultState().with(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);
	private static final BlockState TALL_SEAGRASS_LOWER = Blocks.TALL_SEAGRASS.getDefaultState().with(TallSeagrassBlock.HALF, DoubleBlockHalf.LOWER);

	public SeaGreensDecorator(Codec<SphereDecoratorConfig.DefaultSphereDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(SphereFeatureContext<SphereDecoratorConfig.DefaultSphereDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		PlacedSphere<?> sphere = context.getSphere();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();

		for (BlockPos bp : getCaveBottomBlocks(world, origin, sphere)) {
			int r = random.nextInt(4);
			
			if (world.getBlockState(bp).getBlock() != Blocks.WATER) {
				continue;
			}
			
			if (r == 0) {
				int kelpHeight = random.nextInt(8);
				for (int i = 0; i < kelpHeight; i++) {
					if (world.getBlockState(bp.up(i + 1)).getBlock() == Blocks.WATER) {
						if (world.getBlockState(bp.up(i + 2)).getBlock() == Blocks.WATER && i < kelpHeight - 1) {
							world.setBlockState(bp.up(i + 1), KELP_PLANT, 3); // middle parts
						} else {
							world.setBlockState(bp.up(i + 1), KELP, 3); // the top
						}
					}
				}
			} else if (r == 1) {
				world.setBlockState(bp.up(), SEAGRASS, 3);
			} else if (r == 2) {
				if (world.getBlockState(bp.up(2)).getBlock() == Blocks.WATER) {
					world.setBlockState(bp.up(2), TALL_SEAGRASS_UPPER, 3);
					world.setBlockState(bp.up(), TALL_SEAGRASS_LOWER, 3);
				}
			}
		}

		return true;
	}

}
