package de.dafuqs.starryskies.configs;

import me.shedaniel.autoconfig.*;
import me.shedaniel.autoconfig.annotation.*;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.*;

@Config(name = "StarrySky")
public class StarrySkyConfig implements ConfigData {

	@ConfigEntry.Category("GENERAL")
	@Comment(value = """
			The amount of chunks each sphere system spans.
			Higher values make spheres spread out farther, having more air in between
			Default: 50""")
	public int systemSizeChunks = 50;

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
			Default: 2""")
	public int sphereCommandRequiredPermissionLevel = 2;

}
