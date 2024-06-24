package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class HangingCaveBlockDecorator extends SpheroidFeature<HangingCaveBlockDecoratorConfig> {
	
	public HangingCaveBlockDecorator(Codec<HangingCaveBlockDecoratorConfig> codec) {
		super(codec);
	}
	
	@Override
	public boolean generate(SpheroidFeatureContext<HangingCaveBlockDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		Spheroid spheroid = context.getSpheroid();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		HangingCaveBlockDecoratorConfig config = context.getConfig();
		
		// TODO: is that correct?
		for (BlockPos bp : getBottomBlocks(world, origin, spheroid)) {
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
