package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

/**
 * Creates a small X on one side of the spheroid
 * Puts a loot chest in the absolute center (could be fun on lava spheroids!)
 */
public class XMarksTheSpotDecorator extends SpheroidFeature<XMarksTheSpotDecoratorConfig> {
	
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
	public boolean generate(SpheroidFeatureContext<XMarksTheSpotDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		Spheroid spheroid = context.getSpheroid();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		XMarksTheSpotDecoratorConfig config = context.getConfig();
		
		if (!spheroid.isCenterInChunk(origin)) {
			return false;
		}
		
		placeLootChest(world, spheroid.getPosition(), config.lootTable(), random);
		
		// paint 1-3 "X" on the sphere in random directions
		int r = random.nextInt(6);
		int amountOfXMarks = random.nextInt(2) + 1;
		for (int i = 0; i < amountOfXMarks; i++) {
			Direction randomDirection = Direction.values()[(r + i) % 6];
			paintXInDirection(world, spheroid, config.markingState(), randomDirection);
		}
		
		return true;
	}
	
	/**
	 * Draws an "X" in a 5x5 pattern on a sphere.
	 */
	private void paintXInDirection(StructureWorldAccess world, Spheroid spheroid, BlockState markingState, Direction direction) {
		int startX;
		int startY;
		int startZ;
		
		BlockPos spheroidPos = spheroid.getPosition();
		int radius = spheroid.getRadius();
		switch (direction) {
			case UP -> {
				startX = spheroidPos.getX() - 2;
				startY = spheroidPos.getY() - radius;
				startZ = spheroidPos.getZ() - 2;
			}
			case DOWN -> {
				startX = spheroidPos.getX() - 2;
				startY = spheroidPos.getY() + radius;
				startZ = spheroidPos.getZ() - 2;
			}
			case EAST -> {
				startX = spheroidPos.getX() - radius;
				startY = spheroidPos.getY() - 2;
				startZ = spheroidPos.getZ() - 2;
			}
			case WEST -> {
				startX = spheroidPos.getX() + radius;
				startY = spheroidPos.getY() - 2;
				startZ = spheroidPos.getZ() - 2;
			}
			case NORTH -> {
				startX = spheroidPos.getX() - 2;
				startY = spheroidPos.getY() - 2;
				startZ = spheroidPos.getZ() + spheroid.getRadius();
			}
			default -> {
				startX = spheroidPos.getX() - 2;
				startY = spheroidPos.getY() - 2;
				startZ = spheroidPos.getZ() - spheroid.getRadius();
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
					BlockPos currentBlockPos = findNextNonAirBlockInDirection(world, startBlockPos, direction, spheroid.getRadius());
					if (currentBlockPos != null) {
						world.setBlockState(currentBlockPos, markingState, 3);
					}
				}
			}
		}
	}
	
}
