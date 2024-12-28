package de.dafuqs.starryskies.mixin;

import net.minecraft.block.entity.*;
import org.spongepowered.asm.mixin.*;

@Mixin(EndGatewayBlockEntity.class)
public abstract class EndGatewayBlockEntityMixin {
	
	/* TODO
	@Inject(at = @At("HEAD"), method = "tryTeleportingEntity(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/Entity;Lnet/minecraft/block/entity/EndGatewayBlockEntity;)V")
	private static void starryskies$tryTeleportingEntity(World world, BlockPos pos, BlockState state, Entity entity, EndGatewayBlockEntity blockEntity, CallbackInfo ci) {
		BlockPos blockPos;
		if (world instanceof ServerWorld serverWorld) {
			if (((EndGatewayBlockEntityAccessor) blockEntity).getExitPortalPos() == null && world.getRegistryKey() == StarryDimensionKeys.END_KEY) {
				blockPos = EndGatewayBlockEntityAccessor.invokeSetupExitPortalLocation(serverWorld, pos);
				blockPos = blockPos.up(10);
				EndGatewayBlockEntityAccessor.invokeCreatePortal(serverWorld, blockPos, EndGatewayFeatureConfig.createConfig(pos, false));
				((EndGatewayBlockEntityAccessor) blockEntity).setExitPortalPos(blockPos);
			}
		}
	}*/

}
