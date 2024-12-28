package de.dafuqs.starryskies.mixin;

import net.minecraft.entity.*;
import org.spongepowered.asm.mixin.*;

@Mixin(Entity.class)
public abstract class EntityMixin {

	/* TODO
	@Inject(at = @At("HEAD"), method = "getTeleportTarget", cancellable = true)
	void starryskies$getTeleportTarget(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> callbackInfo) {
		Entity thisEntity = (Entity) (Object) this;
		TeleportTarget newTeleportTarget = StarryDimensionTravelHandler.handleGetTeleportTarget(thisEntity, destination);
		if (newTeleportTarget != null) {
			if (newTeleportTarget.position == null) {
				// starry dimensions, but no teleport target found
				// cancel vanilla, but without destination
				callbackInfo.setReturnValue(null);
			} else {
				callbackInfo.setReturnValue(newTeleportTarget);
			}
		}
	}

	@ModifyVariable(method = "tickPortal()V", at = @At("STORE"))
	private RegistryKey<World> starryskies$tickPortal(RegistryKey<World> registryKey) {
		return StarryDimensionTravelHandler.modifyNetherPortalDestination((Entity) (Object) this, registryKey);
	}
	
	 */

}