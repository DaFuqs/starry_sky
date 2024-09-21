package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

/**
 * Creates a small X on one side of the sphere
 * Puts a loot chest in the absolute center (could be fun on lava spheres!)
 */
public class XMarksTheSpotDecorator extends SphereDecorator<XMarksTheSpotDecoratorConfig> {

	private final static boolean[] THE_X = {
			true, false, false, false, true,
			false, true, false, true, false,
			false, false, true, false, false,
			false, true, false, true, false,
			true, false, false, false, true
	};

	public XMarksTheSpotDecorator(Codec<XMarksTheSpotDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(SphereFeatureContext<XMarksTheSpotDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		PlacedSphere sphere = context.getSpheroid();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		XMarksTheSpotDecoratorConfig config = context.getConfig();

		if (!sphere.isCenterInChunk(origin)) {
			return false;
		}

		placeLootChest(world, sphere.getPosition(), config.lootTable(), random);

		// paint 1-3 "X" on the sphere in random directions
		int r = random.nextInt(6);
		int amountOfXMarks = random.nextInt(2) + 1;
		for (int i = 0; i < amountOfXMarks; i++) {
			Direction randomDirection = Direction.values()[(r + i) % 6];
			paintXInDirection(world, sphere, config.markingState(), randomDirection);
		}

		return true;
	}

	/**
	 * Draws an "X" in a 5x5 pattern on a sphere.
	 */
	private void paintXInDirection(StructureWorldAccess world, PlacedSphere sphere, BlockState markingState, Direction direction) {
		int startX;
		int startY;
		int startZ;

		BlockPos spherePos = sphere.getPosition();
		int radius = sphere.getRadius();
		switch (direction) {
			case UP -> {
				startX = spherePos.getX() - 2;
				startY = spherePos.getY() - radius;
				startZ = spherePos.getZ() - 2;
			}
			case DOWN -> {
				startX = spherePos.getX() - 2;
				startY = spherePos.getY() + radius;
				startZ = spherePos.getZ() - 2;
			}
			case EAST -> {
				startX = spherePos.getX() - radius;
				startY = spherePos.getY() - 2;
				startZ = spherePos.getZ() - 2;
			}
			case WEST -> {
				startX = spherePos.getX() + radius;
				startY = spherePos.getY() - 2;
				startZ = spherePos.getZ() - 2;
			}
			case NORTH -> {
				startX = spherePos.getX() - 2;
				startY = spherePos.getY() - 2;
				startZ = spherePos.getZ() + sphere.getRadius();
			}
			default -> {
				startX = spherePos.getX() - 2;
				startY = spherePos.getY() - 2;
				startZ = spherePos.getZ() - sphere.getRadius();
			}
		}

		for (int i = -0; i < 5; i++) {
			for (int j = -0; j < 5; j++) {
				if (THE_X[i * 5 + j]) {
					BlockPos startBlockPos;
					switch (direction) {
						case UP, DOWN -> startBlockPos = new BlockPos(startX + i, startY, startZ + j);
						case EAST, WEST -> startBlockPos = new BlockPos(startX, startY + i, startZ + j);
						default -> startBlockPos = new BlockPos(startX + i, startY + j, startZ);
					}
					BlockPos currentBlockPos = findNextNonAirBlockInDirection(world, startBlockPos, direction, sphere.getRadius());
					if (currentBlockPos != null) {
						world.setBlockState(currentBlockPos, markingState, 3);
					}
				}
			}
		}
	}

}
