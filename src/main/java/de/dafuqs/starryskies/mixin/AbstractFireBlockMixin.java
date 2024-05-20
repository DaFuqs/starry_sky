package de.dafuqs.starryskies.mixin;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.dimension.*;
import net.minecraft.block.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;


@Mixin(AbstractFireBlock.class)
public abstract class AbstractFireBlockMixin {
	
	@Inject(method = {"isOverworldOrNether"}, at = {@At("HEAD")}, cancellable = true)
	private static void starryskies$isOverworldOrNether(World world, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if (StarrySkies.CONFIG.enableNetherPortalsToStarryNether) {
			if (world.getRegistryKey().equals(StarrySkyDimension.OVERWORLD_KEY) || world.getRegistryKey().equals(StarrySkyDimension.NETHER_KEY)) {
				callbackInfoReturnable.setReturnValue(true);
			}
		}
	}
	
}