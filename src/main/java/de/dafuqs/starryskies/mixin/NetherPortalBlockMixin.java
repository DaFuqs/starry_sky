package de.dafuqs.starryskies.mixin;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.border.*;
import net.minecraft.world.dimension.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;


@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {
	
	@Shadow
	@Nullable
	protected abstract TeleportTarget getOrCreateExitPortalTarget(ServerWorld world, Entity entity, BlockPos pos, BlockPos scaledPos, boolean inNether, WorldBorder worldBorder);
	
	@Inject(at = @At("HEAD"), method = "createTeleportTarget(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/TeleportTarget;", cancellable = true)
	void starryskies$createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos, CallbackInfoReturnable<TeleportTarget> cir) {
		if (StarrySkies.CONFIG.enableNetherPortalsToStarryNether) {
			RegistryKey<World> targetWorldKey = world.getRegistryKey() == StarryDimensionKeys.NETHER_KEY ? StarryDimensionKeys.OVERWORLD_KEY : StarryDimensionKeys.NETHER_KEY;
			ServerWorld targetWorld = world.getServer().getWorld(targetWorldKey);
			if (targetWorld != null) {
				boolean bl = targetWorld.getRegistryKey() == World.NETHER;
				WorldBorder worldBorder = targetWorld.getWorldBorder();
				double d = DimensionType.getCoordinateScaleFactor(world.getDimension(), targetWorld.getDimension());
				BlockPos blockPos = worldBorder.clampFloored(entity.getX() * d, entity.getY(), entity.getZ() * d);
				cir.setReturnValue(this.getOrCreateExitPortalTarget(targetWorld, entity, pos, blockPos, bl, worldBorder));
			}
		}
	}
	
}