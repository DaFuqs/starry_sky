package de.dafuqs.starryskies;

import de.dafuqs.starryskies.dimension.*;
import de.dafuqs.starryskies.dimension.sky.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;

public class StarrySkiesClient implements ClientModInitializer {
	
	@Override
	public void onInitializeClient() {
		StarrySkyBox skybox;
		if (StarrySkies.CONFIG.rainbowSkybox) {
			skybox = new StarrySkyBox("textures/skybox/rainbow_up.png", "textures/skybox/rainbow_down.png", "textures/skybox/rainbow_west.png", "textures/skybox/rainbow_east.png", "textures/skybox/rainbow_north.png", "textures/skybox/rainbow_south.png");
		} else {
			skybox = new StarrySkyBox("textures/skybox/light.png", "textures/skybox/darker.png", "textures/skybox/west.png", "textures/skybox/east.png", "textures/skybox/north.png", "textures/skybox/south.png");
		}
		DimensionRenderingRegistry.registerSkyRenderer(StarrySkyDimension.OVERWORLD_KEY, skybox);
	}
	
}
