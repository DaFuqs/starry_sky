package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class SingleBlockDecorator extends SphereDecorator<SingleBlockDecoratorConfig> {

	public SingleBlockDecorator(Codec<SingleBlockDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(SphereFeatureContext<SingleBlockDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		PlacedSphere sphere = context.getSpheroid();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		SingleBlockDecoratorConfig config = context.getConfig();

		for (BlockPos bp : getTopBlocks(world, origin, sphere)) {
			BlockState posState = world.getBlockState(bp);
			if (posState.isFullCube(world, bp) && world.getBlockState(bp.up()).isAir()) {
				if (random.nextFloat() < config.chance()) {
					world.setBlockState(bp.up(), config.state(), Block.NOTIFY_ALL);
				}
			}
		}

		return true;
	}

}
