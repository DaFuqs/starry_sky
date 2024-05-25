package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.block.enums.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

import static de.dafuqs.starryskies.Support.BLOCKSTATE_STRING_CODEC;

public class DoubleBlockDecorator extends SpheroidDecorator {

	public static final MapCodec<DoubleBlockDecorator> CODEC = RecordCodecBuilder.mapCodec((instance) ->
			instance.group(
					BLOCKSTATE_STRING_CODEC.fieldOf("block").forGetter(decorator -> decorator.block),
					Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(decorator -> decorator.chance)
			).apply(instance, DoubleBlockDecorator::new));

	private final BlockState block;
	private final float chance;

	public DoubleBlockDecorator(BlockState block, float chance) {
		this.block = block;
		this.chance = chance;
	}

	@Override
	protected SpheroidDecoratorType<DoubleBlockDecorator> getType() {
		return SpheroidDecoratorType.DOUBLE_BLOCK;
	}

	@Override
	public void decorate(StructureWorldAccess world, ChunkPos origin, Spheroid spheroid, Random random) {
		for (BlockPos bp : getTopBlocks(world, origin, spheroid)) {
			if (!world.getBlockState(bp).isAir() && world.getBlockState(bp.up()).isAir() && world.getBlockState(bp.up(2)).isAir()) {
				if (random.nextFloat() < chance) {
					world.setBlockState(bp.up(), block.with(TallFlowerBlock.HALF, DoubleBlockHalf.LOWER), 3);
					world.setBlockState(bp.up(2), block.with(TallFlowerBlock.HALF, DoubleBlockHalf.UPPER), 3);
				}
			}
		}
	}
}
