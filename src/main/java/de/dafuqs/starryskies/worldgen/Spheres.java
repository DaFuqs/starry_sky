package de.dafuqs.starryskies.worldgen;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.worldgen.spheres.*;
import net.minecraft.registry.*;

public class Spheres {

	public static final Sphere<SimpleSphere.Config> SIMPLE = register("simple", new SimpleSphere(SimpleSphere.Config.CODEC));
	public static final Sphere<ShellSphere.Config> SHELL = register("shell", new ShellSphere(ShellSphere.Config.CODEC));
	public static final Sphere<CoreSphere.Config> CORE = register("core", new CoreSphere(CoreSphere.Config.CODEC));
	public static final Sphere<ModularSphere.Config> MODULAR = register("modular", new ModularSphere(ModularSphere.Config.CODEC));
	public static final Sphere<CaveSphere.Config> CAVE = register("cave", new CaveSphere(CaveSphere.Config.CODEC));
	public static final Sphere<DungeonSphere.Config> DUNGEON = register("dungeon", new DungeonSphere(DungeonSphere.Config.CODEC));
	public static final Sphere<FluidSphere.Config> FLUID = register("fluid", new FluidSphere(FluidSphere.Config.CODEC));
	
	/*
	public static final Sphere<NetherFortressSphere.Config> NETHER_FORTRESS = register("nether_fortress", NetherFortressSphere.Config.CODEC);
	public static final Sphere<GeodeSphere.Config> GEODE = register("geode", GeodeSphere.Config.CODEC);
	public static final Sphere<CoralsSphere.Config> CORALS = register("corals", CoralsSphere.Config.CODEC);
	public static final Sphere<StrongholdSphere.Config> STRONGHOLD = register("stronghold", StrongholdSphere.Config.CODEC);
	public static final Sphere<StackedHorizontalSphere.Config> STACKED_HORIZONTAL = register("stacked_horizontal", StackedHorizontalSphere.Config.CODEC);
	public static final Sphere<EndCitySphere.Config> END_CITY = register("end_city", EndCitySphere.Config.CODEC);
	public static final Sphere<FluidCoreSphere.Config> CORE_FLUID = register("fluid_core", FluidCoreSphere.Config.CODEC);
	public static final Sphere<MushroomSphere.Config> MUSHROOM = register("mushroom", MushroomSphere.Config.CODEC);
	public static final Sphere<ShellCoreSphere.Config> SHELL_CORE = register("shell_core", ShellCoreSphere.Config.CODEC);
	public static final Sphere<BeeHiveSphere.Config> BEE_HIVE = register("bee_hive", BeeHiveSphere.Config.CODEC);
	public static final Sphere<OceanMonumentSphere.Config> OCEAN_MONUMENT = register("ocean_monument", OceanMonumentSphere.Config.CODEC);
	public static final Sphere<RainbowSphere.Config> RAINBOW = register("rainbow", RainbowSphere.Config.CODEC);
	public static final Sphere<ModularRainbowSphere.Config> MODULAR_RAINBOW = register("modular_rainbow", ModularRainbowSphere.Config.CODEC);*/

	private static <C extends SphereConfig, F extends Sphere<C>> F register(String name, F feature) {
		return Registry.register(StarryRegistries.SPHERE, StarrySkies.id(name), feature);
	}

	public static void initialize() {
	}

}
