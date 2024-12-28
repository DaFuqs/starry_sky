package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;

import java.util.*;


public class PlantAroundPondDecorator extends SphereDecorator<PlantAroundPondDecoratorConfig> {

	public PlantAroundPondDecorator(Codec<PlantAroundPondDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(SphereFeatureContext<PlantAroundPondDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		PlacedSphere<?> sphere = context.getSphere();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		PlantAroundPondDecoratorConfig config = context.getConfig();
		
		for (BlockPos pos : getTopBlocks(world, origin, sphere, random, PlantAroundPondDecoratorConfig.pond_tries)) {
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
					if (random.nextFloat() < config.plant_chance) {
						BlockPos sugarCaneBlockPos = pos.up().offset(currentDirection);
						int sugarCaneHeight = Support.getRandomBetween(random, config.minHeight, config.maxHeight);
						for (int i = 0; i <= sugarCaneHeight; i++) {
							if (config.block.canPlaceAt(world, sugarCaneBlockPos.up(i))) {
								world.setBlockState(sugarCaneBlockPos.up(i), config.block, 3);
							}
						}
					}
				}
			}
		}

		return true;
	}

}



