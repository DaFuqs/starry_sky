package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

import static de.dafuqs.starryskies.Support.BLOCKSTATE_STRING_CODEC;

public class SingleBlockDecorator extends SpheroidDecorator {
	
	public static final MapCodec<SingleBlockDecorator> CODEC = RecordCodecBuilder.mapCodec((instance) ->
			instance.group(
					BLOCKSTATE_STRING_CODEC.fieldOf("block").forGetter(decorator -> decorator.state),
					Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(decorator -> decorator.chance)
			).apply(instance, SingleBlockDecorator::new));
	
	private final BlockState state;
	private final float chance;
	
	public SingleBlockDecorator(BlockState state, float chance) {
		this.state = state;
		this.chance = chance;
	}
	
	@Override
	public void decorate(StructureWorldAccess world, ChunkPos origin, Spheroid spheroid, Random random) {
		for (BlockPos bp : getTopBlocks(world, origin, spheroid)) {
			BlockState posState = world.getBlockState(bp);
			if (posState.isFullCube(world, bp) && world.getBlockState(bp.up()).isAir()) {
				if (random.nextFloat() < chance) {
					world.setBlockState(bp.up(), state, 3);
				}
			}
		}
	}
	
	@Override
	protected SpheroidDecoratorType<SingleBlockDecorator> getType() {
		return SpheroidDecoratorType.SINGLE_BLOCK;
	}
	
}
