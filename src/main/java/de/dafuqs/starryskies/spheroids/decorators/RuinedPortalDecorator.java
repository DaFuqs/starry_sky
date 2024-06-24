package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;


public class RuinedPortalDecorator extends SpheroidFeature<RuinedPortalDecoratorConfig> {
	
	private static final BlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
	private static final BlockState MAGMA_BLOCK = Blocks.MAGMA_BLOCK.getDefaultState();
	private static final BlockState LAVA = Blocks.LAVA.getDefaultState();
	private static final BlockState OBSIDIAN = Blocks.OBSIDIAN.getDefaultState();
	private static final float OBSIDIAN_CHANCE = 0.9F;
	
	public RuinedPortalDecorator(Codec<RuinedPortalDecoratorConfig> codec) {
		super(codec);
	}
	
	@Override
	public boolean generate(SpheroidFeatureContext<RuinedPortalDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		Spheroid spheroid = context.getSpheroid();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		RuinedPortalDecoratorConfig config = context.getConfig();
		
		if (!spheroid.isCenterInChunk(origin)) {
			return false;
		}
		BlockPos spheroidPosition = spheroid.getPosition();
		
		// place the floor
		for (int x = -spheroid.getRadius(); x <= spheroid.getRadius(); x++) {
			for (int z = -spheroid.getRadius(); z <= spheroid.getRadius(); z++) {
				
				int startY = spheroidPosition.getY() + spheroid.getRadius() + 1;
				int upperY = Support.getLowerGroundBlock(world, new BlockPos(spheroidPosition.getX() + x, startY, spheroidPosition.getZ() + z), spheroidPosition.getY());
				
				if (upperY > spheroidPosition.getY()) {
					int randomI = random.nextInt(spheroid.getRadius() + 1);
					if (Math.abs(x * z) * 1.5 < randomI * randomI) {
						BlockPos currentBlockPos = new BlockPos(spheroidPosition.getX() + x, upperY, spheroidPosition.getZ() + z);
						switch (random.nextInt(6)) {
							case 0 -> world.setBlockState(currentBlockPos, MAGMA_BLOCK, 3);
							case 1 -> {
								world.setBlockState(currentBlockPos, LAVA, 3);
								world.getChunk(currentBlockPos).markBlockForPostProcessing(currentBlockPos);
							}
							default -> world.setBlockState(currentBlockPos, NETHERRACK, 3);
						}
					}
				}
			}
		}
		
		// place portal
		int centerTopBlockY = Support.getLowerGroundBlock(world, new BlockPos(spheroidPosition.getX(), spheroidPosition.getY() + spheroid.getRadius() + 1, spheroidPosition.getZ()), spheroidPosition.getY());
		BlockPos currentBlockPos = new BlockPos(spheroidPosition.getX(), centerTopBlockY, spheroidPosition.getZ());
		
		placePortalBlock(world, currentBlockPos, random);
		placePortalBlock(world, currentBlockPos.offset(Direction.SOUTH, 1), random);
		placePortalBlock(world, currentBlockPos.offset(Direction.NORTH, 1), random);
		placePortalBlock(world, currentBlockPos.offset(Direction.SOUTH, 2), random);
		placePortalBlock(world, currentBlockPos.offset(Direction.NORTH, 2), random);
		
		placePortalBlock(world, currentBlockPos.offset(Direction.SOUTH, 2).up(), random);
		placePortalBlock(world, currentBlockPos.offset(Direction.NORTH, 2).up(), random);
		placePortalBlock(world, currentBlockPos.offset(Direction.SOUTH, 2).up(1), random);
		placePortalBlock(world, currentBlockPos.offset(Direction.NORTH, 2).up(1), random);
		placePortalBlock(world, currentBlockPos.offset(Direction.SOUTH, 2).up(2), random);
		placePortalBlock(world, currentBlockPos.offset(Direction.NORTH, 2).up(2), random);
		placePortalBlock(world, currentBlockPos.offset(Direction.SOUTH, 2).up(3), random);
		placePortalBlock(world, currentBlockPos.offset(Direction.NORTH, 2).up(3), random);
		placePortalBlock(world, currentBlockPos.offset(Direction.SOUTH, 2).up(4), random);
		placePortalBlock(world, currentBlockPos.offset(Direction.NORTH, 2).up(4), random);
		
		placePortalBlock(world, currentBlockPos.up(5), random);
		placePortalBlock(world, currentBlockPos.offset(Direction.SOUTH, 1).up(5), random);
		placePortalBlock(world, currentBlockPos.offset(Direction.NORTH, 1).up(5), random);
		placePortalBlock(world, currentBlockPos.offset(Direction.SOUTH, 2).up(5), random);
		placePortalBlock(world, currentBlockPos.offset(Direction.NORTH, 2).up(5), random);
		
		// place loot chest
		int randomX = Support.getRandomBetween(random, spheroidPosition.getX() - spheroid.getRadius() / 2, spheroidPosition.getX() + spheroid.getRadius() / 2);
		int randomZ = Support.getRandomBetween(random, spheroidPosition.getZ() - spheroid.getRadius() / 2, spheroidPosition.getZ() + spheroid.getRadius() / 2);
		centerTopBlockY = Support.getLowerGroundBlock(world, new BlockPos(randomX, spheroidPosition.getY() + spheroid.getRadius() + 2, randomZ), spheroidPosition.getY());
		
		if (centerTopBlockY != spheroidPosition.getY()) {
			BlockPos lootChestPosition = new BlockPos(randomX, centerTopBlockY, randomZ).up();
			placeLootChest(world, lootChestPosition, config.lootTable(), random);
		}
		
		return true;
	}
	
	private void placePortalBlock(StructureWorldAccess world, BlockPos blockPos, Random random) {
		if (random.nextFloat() < OBSIDIAN_CHANCE) {
			world.setBlockState(blockPos, OBSIDIAN, 3);
		}
	}
	
}