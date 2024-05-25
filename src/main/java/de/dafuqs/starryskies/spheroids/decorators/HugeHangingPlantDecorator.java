package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.SpheroidDecoratorType;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

import static de.dafuqs.starryskies.Support.BLOCKSTATE_STRING_CODEC;

public class HugeHangingPlantDecorator extends HugePlantDecorator {

	public static final MapCodec<HugeHangingPlantDecorator> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					BLOCKSTATE_STRING_CODEC.fieldOf("block").forGetter(decorator -> decorator.block),
					BLOCKSTATE_STRING_CODEC.fieldOf("first_block").forGetter(decorator -> decorator.firstBlock),
					BLOCKSTATE_STRING_CODEC.fieldOf("last_block").forGetter(decorator -> decorator.lastBlock),
					Codec.FLOAT.fieldOf("chance").forGetter(decorator -> decorator.chance),
					Codec.INT.fieldOf("minHeight").forGetter(decorator -> decorator.minHeight),
					Codec.INT.fieldOf("maxHeight").forGetter(decorator -> decorator.maxHeight)
			).apply(instance, HugeHangingPlantDecorator::new)
	);

	public HugeHangingPlantDecorator(BlockState block, BlockState firstBlock, BlockState lastBlock, float chance, int minHeight, int maxHeight) {
		super(block, firstBlock, lastBlock, chance, minHeight, maxHeight);
	}

	@Override
	protected SpheroidDecoratorType<HugeHangingPlantDecorator> getType() {
		return SpheroidDecoratorType.HUGE_HANGING_PLANT;
	}

	@Override
	public void decorate(StructureWorldAccess world, ChunkPos origin, Spheroid spheroid, Random random) {
		for (BlockPos bp : getBottomBlocks(world, origin, spheroid)) {
			
			if (random.nextFloat() < chance) {
				int thisHeight = Support.getRandomBetween(random, minHeight, maxHeight);
				for (int i = 1; i < thisHeight + 1; i++) {
					if (world.getBlockState(bp.down(i)).isAir()) {
						
						BlockState placementBlockState = block;
						if (i == 1 && firstBlock != null) {
							placementBlockState = firstBlock;
						} else if (i == thisHeight && lastBlock != null) {
							placementBlockState = lastBlock;
						}
						
						world.setBlockState(bp.down(i), placementBlockState, 3);
					} else {
						if (i > 1 && lastBlock != null) {
							world.setBlockState(bp.down(i - 1), lastBlock, 3);
						}
						break;
					}
				}
			}
		}
	}
	
}
