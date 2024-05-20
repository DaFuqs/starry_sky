package de.dafuqs.starryskies.spheroids.decorators;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class ChorusFruitDecorator extends SpheroidDecorator {
	
	private final float chorusChance;
	
	public ChorusFruitDecorator(JsonObject data) throws CommandSyntaxException {
		super(data);
		this.chorusChance = 0.03F;
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
