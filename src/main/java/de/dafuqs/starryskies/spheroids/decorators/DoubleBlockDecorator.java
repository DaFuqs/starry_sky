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

public class DoubleBlockDecorator extends SpheroidFeature<DoubleBlockDecoratorConfig> {
	
	public DoubleBlockDecorator(Codec<DoubleBlockDecoratorConfig> codec) {
		super(codec);
	}
	
	@Override
	public boolean generate(SpheroidFeatureContext<DoubleBlockDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		Spheroid spheroid = context.getSpheroid();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		DoubleBlockDecoratorConfig config = context.getConfig();
		
		for (BlockPos bp : getTopBlocks(world, origin, spheroid)) {
			if (!world.getBlockState(bp).isAir() && world.getBlockState(bp.up()).isAir() && world.getBlockState(bp.up(2)).isAir()) {
				if (random.nextFloat() < config.chance()) {
					world.setBlockState(bp.up(), config.state().with(TallFlowerBlock.HALF, DoubleBlockHalf.LOWER), 3);
					world.setBlockState(bp.up(2), config.state().with(TallFlowerBlock.HALF, DoubleBlockHalf.UPPER), 3);
				}
			}
		}
		
		return true;
	}
	
}
