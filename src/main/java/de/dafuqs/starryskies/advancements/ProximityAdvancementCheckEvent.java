package de.dafuqs.starryskies.advancements;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;

import java.util.*;

public class ProximityAdvancementCheckEvent implements ServerTickEvents.EndTick {

	private final static int ADVANCEMENT_CHECK_TICKS = 100;
	private static int tickCounter;

	@Override
	public void onEndTick(MinecraftServer minecraftServer) {
		tickCounter++;
		if (tickCounter % ADVANCEMENT_CHECK_TICKS == 0) {
			tickCounter = 0;
			StarrySkies.LOGGER.debug("Advancement check start. Players: {}", minecraftServer.getPlayerManager().getCurrentPlayerCount());
			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
				StarrySkies.LOGGER.debug("Checking player {}", serverPlayerEntity.getName());
				if (StarrySkies.isStarryWorld(serverPlayerEntity.getServerWorld())) {
					StarrySkies.LOGGER.debug("In starry world");
					Optional<Support.SphereDistance> distance = Support.getClosestSphere(serverPlayerEntity);
					if (distance.isPresent() && (Math.sqrt(distance.get().squaredDistance)) < distance.get().sphere.getRadius() + 2) {
						PlacedSphere<?> sphere = distance.get().sphere;
						StarryAdvancementCriteria.SPHERE_DISCOVERED.trigger(serverPlayerEntity, sphere);
					}
				}
			}
		}
	}

}
