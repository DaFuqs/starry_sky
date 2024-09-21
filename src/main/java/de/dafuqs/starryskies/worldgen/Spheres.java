package de.dafuqs.starryskies.worldgen;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.worldgen.spheres.*;
import net.minecraft.registry.*;
import org.jetbrains.annotations.*;

public class Spheres<P extends ConfiguredSphere<?>> {

	public static final Spheres<SimpleSphere.Template> SIMPLE = register("simple", SimpleSphere.Template.CODEC);
	public static final Spheres<DungeonSphere.Template> DUNGEON = register("dungeon", DungeonSphere.Template.CODEC);
	public static final Spheres<CaveSphere.Template> CAVE = register("cave", CaveSphere.Template.CODEC);
	public static final Spheres<ModularSphere.Template> MODULAR = register("modular", ModularSphere.Template.CODEC);
	public static final Spheres<CoreSphere.Template> CORE = register("core", CoreSphere.Template.CODEC);
	public static final Spheres<NetherFortressSphere.Template> NETHER_FORTRESS = register("nether_fortress", NetherFortressSphere.Template.CODEC);
	public static final Spheres<GeodeSphere.Template> GEODE = register("geode", GeodeSphere.Template.CODEC);
	public static final Spheres<CoralsSphere.Template> CORALS = register("corals", CoralsSphere.Template.CODEC);
	public static final Spheres<StrongholdSphere.Template> STRONGHOLD = register("stronghold", StrongholdSphere.Template.CODEC);
	public static final Spheres<StackedHorizontalSphere.Template> STACKED_HORIZONTAL = register("stacked_horizontal", StackedHorizontalSphere.Template.CODEC);
	public static final Spheres<EndCitySphere.Template> END_CITY = register("end_city", EndCitySphere.Template.CODEC);
	public static final Spheres<ShellSphere.Template> SHELL = register("shell", ShellSphere.Template.CODEC);
	public static final Spheres<FluidSphere.Template> FLUID = register("fluid", FluidSphere.Template.CODEC);
	public static final Spheres<FluidCoreSphere.Template> CORE_FLUID = register("fluid_core", FluidCoreSphere.Template.CODEC);
	public static final Spheres<MushroomSphere.Template> MUSHROOM = register("mushroom", MushroomSphere.Template.CODEC);
	public static final Spheres<ShellCoreSphere.Template> SHELL_CORE = register("shell_core", ShellCoreSphere.Template.CODEC);
	public static final Spheres<BeeHiveSphere.Template> BEE_HIVE = register("bee_hive", BeeHiveSphere.Template.CODEC);
	public static final Spheres<OceanMonumentSphere.Template> OCEAN_MONUMENT = register("ocean_monument", OceanMonumentSphere.Template.CODEC);
	public static final Spheres<RainbowSphere.Template> RAINBOW = register("rainbow", RainbowSphere.Template.CODEC);
	public static final Spheres<ModularRainbowSphere.Template> MODULAR_RAINBOW = register("modular_rainbow", ModularRainbowSphere.Template.CODEC);

	private final MapCodec<P> codec;

	public Spheres(MapCodec<P> codec) {
		this.codec = codec;
	}

	private static <P extends ConfiguredSphere<?>> Spheres<P> register(String id, MapCodec<P> codec) {
		return Registry.register(StarryRegistries.SPHERE, StarrySkies.locate(id), new Spheres<>(codec));
	}

	public static void initialize() {
	}

	public @NotNull MapCodec<P> getCodec() {
		return this.codec;
	}

}
