package de.dafuqs.starryskies.mixin;

import de.dafuqs.starryskies.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.server.world.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(Entity.class)
public abstract class EntityMixin {
	
	@Inject(at = @At("HEAD"), method = "getTeleportTarget", cancellable = true)
	void starryskies$getTeleportTarget(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> callbackInfo) {
		Entity thisEntity = (Entity) (Object) this;
		TeleportTarget newTeleportTarget = StarrySkyDimensionTravelHandler.handleGetTeleportTarget(thisEntity, destination);
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
		return StarrySkyDimensionTravelHandler.modifyNetherPortalDestination((Entity) (Object) this, registryKey);
	}
	
}