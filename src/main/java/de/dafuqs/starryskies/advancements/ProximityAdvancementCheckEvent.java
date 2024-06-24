package de.dafuqs.starryskies.advancements;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
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
				if (StarrySkies.inStarryWorld(serverPlayerEntity)) {
					StarrySkies.LOGGER.debug("In starry world");
					Optional<Support.SpheroidDistance> spheroidDistance = Support.getClosestSpheroidToPlayer(serverPlayerEntity);
					if (spheroidDistance.isPresent() && (Math.sqrt(spheroidDistance.get().squaredDistance)) < spheroidDistance.get().spheroid.getRadius() + 2) {
						Spheroid spheroid = spheroidDistance.get().spheroid;
						StarrySkies.LOGGER.debug("On spheroid with template id: {}", spheroid.getTemplate().getID());
						StarryAdvancementCriteria.SPHEROID_DISCOVERED.trigger(serverPlayerEntity, spheroid);
					}
				}
			}
		}
	}
	
}
