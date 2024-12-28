package de.dafuqs.starryskies.mixin;

import net.kyrptonaught.customportalapi.util.*;
import org.spongepowered.asm.mixin.*;

@Mixin(CustomTeleporter.class)
public class CustomTeleporterMixin {
	
	/* TODO
	@WrapOperation(method = "TPToDim", at = @At(value = "INVOKE", target = "Lnet/kyrptonaught/customportalapi/util/CustomTeleporter;customTPTarget(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/kyrptonaught/customportalapi/portal/frame/PortalFrameTester$PortalFrameTesterFactory;)Lnet/minecraft/world/TeleportTarget;"))
	private static TeleportTarget replaceTarget(ServerWorld destinationWorld, Entity entity, BlockPos enteredPortalPos, Block frameBlock, PortalFrameTester.PortalFrameTesterFactory frameTesterFactory, Operation<TeleportTarget> original) {
		var target = StarryDimensionTravelHandler.handleGetTeleportTarget(entity, destinationWorld);
		return target == null ? original.call(destinationWorld, entity, enteredPortalPos, frameBlock, frameTesterFactory) : target;
	}
	 */

}
