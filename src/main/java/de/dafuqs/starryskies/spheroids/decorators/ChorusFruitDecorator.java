package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class ChorusFruitDecorator extends SpheroidFeature<ChorusFruitDecoratorConfig> {
	
	public ChorusFruitDecorator(Codec<ChorusFruitDecoratorConfig> codec) {
		super(codec);
	}
	
	@Override
	public boolean generate(SpheroidFeatureContext<ChorusFruitDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		Spheroid spheroid = context.getSpheroid();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		ChorusFruitDecoratorConfig config = context.getConfig();
		
		boolean success = false;
		for (BlockPos bp : getTopBlocks(world, origin, spheroid)) {
			if (random.nextFloat() < config.chorusChance) {
				ChorusFlowerBlock.generate(world, bp.up(), random, 8);
				success = true;
			}
		}
		
		return success;
	}
	
}
