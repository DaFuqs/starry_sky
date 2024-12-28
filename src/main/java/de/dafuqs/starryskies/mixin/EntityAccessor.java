package de.dafuqs.starryskies.mixin;

import net.minecraft.entity.*;
import org.spongepowered.asm.mixin.*;

@Mixin(Entity.class)
public interface EntityAccessor {

	/*
	@Accessor("lastNetherPortalPosition")
	BlockPos getLastNetherPortalPosition();
	*/
}