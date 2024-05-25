package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

import static de.dafuqs.starryskies.Support.BLOCKSTATE_STRING_CODEC;

public class StackedBlockDecorator extends SpheroidDecorator {

	public static final MapCodec<StackedBlockDecorator> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					BLOCKSTATE_STRING_CODEC.fieldOf("block").forGetter(decorator -> decorator.block),
					Codec.FLOAT.fieldOf("chance").forGetter(decorator -> decorator.chance),
					Codec.INT.fieldOf("min_height").forGetter(decorator -> decorator.minHeight),
					Codec.INT.fieldOf("max_height").forGetter(decorator -> decorator.maxHeight)
			).apply(instance, StackedBlockDecorator::new)
	);

	private final BlockState block;
	private final float chance;
	private final int minHeight;
	private final int maxHeight;

	public StackedBlockDecorator(BlockState block, float chance, int minHeight, int maxHeight) {
		this.block = block;
		this.chance = chance;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
	}

	@Override
	protected SpheroidDecoratorType<StackedBlockDecorator> getType() {
		return SpheroidDecoratorType.STACKED_BLOCK;
	}

	@Override
	public void decorate(StructureWorldAccess world, ChunkPos origin, Spheroid spheroid, Random random) {
		for (BlockPos bp : getTopBlocks(world, origin, spheroid)) {
			if (random.nextFloat() < chance) {
				int height = Support.getRandomBetween(random, minHeight, maxHeight);
				for (int i = 0; i < height; i++) {
					if (block.canPlaceAt(world, bp.up(i + 1))) {
						world.setBlockState(bp.up(i + 1), block, 3);
					}
				}
			}
		}
	}
}
