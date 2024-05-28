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


public class CaveBottomDecorator extends SpheroidDecorator {

	public static final MapCodec<CaveBottomDecorator> CODEC = RecordCodecBuilder.mapCodec((instance) ->
			instance.group(
					BLOCKSTATE_STRING_CODEC.fieldOf("block").forGetter(decorator -> decorator.block),
					Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(decorator -> decorator.chance)
			).apply(instance, CaveBottomDecorator::new));

	private final BlockState block;
	private final float chance;

	public CaveBottomDecorator(BlockState block, float chance) {
		this.block = block;
		this.chance = chance;
	}

	@Override
	protected SpheroidDecoratorType<CaveBottomDecorator> getType() {
		return SpheroidDecoratorType.CAVE_BOTTOM_BLOCK;
	}
	
	@Override
	public void decorate(StructureWorldAccess world, ChunkPos origin, Spheroid spheroid, Random random) {
		for (BlockPos bp : getCaveBottomBlocks(world, origin, spheroid)) {
			if (random.nextFloat() < chance && block.canPlaceAt(world, bp.up())) {
				world.setBlockState(bp.up(), block, 3);
			}
		}
	}
}
