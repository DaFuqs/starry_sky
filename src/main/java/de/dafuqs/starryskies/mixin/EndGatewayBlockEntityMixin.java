package de.dafuqs.starryskies.mixin;

import de.dafuqs.starryskies.dimension.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.gen.feature.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(EndGatewayBlockEntity.class)
public abstract class EndGatewayBlockEntityMixin {
	
	@Inject(at = @At("HEAD"), method = "tryTeleportingEntity(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/Entity;Lnet/minecraft/block/entity/EndGatewayBlockEntity;)V")
	private static void starryskies$tryTeleportingEntity(World world, BlockPos pos, BlockState state, Entity entity, EndGatewayBlockEntity blockEntity, CallbackInfo ci) {
		BlockPos blockPos;
		if (world instanceof ServerWorld serverWorld) {
			if (((EndGatewayBlockEntityAccessor) blockEntity).getExitPortalPos() == null && world.getRegistryKey() == StarrySkyDimension.END_KEY) {
				blockPos = EndGatewayBlockEntityAccessor.invokeSetupExitPortalLocation(serverWorld, pos);
				blockPos = blockPos.up(10);
				EndGatewayBlockEntityAccessor.invokeCreatePortal(serverWorld, blockPos, EndGatewayFeatureConfig.createConfig(pos, false));
				((EndGatewayBlockEntityAccessor) blockEntity).setExitPortalPos(blockPos);
			}
		}
	}
	
}
