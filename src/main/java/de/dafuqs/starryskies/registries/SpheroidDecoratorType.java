package de.dafuqs.starryskies.registries;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.spheroids.decorators.*;
import net.minecraft.registry.*;

public class SpheroidDecoratorType<P extends SpheroidDecorator> {
	
	public static final SpheroidDecoratorType<SingleBlockDecorator> SINGLE_BLOCK = register("single_block", SingleBlockDecorator.CODEC);
	public static SpheroidDecoratorType<DoubleBlockDecorator> DOUBLE_BLOCK = register("double_block", DoubleBlockDecorator.CODEC);
	public static SpheroidDecoratorType<StackedBlockDecorator> STACKED_BLOCK = register("stacked_block", StackedBlockDecorator.CODEC);
	public static SpheroidDecoratorType<GroundDecorator> GROUND_BLOCK = register("ground_block", GroundDecorator.CODEC);
	public static SpheroidDecoratorType<CaveBottomDecorator> CAVE_BOTTOM_BLOCK = register("cave_bottom_block", CaveBottomDecorator.CODEC);
	public static SpheroidDecoratorType<PlantAroundPondDecorator> PLANT_AROUND_POND = register("plant_around_pond", PlantAroundPondDecorator.CODEC);
	public static SpheroidDecoratorType<CenterPondDecorator> CENTER_POND = register("center_pond", CenterPondDecorator.CODEC);
	public static SpheroidDecoratorType<MultifaceGrowthDecorator> MULTIFACE_GROWTH = register("multiface_growth", MultifaceGrowthDecorator.CODEC);
	public static SpheroidDecoratorType<HangingBlockDecorator> HANGING_BLOCK = register("hanging_block", HangingBlockDecorator.CODEC);
	public static SpheroidDecoratorType<HangingCaveBlockDecorator> HANGING_CAVE_BLOCK = register("hanging_cave_block", HangingCaveBlockDecorator.CODEC);
	public static SpheroidDecoratorType<XMarksTheSpotDecorator> X_SPOT = register("x_spot", XMarksTheSpotDecorator.CODEC);
	public static SpheroidDecoratorType<HugePlantDecorator> HUGE_PLANT = register("huge_plant", HugePlantDecorator.CODEC);
	public static SpheroidDecoratorType<HugeHangingPlantDecorator> HUGE_HANGING_PLANT = register("huge_hanging_plant", HugeHangingPlantDecorator.CODEC);
	public static SpheroidDecoratorType<DripleafDecorator> DRIPLEAF = register("dripleaf", DripleafDecorator.CODEC);
	public static SpheroidDecoratorType<CocoaDecorator> COCOA = register("cocoa", CocoaDecorator.CODEC);
	public static SpheroidDecoratorType<BambooDecorator> BAMBOO = register("bamboo", BambooDecorator.CODEC);
	public static SpheroidDecoratorType<SeaGreensDecorator> SEA_GREENS = register("sea_greens", SeaGreensDecorator.CODEC);
	public static SpheroidDecoratorType<RuinedPortalDecorator> RUINED_PORTAL = register("ruined_portal", RuinedPortalDecorator.CODEC);
	public static SpheroidDecoratorType<EndPortalDecorator> END_PORTAL = register("end_portal", EndPortalDecorator.CODEC);
	public static SpheroidDecoratorType<EndGatewayDecorator> END_GATEWAY = register("end_gateway", EndGatewayDecorator.CODEC);
	public static SpheroidDecoratorType<ChorusFruitDecorator> CHORUS_FRUIT = register("chorus_fruit", ChorusFruitDecorator.CODEC);
	
	private final MapCodec<P> codec;
	
	public SpheroidDecoratorType(MapCodec<P> codec) {
		this.codec = codec;
	}
	
	public MapCodec<P> getCodec() {
		return this.codec;
	}
	
	private static <P extends SpheroidDecorator> SpheroidDecoratorType<P> register(String id, MapCodec<P> codec) {
		return Registry.register(StarryRegistries.SPHEROID_DECORATOR_TYPE, StarrySkies.locate(id), new SpheroidDecoratorType<>(codec));
	}

	public static void initialize() {}
	
}
