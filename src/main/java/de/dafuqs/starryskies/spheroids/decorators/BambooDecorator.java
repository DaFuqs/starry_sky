package de.dafuqs.starryskies.spheroids.decorators;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.block.enums.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class BambooDecorator extends SpheroidDecorator {
	
	private final float chance;
	private final float saplingChance;
	private final BlockState bambooBlockState;
	private final BlockState bambooSaplingBlockState;
	
	public BambooDecorator(JsonObject data) throws CommandSyntaxException {
		super(data);
		this.chance = JsonHelper.getFloat(data, "chance");
		this.saplingChance = JsonHelper.getFloat(data, "sapling_chance");
		this.bambooBlockState = StarrySkies.getStateFromString(JsonHelper.getString(data, "bamboo_block"));
		this.bambooSaplingBlockState = StarrySkies.getStateFromString(JsonHelper.getString(data, "sapling_block"));
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
