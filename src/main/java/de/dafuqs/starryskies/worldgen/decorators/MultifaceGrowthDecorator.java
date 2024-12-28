package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.gen.feature.*;

import java.util.*;

public class MultifaceGrowthDecorator extends SphereDecorator<MultifaceGrowthDecoratorConfig> {

	public MultifaceGrowthDecorator(Codec<MultifaceGrowthDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(SphereFeatureContext<MultifaceGrowthDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		PlacedSphere<?> sphere = context.getSphere();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		MultifaceGrowthDecoratorConfig config = context.getConfig();

		int sphereY = sphere.getPosition().getY();

		for (BlockPos bp : getCaveBottomBlocks(world, origin, sphere)) {
			if (random.nextFloat() < config.chance) {
				BlockPos currentPos = new BlockPos(bp.getX(), sphereY, bp.getZ());
				for (int i = 0; i < sphere.getRadius(); i++) {
					if (!world.getBlockState(currentPos.up(i)).isAir()) {
						if (world.getBlockState(currentPos.up(i - 1)).isAir()) {
							MultifaceGrowthFeature.generate(world, currentPos, world.getBlockState(bp), config.featureConfig, random, Arrays.asList(Direction.values()));
						}
						break;
					}
				}
			}
		}

		return true;
	}

}
