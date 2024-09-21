package de.dafuqs.starryskies.worldgen;

import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.worldgen.decorators.*;
import net.minecraft.registry.*;

public class SphereDecorators {

	public static SphereDecorator<BambooDecoratorConfig> BAMBOO = register("bamboo", new BambooDecorator(BambooDecoratorConfig.CODEC));
	public static SphereDecorator<SingleBlockDecoratorConfig> SINGLE_BLOCK = register("single_block", new SingleBlockDecorator(SingleBlockDecoratorConfig.CODEC));
	public static SphereDecorator<DoubleBlockDecoratorConfig> DOUBLE_BLOCK = register("double_block", new DoubleBlockDecorator(DoubleBlockDecoratorConfig.CODEC));
	public static SphereDecorator<StackedBlockDecoratorConfig> STACKED_BLOCK = register("stacked_block", new StackedBlockDecorator(StackedBlockDecoratorConfig.CODEC));
	public static SphereDecorator<GroundDecoratorConfig> GROUND_BLOCK = register("ground_block", new GroundDecorator(GroundDecoratorConfig.CODEC));
	public static SphereDecorator<CaveBottomDecoratorConfig> CAVE_BOTTOM_BLOCK = register("cave_bottom_block", new CaveBottomDecorator(CaveBottomDecoratorConfig.CODEC));
	public static SphereDecorator<PlantAroundPondDecoratorConfig> PLANT_AROUND_POND = register("plant_around_pond", new PlantAroundPondDecorator(PlantAroundPondDecoratorConfig.CODEC));
	public static SphereDecorator<CenterPondDecoratorConfig> CENTER_POND = register("center_pond", new CenterPondDecorator(CenterPondDecoratorConfig.CODEC));
	public static SphereDecorator<MultifaceGrowthDecoratorConfig> MULTIFACE_GROWTH = register("multiface_growth", new MultifaceGrowthDecorator(MultifaceGrowthDecoratorConfig.CODEC));
	public static SphereDecorator<HangingBlockDecoratorConfig> HANGING_BLOCK = register("hanging_block", new HangingBlockDecorator(HangingBlockDecoratorConfig.CODEC));
	public static SphereDecorator<HangingCaveBlockDecoratorConfig> HANGING_CAVE_BLOCK = register("hanging_cave_block", new HangingCaveBlockDecorator(HangingCaveBlockDecoratorConfig.CODEC));
	public static SphereDecorator<XMarksTheSpotDecoratorConfig> X_SPOT = register("x_spot", new XMarksTheSpotDecorator(XMarksTheSpotDecoratorConfig.CODEC));
	public static SphereDecorator<HugePlantDecoratorConfig> HUGE_PLANT = register("huge_plant", new HugePlantDecorator(HugePlantDecoratorConfig.CODEC));
	public static SphereDecorator<HugePlantDecoratorConfig> HUGE_HANGING_PLANT = register("huge_hanging_plant", new HugePlantDecorator(HugePlantDecoratorConfig.CODEC));
	public static SphereDecorator<DripleafDecoratorConfig> DRIPLEAF = register("dripleaf", new DripleafDecorator(DripleafDecoratorConfig.CODEC));
	public static SphereDecorator<SphereDecoratorConfig.DefaultSphereDecoratorConfig> COCOA = register("cocoa", new CocoaDecorator(SphereDecoratorConfig.DefaultSphereDecoratorConfig.CODEC));
	public static SphereDecorator<SphereDecoratorConfig.DefaultSphereDecoratorConfig> SEA_GREENS = register("sea_greens", new SeaGreensDecorator(SphereDecoratorConfig.DefaultSphereDecoratorConfig.CODEC));
	public static SphereDecorator<RuinedPortalDecoratorConfig> RUINED_PORTAL = register("ruined_portal", new RuinedPortalDecorator(RuinedPortalDecoratorConfig.CODEC));
	public static SphereDecorator<SphereDecoratorConfig.DefaultSphereDecoratorConfig> END_PORTAL = register("end_portal", new EndPortalDecorator(SphereDecoratorConfig.DefaultSphereDecoratorConfig.CODEC));
	public static SphereDecorator<SphereDecoratorConfig.DefaultSphereDecoratorConfig> END_GATEWAY = register("end_gateway", new EndGatewayDecorator(SphereDecoratorConfig.DefaultSphereDecoratorConfig.CODEC));
	public static SphereDecorator<ChorusFruitDecoratorConfig> CHORUS_FRUIT = register("chorus_fruit", new ChorusFruitDecorator(ChorusFruitDecoratorConfig.CODEC));

	public static void initialize() {

	}

	private static <C extends SphereDecoratorConfig, F extends SphereDecorator<C>> F register(String name, F feature) {
		return Registry.register(StarryRegistries.SPHERE_DECORATOR, name, feature);
	}

}
