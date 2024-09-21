package de.dafuqs.starryskies.mixin;

import net.minecraft.block.entity.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;
import net.minecraft.world.gen.feature.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(EndGatewayBlockEntity.class)
public interface EndGatewayBlockEntityAccessor {

	@Invoker("setupExitPortalLocation")
	static BlockPos invokeSetupExitPortalLocation(ServerWorld world, BlockPos pos) {
		return null;
	}

	@Invoker("createPortal")
	static void invokeCreatePortal(ServerWorld world, BlockPos pos, EndGatewayFeatureConfig config) {

	}

	@Accessor("exitPortalPos")
	BlockPos getExitPortalPos();

	@Accessor("exitPortalPos")
	void setExitPortalPos(BlockPos blockPos);

}
