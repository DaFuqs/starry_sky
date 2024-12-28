package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class HangingBlockDecorator extends SphereDecorator<HangingBlockDecoratorConfig> {

	public HangingBlockDecorator(Codec<HangingBlockDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(SphereFeatureContext<HangingBlockDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		PlacedSphere<?> sphere = context.getSphere();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		HangingBlockDecoratorConfig config = context.getConfig();

		int sphereY = sphere.getPosition().getY();
		for (BlockPos bp : getBottomBlocks(world, origin, sphere)) {
			BlockPos flippedBlockPos = bp.down((bp.getY() - sphereY) * 2);

			if (world.getBlockState(flippedBlockPos.down()).isAir()) {
				if (random.nextFloat() < config.chance()) {
					world.setBlockState(flippedBlockPos.down(), config.state(), 3);
				}
			}
		}

		return true;
	}

}
