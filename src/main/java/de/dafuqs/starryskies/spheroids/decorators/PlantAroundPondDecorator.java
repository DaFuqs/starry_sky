package de.dafuqs.starryskies.spheroids.decorators;
import com.mojang.serialization.MapCodec;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;

import java.util.*;


public class PlantAroundPondDecorator extends SpheroidDecorator {

	public static final MapCodec<PlantAroundPondDecorator> CODEC = MapCodec.unit(PlantAroundPondDecorator::new);
	
	private final BlockState block = Blocks.SUGAR_CANE.getDefaultState();
	private static final int pond_tries = 3;
	private static final float plant_chance = 0.5F;
	private static final int minHeight = 1;
	private static final int maxHeight = 3;

	// note: block value not changed???? (same behavior as pre-port)
	public PlantAroundPondDecorator() {
		super();
	}

	@Override
	protected SpheroidDecoratorType<PlantAroundPondDecorator> getType() {
		return SpheroidDecoratorType.PLANT_AROUND_POND;
	}

	@Override
	public void decorate(StructureWorldAccess world, ChunkPos origin, Spheroid spheroid, Random random) {
		for (BlockPos pos : getTopBlocks(world, origin, spheroid, random, pond_tries)) {
			boolean canGenerate;
			// check if all 4 sides of the future water pond are solid
			canGenerate = true;
			Iterator<Direction> direction = Direction.Type.HORIZONTAL.iterator();
			while (direction.hasNext() && canGenerate) {
				BlockPos currentCheckBlockPos = pos.offset(direction.next());
				
				if (!world.getBlockState(currentCheckBlockPos).isSolidBlock(world, currentCheckBlockPos)
						|| !world.getBlockState(currentCheckBlockPos.up()).isAir()) {
					canGenerate = false;
				}
			}
			
			if (canGenerate) {
				world.setBlockState(pos, Blocks.WATER.getDefaultState(), 3);
				
				// place sugar cane with chance
				direction = Direction.Type.HORIZONTAL.iterator();
				while (direction.hasNext()) {
					Direction currentDirection = direction.next();
					if (random.nextFloat() < plant_chance) {
						BlockPos sugarCaneBlockPos = pos.up().offset(currentDirection);
						int sugarCaneHeight = Support.getRandomBetween(random, minHeight, maxHeight);
						for (int i = 0; i <= sugarCaneHeight; i++) {
							if (block.canPlaceAt(world, sugarCaneBlockPos.up(i))) {
								world.setBlockState(sugarCaneBlockPos.up(i), block, 3);
							}
						}
					}
				}
			}
		}
	}
}
