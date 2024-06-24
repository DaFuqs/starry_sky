package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class SingleBlockDecorator extends SpheroidFeature<SingleBlockDecoratorConfig> {
	
	public SingleBlockDecorator(Codec<SingleBlockDecoratorConfig> codec) {
		super(codec);
	}
	
	@Override
	public boolean generate(SpheroidFeatureContext<SingleBlockDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		Spheroid spheroid = context.getSpheroid();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		SingleBlockDecoratorConfig config = context.getConfig();
		
		for (BlockPos bp : getTopBlocks(world, origin, spheroid)) {
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
