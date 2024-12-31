package de.dafuqs.starryskies.worldgen;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.worldgen.spheres.*;
import net.minecraft.registry.*;

public class Spheres {
	
	// Highly configurable
	public static final Sphere<SimpleSphere.Config> SIMPLE = register("simple", new SimpleSphere(SimpleSphere.Config.CODEC));
	public static final Sphere<ShellSphere.Config> SHELL = register("shell", new ShellSphere(ShellSphere.Config.CODEC));
	public static final Sphere<CoreSphere.Config> CORE = register("core", new CoreSphere(CoreSphere.Config.CODEC));
	public static final Sphere<ModularSphere.Config> MODULAR = register("modular", new ModularSphere(ModularSphere.Config.CODEC));
	public static final Sphere<CaveSphere.Config> CAVE = register("cave", new CaveSphere(CaveSphere.Config.CODEC));
	public static final Sphere<DungeonSphere.Config> DUNGEON = register("dungeon", new DungeonSphere(DungeonSphere.Config.CODEC));
	public static final Sphere<FluidSphere.Config> FLUID = register("fluid", new FluidSphere(FluidSphere.Config.CODEC));
	public static final Sphere<FluidCoreSphere.Config> FLUID_CORE = register("fluid_core", new FluidCoreSphere(FluidCoreSphere.Config.CODEC));
	public static final Sphere<ShellCoreSphere.Config> SHELL_CORE = register("shell_core", new ShellCoreSphere(ShellCoreSphere.Config.CODEC));
	public static final Sphere<MushroomSphere.Config> MUSHROOM = register("mushroom", new MushroomSphere(MushroomSphere.Config.CODEC));
	public static final Sphere<HorizontalStackedSphere.Config> HORIZONTAL_STACKED = register("horizontal_stacked", new HorizontalStackedSphere(HorizontalStackedSphere.Config.CODEC));
	public static final Sphere<StructureInteriorSphere.Config> STRUCTURE_INTERIOR = register("structure_interior", new StructureInteriorSphere(StructureInteriorSphere.Config.CODEC));
	
	// Kind of specialized ones
	public static final Sphere<GeodeSphere.Config> GEODE = register("geode", new GeodeSphere(GeodeSphere.Config.CODEC));
	public static final Sphere<CoralsSphere.Config> CORALS = register("corals", new CoralsSphere(CoralsSphere.Config.CODEC));
	public static final Sphere<BeeHiveSphere.Config> BEE_HIVE = register("bee_hive", new BeeHiveSphere(BeeHiveSphere.Config.CODEC));
	public static final Sphere<OceanMonumentSphere.Config> OCEAN_MONUMENT = register("ocean_monument", new OceanMonumentSphere(OceanMonumentSphere.Config.CODEC));
	
	private static <C extends SphereConfig, F extends Sphere<C>> F register(String name, F feature) {
		return Registry.register(StarryRegistries.SPHERE, StarrySkies.id(name), feature);
	}

	public static void initialize() {
	}

}
