package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.block.enums.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class BambooDecorator extends SpheroidFeature<BambooDecoratorConfig> {
	
	public BambooDecorator(Codec<BambooDecoratorConfig> codec) {
		super(codec);
	}
	
	@Override
	public boolean generate(SpheroidFeatureContext<BambooDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		Spheroid spheroid = context.getSpheroid();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		BambooDecoratorConfig config = context.getConfig();
		
		for (BlockPos bp : getTopBlocks(world, origin, spheroid)) {
			if (random.nextFloat() < config.chance()) {
				if (random.nextFloat() < config.saplingChance()) {
					if (config.bambooBlockState().canPlaceAt(world, bp.up())) {
						world.setBlockState(bp.up(), config.bambooBlockState(), 3);
					}
				} else {
					int height = random.nextInt(8);
					for (int i = 1; i < height; i++) {
						if (config.bambooBlockState().canPlaceAt(world, bp.up(i))) {
							if (i == 3 && height < 5) {
								world.setBlockState(bp.up(i), config.bambooBlockState().with(BambooBlock.LEAVES, BambooLeaves.NONE), 3);
							} else if (i > 4) {
								world.setBlockState(bp.up(i), config.bambooBlockState().with(BambooBlock.LEAVES, BambooLeaves.LARGE), 3);
							} else if (i > 2) {
								world.setBlockState(bp.up(i), config.bambooBlockState().with(BambooBlock.LEAVES, BambooLeaves.SMALL), 3);
							} else {
								world.setBlockState(bp.up(i), config.bambooBlockState().with(BambooBlock.LEAVES, BambooLeaves.NONE), 3);
							}
						}
					}
				}
			}
		}
		
		return true;
	}
	
}
