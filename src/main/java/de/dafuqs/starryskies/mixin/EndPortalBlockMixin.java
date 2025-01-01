package de.dafuqs.starryskies.mixin;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(EndPortalBlock.class)
public abstract class EndPortalBlockMixin {
	
	@Inject(at = @At("HEAD"), method = "createTeleportTarget(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/TeleportTarget;", cancellable = true)
	void starryskies$createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos, CallbackInfoReturnable<TeleportTarget> cir) {
		if (StarrySkies.CONFIG.enableEndPortalsToStarryEnd) {
			boolean isInStarryEnd = world.getRegistryKey() == StarryDimensionKeys.END_KEY;
			boolean isInStarryOverworld = world.getRegistryKey() == StarryDimensionKeys.OVERWORLD_KEY;
			
			if (isInStarryEnd || isInStarryOverworld) {
				// show the credits
				// taken from EndPortalBlock.onEntityCollision()
				if (!world.isClient && isInStarryEnd && entity instanceof ServerPlayerEntity serverPlayerEntity) {
					if (!serverPlayerEntity.seenCredits) {
						serverPlayerEntity.detachForDimensionChange();
						cir.cancel();
					}
				}
				
				RegistryKey<World> targetWorldKey = isInStarryEnd ? StarryDimensionKeys.OVERWORLD_KEY : StarryDimensionKeys.END_KEY;
				ServerWorld serverWorld = world.getServer().getWorld(targetWorldKey);
				if (serverWorld == null) {
					cir.cancel();
				} else {
					BlockPos targetPos = isInStarryOverworld ? StarryDimensionKeys.STARRY_END_SPAWN_BLOCK_POS : StarryDimensionKeys.STARRY_OVERWORLD_SPAWN_BLOCK_POS;
					Vec3d targetVec = targetPos.toBottomCenterPos();
					float entityYaw = entity.getYaw();
					if (isInStarryOverworld) {
						entityYaw = Direction.WEST.getPositiveHorizontalDegrees();
						if (entity instanceof ServerPlayerEntity) {
							targetVec = targetVec.subtract(0.0, 1.0, 0.0);
						}
					} else {
						if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
							cir.setReturnValue(serverPlayerEntity.getRespawnTarget(false, TeleportTarget.NO_OP));
						}
						
						targetVec = entity.getWorldSpawnPos(serverWorld, targetPos).toBottomCenterPos();
					}
					
					cir.setReturnValue(new TeleportTarget(serverWorld, targetVec, entity.getVelocity(), entityYaw, entity.getPitch(), TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)));
				}
			}
		}
	}

}