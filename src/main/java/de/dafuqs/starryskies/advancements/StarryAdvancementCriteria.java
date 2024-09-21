package de.dafuqs.starryskies.advancements;

import de.dafuqs.starryskies.*;
import net.minecraft.advancement.criterion.*;

public class StarryAdvancementCriteria {

	public static SphereDiscoveredCriterion SPHERE_DISCOVERED;

	public static void register() {
		SPHERE_DISCOVERED = Criteria.register(StarrySkies.locatePlain("sphere_discovered"), new SphereDiscoveredCriterion());
	}

}