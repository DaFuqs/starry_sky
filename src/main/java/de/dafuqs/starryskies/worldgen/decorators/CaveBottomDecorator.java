package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;


public class CaveBottomDecorator extends SphereDecorator<CaveBottomDecoratorConfig> {

	public CaveBottomDecorator(Codec<CaveBottomDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(SphereFeatureContext<CaveBottomDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		PlacedSphere sphere = context.getSpheroid();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		CaveBottomDecoratorConfig config = context.getConfig();

		for (BlockPos bp : getCaveBottomBlocks(world, origin, sphere)) {
			if (random.nextFloat() < config.chance() && config.state().canPlaceAt(world, bp.up())) {
				world.setBlockState(bp.up(), config.state(), 3);
			}
		}

		return true;
	}

}
