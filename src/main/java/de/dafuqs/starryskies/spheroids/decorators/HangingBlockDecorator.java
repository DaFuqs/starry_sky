package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

import static de.dafuqs.starryskies.Support.BLOCKSTATE_STRING_CODEC;

public class HangingBlockDecorator extends SpheroidDecorator {

	public static final MapCodec<HangingBlockDecorator> CODEC = RecordCodecBuilder.mapCodec((instance) ->
			instance.group(
					BLOCKSTATE_STRING_CODEC.fieldOf("block").forGetter(decorator -> decorator.block),
					Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(decorator -> decorator.chance)
			).apply(instance, HangingBlockDecorator::new));

	private final BlockState block;
	private final float chance;

	public HangingBlockDecorator(BlockState block, float chance) {
		this.block = block;
		this.chance = chance;
	}

	@Override
	protected SpheroidDecoratorType<HangingBlockDecorator> getType() {
		return SpheroidDecoratorType.HANGING_BLOCK;
	}

	@Override
	public void decorate(StructureWorldAccess world, ChunkPos origin, Spheroid spheroid, Random random) {
		int spheroidY = spheroid.getPosition().getY();
		for (BlockPos bp : getBottomBlocks(world, origin, spheroid)) {
			BlockPos flippedBlockPos = bp.down((bp.getY() - spheroidY) * 2);
			
			if (world.getBlockState(flippedBlockPos.down()).isAir()) {
				if (random.nextFloat() < chance) {
					world.setBlockState(flippedBlockPos.down(), block, 3);
				}
			}
		}
	}
}
