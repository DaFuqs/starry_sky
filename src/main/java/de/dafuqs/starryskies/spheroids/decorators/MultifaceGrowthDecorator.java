package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.gen.feature.*;

import java.util.*;

public class MultifaceGrowthDecorator extends SpheroidDecorator {

	public static final MapCodec<MultifaceGrowthDecorator> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					Registries.BLOCK.getCodec().fieldOf("block").forGetter(decorator -> decorator.featureConfig.lichen),
					RegistryCodecs.entryList(RegistryKeys.BLOCK, true).fieldOf("placeable_on_blocks").forGetter(decorator -> decorator.featureConfig.canPlaceOn),
					Codec.FLOAT.fieldOf("chance").forGetter(decorator -> decorator.chance)
			).apply(instance, MultifaceGrowthDecorator::new)
	);
	
	private final MultifaceGrowthFeatureConfig featureConfig;
	private final float chance;

	public MultifaceGrowthDecorator(Block block, RegistryEntryList<Block> placeableOn, float chance) {
		this.featureConfig = new MultifaceGrowthFeatureConfig((MultifaceGrowthBlock) block, 20, false, true, true, 0.5F, placeableOn);
		this.chance = chance;
	}

	@Override
	protected SpheroidDecoratorType<MultifaceGrowthDecorator> getType() {
		return SpheroidDecoratorType.MULTIFACE_GROWTH;
	}

	@Override
	public void decorate(StructureWorldAccess world, ChunkPos origin, Spheroid spheroid, Random random) {
		int spheroidY = spheroid.getPosition().getY();
		
		for (BlockPos bp : getCaveBottomBlocks(world, origin, spheroid)) {
			if (random.nextFloat() < chance) {
				BlockPos currentPos = new BlockPos(bp.getX(), spheroidY, bp.getZ());
				for (int i = 0; i < spheroid.getRadius(); i++) {
					if (!world.getBlockState(currentPos.up(i)).isAir()) {
						if (world.getBlockState(currentPos.up(i - 1)).isAir()) {
							MultifaceGrowthFeature.generate(world, currentPos, world.getBlockState(bp), featureConfig, random, Arrays.asList(Direction.values()));
						}
						break;
					}
				}
			}
		}
	}
}
