package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;

import java.util.*;

public class DripleafDecorator extends SphereDecorator<DripleafDecoratorConfig> {

	private static final BlockState DRIPLEAF_BLOCK_STATE = Blocks.BIG_DRIPLEAF.getDefaultState();
	private static final BlockState DRIPLEAF_STEM_BLOCK_STATE = Blocks.BIG_DRIPLEAF_STEM.getDefaultState();
	private static final BlockState WATER_BLOCK_STATE = Blocks.WATER.getDefaultState();
	private static final BlockState CLAY_BLOCK_STATE = Blocks.CLAY.getDefaultState();

	public DripleafDecorator(Codec<DripleafDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(SphereFeatureContext<DripleafDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		PlacedSphere sphere = context.getSpheroid();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		DripleafDecoratorConfig config = context.getConfig();

		for (BlockPos bp : getCaveBottomBlocks(world, origin, sphere, random, config.tries())) {
			boolean canGenerate;

			// check if all 4 sides of the future water pond are solid
			canGenerate = true;
			Iterator<Direction> direction = Direction.Type.HORIZONTAL.iterator();
			while (direction.hasNext() && canGenerate) {
				BlockPos currentCheckBlockPos = bp.offset(direction.next());

				if (!world.getBlockState(currentCheckBlockPos).isSolidBlock(world, currentCheckBlockPos) || !world.getBlockState(currentCheckBlockPos.up()).isAir()) {
					canGenerate = false;
				}
			}

			if (canGenerate) {
				// clay
				world.setBlockState(bp, CLAY_BLOCK_STATE, 3);

				// the dripleaf
				Direction randomDirection = Direction.Type.HORIZONTAL.random(random);
				int dripLeafHeight = random.nextInt(3) + 1;
				for (int i = 0; i <= dripLeafHeight; i++) {
					BlockState dripleafState = DRIPLEAF_BLOCK_STATE.with(HorizontalFacingBlock.FACING, randomDirection);
					if (dripleafState.canPlaceAt(world, bp.up(i))) {
						if (i == dripLeafHeight) {
							world.setBlockState(bp.up(i), DRIPLEAF_BLOCK_STATE.with(HorizontalFacingBlock.FACING, randomDirection), 3);
						} else {
							world.setBlockState(bp.up(i), DRIPLEAF_STEM_BLOCK_STATE.with(HorizontalFacingBlock.FACING, randomDirection), 3);
						}

					}
				}

				// surrounding water
				direction = Direction.Type.HORIZONTAL.iterator();
				while (direction.hasNext()) {
					Direction currentDirection = direction.next();
					BlockPos offsetPos = bp.offset(currentDirection);
					if (world.getBlockState(offsetPos.up()).isAir()) {
						world.setBlockState(offsetPos, WATER_BLOCK_STATE, 3);
					}
				}
			}
		}

		return true;
	}

}
