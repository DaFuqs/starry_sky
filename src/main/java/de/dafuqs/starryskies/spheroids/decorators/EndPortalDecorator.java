package de.dafuqs.starryskies.spheroids.decorators;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;

import java.util.*;

public class EndPortalDecorator extends SpheroidDecorator {
	
	public EndPortalDecorator(JsonObject data) throws CommandSyntaxException {
		super(data);
	}
	
	@Override
	public void decorate(StructureWorldAccess world, ChunkPos origin, Spheroid spheroid, Random random) {
		if (!spheroid.isCenterInChunk(origin)) {
			return;
		}
		this.generate(world, new BlockPos(0, 64, 0), true);
	}
	
	public boolean generate(StructureWorldAccess structureWorldAccess, BlockPos blockPos, boolean open) {
		Iterator<BlockPos> var6 = BlockPos.iterate(new BlockPos(blockPos.getX() - 4, blockPos.getY() - 1, blockPos.getZ() - 4), new BlockPos(blockPos.getX() + 4, blockPos.getY() + 32, blockPos.getZ() + 4)).iterator();
		
		while (true) {
			BlockPos blockPos2;
			boolean bl;
			do {
				if (!var6.hasNext()) {
					for (int i = 0; i < 4; ++i) {
						structureWorldAccess.setBlockState(blockPos.up(i), Blocks.BEDROCK.getDefaultState(), 3);
					}
					
					BlockPos blockPos3 = blockPos.up(2);
					for (Direction direction : Direction.Type.HORIZONTAL) {
						structureWorldAccess.setBlockState(blockPos3.offset(direction), Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, direction), 3);
					}
					
					return true;
				}
				
				blockPos2 = var6.next();
				bl = blockPos2.isWithinDistance(blockPos, 2.5D);
			} while (!bl && !blockPos2.isWithinDistance(blockPos, 3.5D));
			
			if (blockPos2.getY() < blockPos.getY()) {
				if (bl) {
					structureWorldAccess.setBlockState(blockPos2, Blocks.BEDROCK.getDefaultState(), 3);
				} else if (blockPos2.getY() < blockPos.getY()) {
					structureWorldAccess.setBlockState(blockPos2, Blocks.END_STONE.getDefaultState(), 3);
				}
			} else if (blockPos2.getY() > blockPos.getY()) {
				structureWorldAccess.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 3);
			} else if (!bl) {
				structureWorldAccess.setBlockState(blockPos2, Blocks.BEDROCK.getDefaultState(), 3);
			} else if (open) {
				structureWorldAccess.setBlockState(new BlockPos(blockPos2), Blocks.END_PORTAL.getDefaultState(), 3);
			} else {
				structureWorldAccess.setBlockState(new BlockPos(blockPos2), Blocks.AIR.getDefaultState(), 3);
			}
		}
	}
	
}
