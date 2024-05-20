package de.dafuqs.starryskies.mixin;

import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(Entity.class)

public interface EntityAccessor {
	
	@Accessor("lastNetherPortalPosition")
	BlockPos getLastNetherPortalPosition();
	
}