package de.dafuqs.starryskies.spheroids.decorators;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class EndGatewayDecorator extends SeaGreensDecorator {
	
	public EndGatewayDecorator(JsonObject data) throws CommandSyntaxException {
		super(data);
	}
	
	@Override
	public void decorate(StructureWorldAccess world, ChunkPos origin, Spheroid spheroid, Random random) {
		if (!spheroid.isCenterInChunk(origin)) {
			return;
		}
		
		BlockPos exitBlockPos = StarrySkyDimensionTravelHandler.END_SPAWN_BLOCK_POS;
		BlockPos portalBlockPos = spheroid.getPosition();
		
		for (BlockPos blockPos2 : BlockPos.iterate(portalBlockPos.add(-1, -2, -1), portalBlockPos.add(1, 2, 1))) {
			boolean bl = blockPos2.getX() == portalBlockPos.getX();
			boolean bl2 = blockPos2.getY() == portalBlockPos.getY();
			boolean bl3 = blockPos2.getZ() == portalBlockPos.getZ();
			boolean bl4 = Math.abs(blockPos2.getY() - portalBlockPos.getY()) == 2;
			if (bl && bl2 && bl3) {
				BlockPos blockPos3 = blockPos2.toImmutable();
				
				world.setBlockState(blockPos3, Blocks.END_GATEWAY.getDefaultState(), 3);
				
				// set exit position
				BlockEntity blockEntity = world.getBlockEntity(blockPos3);
				if (blockEntity instanceof EndGatewayBlockEntity endGatewayBlockEntity) {
					endGatewayBlockEntity.setExitPortalPos(exitBlockPos, false);
					blockEntity.markDirty();
				}
				
			} else if (bl2) {
				world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 3);
			} else if (bl4 && bl && bl3) {
				world.setBlockState(blockPos2, Blocks.BEDROCK.getDefaultState(), 3);
			} else if ((bl || bl3) && !bl4) {
				world.setBlockState(blockPos2, Blocks.BEDROCK.getDefaultState(), 3);
			} else {
				world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 3);
			}
		}
	}
	
}
