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

public class BambooDecorator extends SpheroidDecorator {

	public static final MapCodec<BambooDecorator> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					Codec.FLOAT.fieldOf("chance").forGetter(decorator -> decorator.chance),
					Codec.FLOAT.fieldOf("sapling_chance").forGetter(decorator -> decorator.saplingChance),
					BLOCKSTATE_STRING_CODEC.fieldOf("bamboo_block").forGetter(decorator -> decorator.bambooBlockState),
					BLOCKSTATE_STRING_CODEC.fieldOf("sapling_block").forGetter(decorator -> decorator.bambooSaplingBlockState)
			).apply(instance, BambooDecorator::new)
	);
	
	private final float chance;
	private final float saplingChance;
	private final BlockState bambooBlockState;
	private final BlockState bambooSaplingBlockState;

	public BambooDecorator(float chance, float saplingChance, BlockState bambooBlockState, BlockState bambooSaplingBlockState) {
		this.chance = chance;
		this.saplingChance = saplingChance;
		this.bambooBlockState = bambooBlockState;
		this.bambooSaplingBlockState = bambooSaplingBlockState;
	}

	@Override
	protected SpheroidDecoratorType<BambooDecorator> getType() {
		return SpheroidDecoratorType.BAMBOO;
	}

	@Override
	public void decorate(StructureWorldAccess world, ChunkPos origin, Spheroid spheroid, Random random) {
		for (BlockPos bp : getTopBlocks(world, origin, spheroid)) {
			if (random.nextFloat() < chance) {
				if (random.nextFloat() < saplingChance) {
					if (bambooSaplingBlockState.canPlaceAt(world, bp.up())) {
						world.setBlockState(bp.up(), bambooSaplingBlockState, 3);
					}
				} else {
					int height = random.nextInt(8);
					for (int i = 1; i < height; i++) {
						if (bambooBlockState.canPlaceAt(world, bp.up(i))) {
							if (i == 3 && height < 5) {
								world.setBlockState(bp.up(i), bambooBlockState.with(BambooBlock.LEAVES, BambooLeaves.NONE), 3);
							} else if (i > 4) {
								world.setBlockState(bp.up(i), bambooBlockState.with(BambooBlock.LEAVES, BambooLeaves.LARGE), 3);
							} else if (i > 2) {
								world.setBlockState(bp.up(i), bambooBlockState.with(BambooBlock.LEAVES, BambooLeaves.SMALL), 3);
							} else {
								world.setBlockState(bp.up(i), bambooBlockState.with(BambooBlock.LEAVES, BambooLeaves.NONE), 3);
							}
						}
					}
				}
			}
		}
	}
}
