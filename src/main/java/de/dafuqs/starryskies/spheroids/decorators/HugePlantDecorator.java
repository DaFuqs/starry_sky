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

import java.util.Optional;

import static de.dafuqs.starryskies.Support.BLOCKSTATE_STRING_CODEC;

public class HugePlantDecorator extends SpheroidDecorator {

	public static final MapCodec<HugePlantDecorator> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					BLOCKSTATE_STRING_CODEC.fieldOf("block").forGetter(decorator -> decorator.block),
					BLOCKSTATE_STRING_CODEC.lenientOptionalFieldOf("first_block").forGetter(decorator -> Optional.ofNullable(decorator.firstBlock)),
					BLOCKSTATE_STRING_CODEC.lenientOptionalFieldOf("last_block").forGetter(decorator -> Optional.ofNullable(decorator.lastBlock)),
					Codec.FLOAT.fieldOf("chance").forGetter(decorator -> decorator.chance),
					Codec.INT.fieldOf("min_height").forGetter(decorator -> decorator.minHeight),
					Codec.INT.fieldOf("max_height").forGetter(decorator -> decorator.maxHeight)
			).apply(instance, HugePlantDecorator::new)
	);
	
	protected final BlockState block;
	protected final BlockState firstBlock;
	protected final BlockState lastBlock;
	protected final float chance;
	protected final int minHeight;
	protected final int maxHeight;

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public HugePlantDecorator(BlockState block, Optional<BlockState> firstBlock, Optional<BlockState> lastBlock,
                              float chance, int minHeight, int maxHeight) {
		this.block = block;
		this.firstBlock = firstBlock.orElse(null);
		this.lastBlock = lastBlock.orElse(null);
		this.chance = chance;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
	}

	@Override
	protected SpheroidDecoratorType<? extends HugePlantDecorator> getType() {
		return SpheroidDecoratorType.HUGE_PLANT;
	}

	@Override
	public void decorate(StructureWorldAccess world, ChunkPos origin, Spheroid spheroid, Random random) {
		for (BlockPos bp : getTopBlocks(world, origin, spheroid)) {
			BlockState posState = world.getBlockState(bp);
			if (!posState.isFullCube(world, bp)) {
				continue;
			}
			
			if (random.nextFloat() < chance) {
				int thisHeight = Support.getRandomBetween(random, minHeight, maxHeight);
				for (int i = 1; i < thisHeight + 1; i++) {
					if (world.getBlockState(bp.up(i)).isAir()) {
						
						BlockState placementBlockState = block;
						if (i == 1 && firstBlock != null) {
							placementBlockState = firstBlock;
						} else if (i == thisHeight && lastBlock != null) {
							placementBlockState = lastBlock;
						}
						
						world.setBlockState(bp.up(), placementBlockState, 3);
					} else {
						break;
					}
				}
			}
		}
	}
}
