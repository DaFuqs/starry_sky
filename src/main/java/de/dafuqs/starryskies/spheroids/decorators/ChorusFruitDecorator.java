package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.MapCodec;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class ChorusFruitDecorator extends SpheroidDecorator {

	public static MapCodec<ChorusFruitDecorator> CODEC = MapCodec.unit(ChorusFruitDecorator::new);
	
	private final float chorusChance;
	
	public ChorusFruitDecorator() {
		super();
		this.chorusChance = 0.03F;
	}

	@Override
	protected SpheroidDecoratorType<ChorusFruitDecorator> getType() {
		return SpheroidDecoratorType.CHORUS_FRUIT;
	}

	@Override
	public void decorate(StructureWorldAccess world, ChunkPos origin, Spheroid spheroid, Random random) {
		for (BlockPos bp : getTopBlocks(world, origin, spheroid)) {
			if (random.nextFloat() < chorusChance) {
				ChorusFlowerBlock.generate(world, bp.up(), random, 8);
			}
		}
	}
	
}
