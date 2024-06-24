package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;

public class HugePlantDecorator extends SpheroidFeature<HugePlantDecoratorConfig> {
	
	public HugePlantDecorator(Codec<HugePlantDecoratorConfig> codec) {
		super(codec);
	}
	
	@Override
	public boolean generate(SpheroidFeatureContext<HugePlantDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		Spheroid spheroid = context.getSpheroid();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		HugePlantDecoratorConfig config = context.getConfig();
		
		for (BlockPos bp : getTopBlocks(world, origin, spheroid)) {
			BlockState posState = world.getBlockState(bp);
			if (!posState.isFullCube(world, bp)) {
				continue;
			}
			
			if (random.nextFloat() < config.chance) {
				int thisHeight = Support.getRandomBetween(random, config.minHeight, config.maxHeight);
				for (int i = 1; i < thisHeight + 1; i++) {
					if (world.getBlockState(bp.up(i)).isAir()) {
						
						BlockState placementBlockState = config.block;
						if (i == 1 && config.firstBlock != null) {
							placementBlockState = config.firstBlock;
						} else if (i == thisHeight && config.lastBlock != null) {
							placementBlockState = config.lastBlock;
						}
						
						world.setBlockState(bp.up(), placementBlockState, 3);
					} else {
						break;
					}
				}
			}
		}
		
		return true;
	}
	
}
