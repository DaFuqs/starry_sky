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
	public int sphereCommandRequiredPermissionLevel = 0;
	
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
	
	@ConfigEntry.Gui.Tooltip()
	@ConfigEntry.Category("SYSTEM GENERATION")
	@Comment(value = """
			How many spheres a system can contain max.
			Some will fail because of distance restrictions
			so it's not an exact value
			Default: 2000""")
	public int sphereDensityOverworld = 2000;
	
	@ConfigEntry.Gui.Tooltip()
	@ConfigEntry.Category("SYSTEM GENERATION")
	@Comment(value = """
			How much empty blocks should be enforced between individual spheres.
			If the distance is too low generation of that one sphere will be cancelled.
			Default: 10
			""")
	public int minDistanceBetweenSpheresOverworld = 10;
	
	@ConfigEntry.Gui.Tooltip()
	@ConfigEntry.Category("SYSTEM GENERATION")
	@Comment(value = """
			How many spheres a system can contain max.
			Some will fail because of distance restrictions
			so it's not an exact value
			Default: 2500""")
	public int sphereDensityNether = 2500;
	
	@ConfigEntry.Gui.Tooltip()
	@ConfigEntry.Category("SYSTEM GENERATION")
	@Comment(value = """
			How much empty blocks should be enforced between individual spheres.
			If the distance is too low generation of that one sphere will be cancelled.
			Default: 10
			""")
	public int minDistanceBetweenSpheresNether = 7;
	
	@ConfigEntry.Gui.Tooltip()
	@ConfigEntry.Category("SYSTEM GENERATION")
	@Comment(value = """
			How many spheres a system can contain max.
			Some will fail because of distance restrictions
			so it's not an exact value
			Default: 1500""")
	public int sphereDensityEnd = 1500;
	
	@ConfigEntry.Gui.Tooltip()
	@ConfigEntry.Category("SYSTEM GENERATION")
	@Comment(value = """
			How much empty blocks should be enforced between individual spheres.
			If the distance is too low generation of that one sphere will be cancelled.
			Default: 10
			""")
	public int minDistanceBetweenSpheresEnd = 8;
	
	@ConfigEntry.Gui.PrefixText
	@ConfigEntry.Category("WORLD FLOOR GENERATION")
	@Comment(value = """
			If (or how high) there should be a world floor.
			When set to >1 there will the specified BottomBlock at y=1 and the floor block up to the floor height
			When set to  1 there will the specified BottomBlock at y=1
			When set to  0 there will be no ground, only void
			Default: 0 (only void)""")
	@ConfigEntry.BoundedDiscrete(min = 0, max = 128)
	public int floorHeightOverworld = 0;
	
	@ConfigEntry.Category("WORLD FLOOR GENERATION")
	@Comment(value = """
			The block generating at y>1 (if floorHeight > 1)
			Default: WATER""")
	public String floorBlockOverworld = "WATER";
	
	@ConfigEntry.Category("WORLD FLOOR GENERATION")
	@Comment(value = """
			The block generating at y=0 (if floorHeight > 0)
			Default: BEDROCK""")
	public String bottomBlockOverworld = "BEDROCK";
	
	@ConfigEntry.Category("WORLD FLOOR GENERATION")
	@Comment(value = """
			If (or how high) there should be a world floor.
			When set to  1 there will the specified BottomBlock at y=1 and the floor block up to the floor height
			When set to >1 there will the specified BottomBlock at y=1
			When set to  0 there will be no ground, only void
			Default: 0 (only void)""")
	@ConfigEntry.BoundedDiscrete(min = 0, max = 128)
	public int floorHeightNether = 0;
	
	@ConfigEntry.Category("WORLD FLOOR GENERATION")
	@Comment(value = """
			The block generating at y>1 (if floorHeight > 1)
			Default: LAVA""")
	public String floorBlockNether = "LAVA";
	
	@ConfigEntry.Category("WORLD FLOOR GENERATION")
	@Comment(value = """
			The block generating at y=0 (if floorHeight > 0)
			Default: BEDROCK""")
	public String bottomBlockNether = "BEDROCK";
	
	@ConfigEntry.Category("WORLD FLOOR GENERATION")
	@Comment(value = """
			If (or how high) there should be a world floor.
			When set to  1 there will the specified BottomBlock at y=1 and the floor block up to the floor height
			When set to >1 there will the specified BottomBlock at y=1
			When set to  0 there will be no ground, only void
			Default: 0 (only void)""")
	@ConfigEntry.BoundedDiscrete(min = 0, max = 128)
	public int floorHeightEnd = 0;
	
	@ConfigEntry.Category("WORLD FLOOR GENERATION")
	@Comment(value = """
			The block generating at y>1 (if floorHeight > 1)
			Default: WATER""")
	public String floorBlockEnd = "WATER";
	
	@ConfigEntry.Category("WORLD FLOOR GENERATION")
	@Comment(value = """
			The block generating at y=0 (if floorHeight > 0)
			Default: BEDROCK""")
	public String bottomBlockEnd = "BEDROCK";
	
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
		// do all the blocks exist?
		// if not: use defaults
		
		// floor blocks
		if (!isValidBlock(floorBlockOverworld)) {
			floorBlockOverworld = "WATER";
		}
		if (!isValidBlock(floorBlockNether)) {
			floorBlockNether = "LAVA";
		}
		if (!isValidBlock(floorBlockEnd)) {
			floorBlockEnd = "WATER";
		}
		
		// bottom blocks
		if (!isValidBlock(bottomBlockOverworld)) {
			bottomBlockOverworld = "BEDROCK";
		}
		if (!isValidBlock(bottomBlockNether)) {
			bottomBlockNether = "BEDROCK";
		}
		if (!isValidBlock(bottomBlockEnd)) {
			bottomBlockEnd = "BEDROCK";
		}
		
		// portal frame blocks
		if (!isValidBlock(starrySkyPortalFrameBlock)) {
			starrySkyPortalFrameBlock = "PACKED_ICE";
		}
		
	}
	
}
