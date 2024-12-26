package de.dafuqs.starryskies.advancements;

import de.dafuqs.starryskies.*;
import net.minecraft.advancement.criterion.*;
import net.minecraft.registry.*;

public class StarryAdvancementCriteria {

	public static SphereDiscoveredCriterion SPHERE_DISCOVERED;

	public static void register() {
		SPHERE_DISCOVERED = Registry.register(Registries.CRITERION, StarrySkies.idPlain("sphere_discovered"), new SphereDiscoveredCriterion());
	}

}