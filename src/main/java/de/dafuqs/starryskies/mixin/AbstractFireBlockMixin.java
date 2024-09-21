package de.dafuqs.starryskies.mixin;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
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
			if (world.getRegistryKey().equals(StarryDimensionKeys.OVERWORLD_KEY) || world.getRegistryKey().equals(StarryDimensionKeys.NETHER_KEY)) {
				callbackInfoReturnable.setReturnValue(true);
			}
		}
	}

}