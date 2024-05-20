package de.dafuqs.starryskies.advancements;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;

import java.util.*;

import static org.apache.logging.log4j.Level.*;

public class ProximityAdvancementCheckEvent implements ServerTickEvents.EndTick {
	
	private static int tickCounter;
	private final static int advancementsEveryXTicks = 100;
	
	@Override
	public void onEndTick(MinecraftServer minecraftServer) {
		tickCounter++;
		if (tickCounter % advancementsEveryXTicks == 0) {
			tickCounter = 0;
			StarrySkies.log(DEBUG, "Advancement check start. Players: " + minecraftServer.getPlayerManager().getCurrentPlayerCount());
			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
				StarrySkies.log(DEBUG, "Checking player " + serverPlayerEntity.getName());
				if (StarrySkies.inStarryWorld(serverPlayerEntity)) {
					StarrySkies.log(DEBUG, "In starry world");
					Optional<Support.SpheroidDistance> spheroidDistance = Support.getClosestSpheroidToPlayer(serverPlayerEntity);
					if (spheroidDistance.isPresent() && (Math.sqrt(spheroidDistance.get().squaredDistance)) < spheroidDistance.get().spheroid.getRadius() + 2) {
						Spheroid spheroid = spheroidDistance.get().spheroid;
						StarrySkies.log(DEBUG, "On spheroid with template id: " + spheroid.getTemplate().getID());
						StarryAdvancementCriteria.SPHEROID_DISCOVERED.trigger(serverPlayerEntity, spheroid);
					}
				}
			}
		}
	}
	
}
