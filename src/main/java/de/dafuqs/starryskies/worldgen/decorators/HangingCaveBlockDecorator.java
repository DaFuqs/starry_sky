package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class HangingCaveBlockDecorator extends SphereDecorator<HangingCaveBlockDecoratorConfig> {

	public HangingCaveBlockDecorator(Codec<HangingCaveBlockDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(SphereFeatureContext<HangingCaveBlockDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		PlacedSphere<?> sphere = context.getSphere();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		HangingCaveBlockDecoratorConfig config = context.getConfig();

		// TODO: is that correct?
		for (BlockPos bp : getBottomBlocks(world, origin, sphere)) {
			if (!world.getBlockState(bp).isAir() && random.nextFloat() < config.chance()) {
				if (world.getBlockState(bp.down()).isAir()) {
					world.setBlockState(bp.down(), config.block(), 3);
				}
				return true;
			}
		}

		return false;
	}

}
