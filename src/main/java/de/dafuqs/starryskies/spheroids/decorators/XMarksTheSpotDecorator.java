package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.loot.*;
import net.minecraft.registry.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

import static de.dafuqs.starryskies.Support.BLOCKSTATE_STRING_CODEC;

/**
 * Creates a small X on one side of the spheroid
 * Puts a loot chest in the absolute center (could be fun on lava spheroids!)
 */
public class XMarksTheSpotDecorator extends SpheroidDecorator {

	public static final MapCodec<XMarksTheSpotDecorator> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).fieldOf("loot_table").forGetter(decorator -> decorator.lootTable),
					BLOCKSTATE_STRING_CODEC.fieldOf("marking_block").forGetter(decorator -> decorator.markingBlock)
			).apply(instance, XMarksTheSpotDecorator::new)
	);
	
	private final RegistryKey<LootTable> lootTable;
	private final BlockState markingBlock;
	private final boolean[] theX = {
			true, false, false, false, true,
			false, true, false, true, false,
			false, false, true, false, false,
			false, true, false, true, false,
			true, false, false, false, true
	};

	public XMarksTheSpotDecorator(RegistryKey<LootTable> lootTable, BlockState markingBlock) {
		this.lootTable = lootTable;
		this.markingBlock = markingBlock;
	}

	@Override
	protected SpheroidDecoratorType<XMarksTheSpotDecorator> getType() {
		return SpheroidDecoratorType.X_SPOT;
	}

	@Override
	public void decorate(StructureWorldAccess world, ChunkPos origin, Spheroid spheroid, Random random) {
		if (!spheroid.isCenterInChunk(origin)) {
			return;
		}
		
		placeLootChest(world, spheroid.getPosition(), lootTable, random);
		
		// paint 1-3 "X"es on the sphere in random directions
		int r = random.nextInt(6);
		int amountOfXMarks = random.nextInt(2) + 1;
		for (int i = 0; i < amountOfXMarks; i++) {
			Direction randomDirection = Direction.values()[(r + i) % 6];
			paintXInDirection(world, spheroid, randomDirection);
		}
	}
	
	/**
	 * Draws an "X" in a 5x5 pattern on a sphere.
	 */
	private void paintXInDirection(StructureWorldAccess world, Spheroid spheroid, Direction direction) {
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
				if (theX[i * 5 + j]) {
					BlockPos startBlockPos;
					switch (direction) {
						case UP, DOWN -> startBlockPos = new BlockPos(startX + i, startY, startZ + j);
						case EAST, WEST -> startBlockPos = new BlockPos(startX, startY + i, startZ + j);
						default -> startBlockPos = new BlockPos(startX + i, startY + j, startZ);
					}
					BlockPos currentBlockPos = findNextNonAirBlockInDirection(world, startBlockPos, direction, spheroid.getRadius());
					if (currentBlockPos != null) {
						world.setBlockState(currentBlockPos, markingBlock, 3);
					}
				}
			}
		}
	}
	
}
