package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class HangingBlockDecorator extends SpheroidFeature<HangingBlockDecoratorConfig> {
	
	public HangingBlockDecorator(Codec<HangingBlockDecoratorConfig> codec) {
		super(codec);
	}
	
	@Override
	public boolean generate(SpheroidFeatureContext<HangingBlockDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		Spheroid spheroid = context.getSpheroid();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		HangingBlockDecoratorConfig config = context.getConfig();
		
		int spheroidY = spheroid.getPosition().getY();
		for (BlockPos bp : getBottomBlocks(world, origin, spheroid)) {
			BlockPos flippedBlockPos = bp.down((bp.getY() - spheroidY) * 2);
			
			if (world.getBlockState(flippedBlockPos.down()).isAir()) {
				if (random.nextFloat() < config.chance()) {
					world.setBlockState(flippedBlockPos.down(), config.state(), 3);
				}
			}
		}
		
		return true;
	}
	
}
