package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.gen.feature.*;

import java.util.*;

public class MultifaceGrowthDecorator extends SpheroidFeature<MultifaceGrowthDecoratorConfig> {
	
	public MultifaceGrowthDecorator(Codec<MultifaceGrowthDecoratorConfig> codec) {
		super(codec);
	}
	
	@Override
	public boolean generate(SpheroidFeatureContext<MultifaceGrowthDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		Spheroid spheroid = context.getSpheroid();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		MultifaceGrowthDecoratorConfig config = context.getConfig();
		
		int spheroidY = spheroid.getPosition().getY();
		
		for (BlockPos bp : getCaveBottomBlocks(world, origin, spheroid)) {
			if (random.nextFloat() < config.chance) {
				BlockPos currentPos = new BlockPos(bp.getX(), spheroidY, bp.getZ());
				for (int i = 0; i < spheroid.getRadius(); i++) {
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
