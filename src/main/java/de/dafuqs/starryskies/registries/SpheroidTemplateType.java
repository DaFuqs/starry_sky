package de.dafuqs.starryskies.registries;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.registry.*;
import org.jetbrains.annotations.NotNull;

public class SpheroidTemplateType<P extends Spheroid.Template<?>> {

	public static final SpheroidTemplateType<SimpleSpheroid.Template> SIMPLE = register("simple", SimpleSpheroid.Template.CODEC);
	public static final SpheroidTemplateType<DungeonSpheroid.Template> DUNGEON = register("dungeon", DungeonSpheroid.Template.CODEC);
	public static final SpheroidTemplateType<CaveSpheroid.Template> CAVE = register("cave", CaveSpheroid.Template.CODEC);
	public static final SpheroidTemplateType<ModularSpheroid.Template> MODULAR = register("modular", ModularSpheroid.Template.CODEC);
	public static final SpheroidTemplateType<CoreSpheroid.Template> CORE = register("core", CoreSpheroid.Template.CODEC);
	public static final SpheroidTemplateType<NetherFortressSpheroid.Template> NETHER_FORTRESS = register("nether_fortress", NetherFortressSpheroid.Template.CODEC);
	public static final SpheroidTemplateType<GeodeSpheroid.Template> GEODE = register("geode", GeodeSpheroid.Template.CODEC);
	public static final SpheroidTemplateType<CoralsSpheroid.Template> CORALS = register("corals", CoralsSpheroid.Template.CODEC);
	public static final SpheroidTemplateType<StrongholdSpheroid.Template> STRONGHOLD = register("stronghold", StrongholdSpheroid.Template.CODEC);
	public static final SpheroidTemplateType<StackedHorizontalSpheroid.Template> STACKED_HORIZONTAL = register("stacked_horizontal", StackedHorizontalSpheroid.Template.CODEC);
	public static final SpheroidTemplateType<EndCitySpheroid.Template> END_CITY = register("end_city", EndCitySpheroid.Template.CODEC);
	public static final SpheroidTemplateType<ShellSpheroid.Template> SHELL = register("shell", ShellSpheroid.Template.CODEC);
	public static final SpheroidTemplateType<FluidSpheroid.Template> FLUID = register("fluid", FluidSpheroid.Template.CODEC);
	public static final SpheroidTemplateType<FluidCoreSpheroid.Template> CORE_FLUID = register("fluid_core", FluidCoreSpheroid.Template.CODEC);
	public static final SpheroidTemplateType<MushroomSpheroid.Template> MUSHROOM = register("mushroom", MushroomSpheroid.Template.CODEC);
	public static final SpheroidTemplateType<ShellCoreSpheroid.Template> SHELL_CORE = register("shell_core", ShellCoreSpheroid.Template.CODEC);
	public static final SpheroidTemplateType<BeeHiveSpheroid.Template> BEE_HIVE = register("bee_hive", BeeHiveSpheroid.Template.CODEC);
	public static final SpheroidTemplateType<OceanMonumentSpheroid.Template> OCEAN_MONUMENT = register("ocean_monument", OceanMonumentSpheroid.Template.CODEC);
	public static final SpheroidTemplateType<RainbowSpheroid.Template> RAINBOW = register("rainbow", RainbowSpheroid.Template.CODEC);
	public static final SpheroidTemplateType<ModularRainbowSpheroid.Template> MODULAR_RAINBOW = register("modular_rainbow", ModularRainbowSpheroid.Template.CODEC);
	
	private final MapCodec<P> codec;
	
	public SpheroidTemplateType(MapCodec<P> codec) {
		this.codec = codec;
	}
	
	public @NotNull MapCodec<P> getCodec() {
		return this.codec;
	}
	
	private static <P extends Spheroid.Template<?>> SpheroidTemplateType<P> register(String id, MapCodec<P> codec) {
		return Registry.register(StarryRegistries.SPHEROID_TEMPLATE_TYPE, StarrySkies.locate(id), new SpheroidTemplateType<>(codec));
	}

	public static void initialize() {}
	
}
