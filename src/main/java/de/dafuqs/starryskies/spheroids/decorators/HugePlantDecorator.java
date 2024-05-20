package de.dafuqs.starryskies.spheroids.decorators;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class HugePlantDecorator extends SpheroidDecorator {
	
	protected final BlockState block;
	protected final BlockState firstBlock;
	protected final BlockState lastBlock;
	protected final float chance;
	protected final int minHeight;
	protected final int maxHeight;
	
	/**
	 * A chance of 0 = 0%, 100 = 100%
	 */
	public HugePlantDecorator(JsonObject data) throws CommandSyntaxException {
		super(data);
		block = StarrySkies.getStateFromString(JsonHelper.getString(data, "block"));
		chance = JsonHelper.getFloat(data, "chance");
		if (JsonHelper.hasString(data, "first_block")) {
			firstBlock = StarrySkies.getStateFromString(JsonHelper.getString(data, "first_block"));
		} else {
			firstBlock = null;
		}
		if (JsonHelper.hasString(data, "last_block")) {
			lastBlock = StarrySkies.getStateFromString(JsonHelper.getString(data, "last_block"));
		} else {
			lastBlock = null;
		}
		minHeight = JsonHelper.getInt(data, "min_height");
		maxHeight = JsonHelper.getInt(data, "max_height");
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
