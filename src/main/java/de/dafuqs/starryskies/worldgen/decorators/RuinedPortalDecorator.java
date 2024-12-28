package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;


public class RuinedPortalDecorator extends SphereDecorator<RuinedPortalDecoratorConfig> {

	private static final BlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
	private static final BlockState MAGMA_BLOCK = Blocks.MAGMA_BLOCK.getDefaultState();
	private static final BlockState LAVA = Blocks.LAVA.getDefaultState();
	private static final BlockState OBSIDIAN = Blocks.OBSIDIAN.getDefaultState();
	private static final float OBSIDIAN_CHANCE = 0.9F;

	public RuinedPortalDecorator(Codec<RuinedPortalDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(SphereFeatureContext<RuinedPortalDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		PlacedSphere<?> sphere = context.getSphere();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		RuinedPortalDecoratorConfig config = context.getConfig();

		if (!sphere.isCenterInChunk(origin)) {
			return false;
		}
		BlockPos spherePosition = sphere.getPosition();

		// place the floor
		for (int x = -sphere.getRadius(); x <= sphere.getRadius(); x++) {
			for (int z = -sphere.getRadius(); z <= sphere.getRadius(); z++) {

				int startY = spherePosition.getY() + sphere.getRadius() + 1;
				int upperY = Support.getLowerGroundBlock(world, new BlockPos(spherePosition.getX() + x, startY, spherePosition.getZ() + z), spherePosition.getY());

				if (upperY > spherePosition.getY()) {
					int randomI = random.nextInt(sphere.getRadius() + 1);
					if (Math.abs(x * z) * 1.5 < randomI * randomI) {
						BlockPos currentBlockPos = new BlockPos(spherePosition.getX() + x, upperY, spherePosition.getZ() + z);
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
		int centerTopBlockY = Support.getLowerGroundBlock(world, new BlockPos(spherePosition.getX(), spherePosition.getY() + sphere.getRadius() + 1, spherePosition.getZ()), spherePosition.getY());
		BlockPos currentBlockPos = new BlockPos(spherePosition.getX(), centerTopBlockY, spherePosition.getZ());

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
		int randomX = Support.getRandomBetween(random, spherePosition.getX() - sphere.getRadius() / 2, spherePosition.getX() + sphere.getRadius() / 2);
		int randomZ = Support.getRandomBetween(random, spherePosition.getZ() - sphere.getRadius() / 2, spherePosition.getZ() + sphere.getRadius() / 2);
		centerTopBlockY = Support.getLowerGroundBlock(world, new BlockPos(randomX, spherePosition.getY() + sphere.getRadius() + 2, randomZ), spherePosition.getY());

		if (centerTopBlockY != spherePosition.getY()) {
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