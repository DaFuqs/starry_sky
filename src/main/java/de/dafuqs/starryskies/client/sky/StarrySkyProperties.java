package de.dafuqs.starryskies.client.sky;

import de.dafuqs.starryskies.*;
import net.fabricmc.api.*;
import net.minecraft.client.render.*;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.*;

@Environment(EnvType.CLIENT)
public class StarrySkyProperties extends DimensionEffects {

	public StarrySkyProperties() {
		super(StarrySkies.CONFIG.cloudHeight, true, SkyType.NORMAL, true, false);
	}

	/**
	 * @param color
	 * @param sunHeight Cos 0-1
	 * @return
	 */
	@Override
	public Vec3d adjustFogColor(@NotNull Vec3d color, float sunHeight) {
		return color.multiply((sunHeight), (sunHeight), (sunHeight));
	}

	@Override
	public boolean useThickFog(int camX, int camY) {
		return false;
	}
	
	@Override
	public boolean shouldBrightenLighting() {
		return false;
	}


}
