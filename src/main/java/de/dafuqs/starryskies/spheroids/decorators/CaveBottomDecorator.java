package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;


public class CaveBottomDecorator extends SpheroidFeature<CaveBottomDecoratorConfig> {
	
	public CaveBottomDecorator(Codec<CaveBottomDecoratorConfig> codec) {
		super(codec);
	}
	
	@Override
	public boolean generate(SpheroidFeatureContext<CaveBottomDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		Spheroid spheroid = context.getSpheroid();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		CaveBottomDecoratorConfig config = context.getConfig();
		
		for (BlockPos bp : getCaveBottomBlocks(world, origin, spheroid)) {
			if (random.nextFloat() < config.chance() && config.state().canPlaceAt(world, bp.up())) {
				world.setBlockState(bp.up(), config.state(), 3);
			}
		}
		
		return true;
	}
	
}
