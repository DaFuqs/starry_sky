package de.dafuqs.starryskies.client;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.client.sky.*;
import de.dafuqs.starryskies.registries.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;

public class StarrySkiesClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		DimensionRenderingRegistry.registerSkyRenderer(
				StarryDimensionKeys.OVERWORLD_KEY,
				StarrySkies.CONFIG.rainbowSkybox
						? new StarrySkyBox("textures/skybox/rainbow_up.png", "textures/skybox/rainbow_down.png", "textures/skybox/rainbow_west.png", "textures/skybox/rainbow_east.png", "textures/skybox/rainbow_north.png", "textures/skybox/rainbow_south.png")
						: new StarrySkyBox("textures/skybox/light.png", "textures/skybox/darker.png", "textures/skybox/west.png", "textures/skybox/east.png", "textures/skybox/north.png", "textures/skybox/south.png"));
	}

}
