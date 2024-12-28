package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class EndGatewayDecorator extends SphereDecorator<SphereDecoratorConfig.DefaultSphereDecoratorConfig> {

	public EndGatewayDecorator(Codec<SphereDecoratorConfig.DefaultSphereDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(SphereFeatureContext<SphereDecoratorConfig.DefaultSphereDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		PlacedSphere<?> sphere = context.getSphere();
		ChunkPos origin = context.getChunkPos();

		if (!sphere.isCenterInChunk(origin)) {
			return false;
		}
		
		BlockPos exitBlockPos = StarryDimensionKeys.STARRY_END_SPAWN_BLOCK_POS;
		BlockPos portalBlockPos = sphere.getPosition();

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

		return true;
	}

}
