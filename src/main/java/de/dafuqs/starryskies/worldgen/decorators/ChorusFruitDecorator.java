package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class ChorusFruitDecorator extends SphereDecorator<ChorusFruitDecoratorConfig> {

	public ChorusFruitDecorator(Codec<ChorusFruitDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(SphereFeatureContext<ChorusFruitDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		PlacedSphere<?> sphere = context.getSphere();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		ChorusFruitDecoratorConfig config = context.getConfig();

		boolean success = false;
		for (BlockPos bp : getTopBlocks(world, origin, sphere)) {
			if (random.nextFloat() < config.chorusChance) {
				ChorusFlowerBlock.generate(world, bp.up(), random, 8);
				success = true;
			}
		}

		return success;
	}

}
