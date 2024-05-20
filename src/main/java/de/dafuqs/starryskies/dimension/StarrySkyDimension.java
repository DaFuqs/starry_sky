package de.dafuqs.starryskies.dimension;

import de.dafuqs.starryskies.*;
import net.kyrptonaught.customportalapi.*;
import net.kyrptonaught.customportalapi.util.*;
import net.minecraft.block.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import static org.apache.logging.log4j.Level.*;

public class StarrySkyDimension {
	
	public static final Identifier STARRY_SKIES_DIMENSION_ID = new Identifier(StarrySkies.MOD_ID, "overworld");
	public static final Identifier STARRY_SKIES_NETHER_DIMENSION_ID = new Identifier(StarrySkies.MOD_ID, "nether");
	public static final Identifier STARRY_SKIES_END_DIMENSION_ID = new Identifier(StarrySkies.MOD_ID, "end");
	public static final RegistryKey<World> OVERWORLD_KEY = getWorld(STARRY_SKIES_DIMENSION_ID);
	public static final RegistryKey<World> NETHER_KEY = getWorld(STARRY_SKIES_NETHER_DIMENSION_ID);
	public static final RegistryKey<World> END_KEY = getWorld(STARRY_SKIES_END_DIMENSION_ID);
	
	private static RegistryKey<World> getWorld(Identifier id) {
		return RegistryKey.of(RegistryKeys.WORLD, id);
	}
	
	public static void setupPortals() {
		StarrySkies.log(INFO, "Setting up portals...");
		
		Identifier portalFrameBlockIdentifier = new Identifier(StarrySkies.CONFIG.starrySkyPortalFrameBlock.toLowerCase());
		Block portalFrameBlock = Registries.BLOCK.get(portalFrameBlockIdentifier);
		
		PortalLink portalLink = new PortalLink(portalFrameBlockIdentifier, STARRY_SKIES_DIMENSION_ID, StarrySkies.CONFIG.starrySkyPortalColor);
		CustomPortalApiRegistry.addPortal(portalFrameBlock, portalLink);
	}
	
}
