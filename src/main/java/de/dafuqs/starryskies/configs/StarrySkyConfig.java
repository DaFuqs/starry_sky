package de.dafuqs.starryskies.configs;

import me.shedaniel.autoconfig.*;
import me.shedaniel.autoconfig.annotation.*;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.*;
import net.minecraft.block.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;

@Config(name = "StarrySky")
public class StarrySkyConfig implements ConfigData {
	
	@ConfigEntry.Category("GENERAL")
	@Comment(value = """
			Logs errors when loading Datapack Spheres and decorators to the log.""")
	public boolean packCreatorMode = false;
	
	@ConfigEntry.Category("GENERAL")
	@Comment(value = """
			Should Starry register Portal Blocks for Overworld <=> Starry Skies travel.
			If set to false can be used completely serverside, as long as you add a means to travel between dimensions.""")
	public boolean registerStarryPortal = true;
	
	@ConfigEntry.Gui.PrefixText()
	@ConfigEntry.Category("GENERAL")
	@Comment(value = """
			The block the portal to the Starry Sky dimension needs to be built with.
			Build it like a nether portal & has to be activated with flint & steel
			Default: PACKED_ICE""")
	public String starrySkyPortalFrameBlock = "PACKED_ICE";
	
	@ConfigEntry.Category("GENERAL")
	@Comment(value = """
			The Color for the Portal to Starry Skies
			Default: 11983869 (light, grayish blue)""")
	public int starrySkyPortalColor = 11983869;
	
	@ConfigEntry.Category("GENERAL")
	@Comment(value = """
			The height of clouds in the Starry Sky dimension.
			Default: 270""")
	public float cloudHeight = 270F;
	
	@ConfigEntry.Category("GENERAL")
	@Comment(value = """
			Use a fancy rainbow skybox instead of a generic one.
			Default: true""")
	public boolean rainbowSkybox = true;
	
	@ConfigEntry.Category("GENERAL")
	@Comment(value = """
			If true nether portals in Starry Sky lead to Scary Sky, if false portals do not form.
			Default: true""")
	public boolean enableNetherPortalsToStarryNether = true;
	
	@ConfigEntry.Category("GENERAL")
	@Comment(value = """
			If true end portals in Starry Sky lead to Scarcy Sky, if false to the vanilla end.
			Default: true""")
	public boolean enableEndPortalsToStarryEnd = true;
	
	@ConfigEntry.Gui.Tooltip()
	@ConfigEntry.Category("GENERAL")
	@Comment(value = """
			The '/sphere' command lists all the data of the closest sphere (position, blocks, ...)
			
			Default: 0""")
	public int sphereCommandRequiredPermissionLevel = 2;
	
	@ConfigEntry.Gui.PrefixText
	@ConfigEntry.Gui.Tooltip()
	@ConfigEntry.Category("SYSTEM GENERATION")
	@Comment(value = """
			Spheroids are generated in systems.
			Each system consists out of x spheroids over y chunks.
			How big each system should be in chunks²
			Higher values make the very slight 'gaps' at the border between
			systems less common, but since systems are generating all at once
			high values can result in small lag spikes every time a new system is generated. (but less spikes in total)
			Default: 50""")
	public int systemSizeChunks = 50;
	
	private boolean isValidBlock(String blockName) {
		// validate floorBlock
		try {
			Identifier identifier = new Identifier(blockName.toLowerCase());
			BlockState bs = Registries.BLOCK.get(identifier).getDefaultState();
			if (bs == null) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@Override
	public void validatePostLoad() {
		// portal frame blocks
		if (!isValidBlock(starrySkyPortalFrameBlock)) {
			starrySkyPortalFrameBlock = "PACKED_ICE";
		}
		
	}
	
}
