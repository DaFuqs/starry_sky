package de.dafuqs.starryskies.mixin;

import de.dafuqs.starryskies.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;


@Mixin(EndPortalBlock.class)
public abstract class EndPortalBlockMixin {

	@Inject(at = @At("HEAD"), method = "onEntityCollision", cancellable = true)
	void starryskies$onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo callbackInfo) {
		if (StarrySkies.CONFIG.enableEndPortalsToStarryEnd) {
			boolean handled = StarryDimensionTravelHandler.handleEndPortalCollision(state, world, pos, entity);
			if (handled) {
				callbackInfo.cancel();
			}
		}
	}

}