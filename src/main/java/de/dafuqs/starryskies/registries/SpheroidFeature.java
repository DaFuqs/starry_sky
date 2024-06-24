package de.dafuqs.starryskies.registries;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import de.dafuqs.starryskies.spheroids.decorators.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.fluid.*;
import net.minecraft.loot.*;
import net.minecraft.registry.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.util.*;

public abstract class SpheroidFeature<FC extends SpheroidFeatureConfig> {
	
	public static SpheroidFeature<BambooDecoratorConfig> BAMBOO = register("bamboo", new BambooDecorator(BambooDecoratorConfig.CODEC));
	public static SpheroidFeature<SingleBlockDecoratorConfig> SINGLE_BLOCK = register("single_block", new SingleBlockDecorator(SingleBlockDecoratorConfig.CODEC));
	public static SpheroidFeature<DoubleBlockDecoratorConfig> DOUBLE_BLOCK = register("double_block", new DoubleBlockDecorator(DoubleBlockDecoratorConfig.CODEC));
	public static SpheroidFeature<StackedBlockDecoratorConfig> STACKED_BLOCK = register("stacked_block", new StackedBlockDecorator(StackedBlockDecoratorConfig.CODEC));
	public static SpheroidFeature<GroundDecoratorConfig> GROUND_BLOCK = register("ground_block", new GroundDecorator(GroundDecoratorConfig.CODEC));
	public static SpheroidFeature<CaveBottomDecoratorConfig> CAVE_BOTTOM_BLOCK = register("cave_bottom_block", new CaveBottomDecorator(CaveBottomDecoratorConfig.CODEC));
	public static SpheroidFeature<PlantAroundPondDecoratorConfig> PLANT_AROUND_POND = register("plant_around_pond", new PlantAroundPondDecorator(PlantAroundPondDecoratorConfig.CODEC));
	public static SpheroidFeature<CenterPondDecoratorConfig> CENTER_POND = register("center_pond", new CenterPondDecorator(CenterPondDecoratorConfig.CODEC));
	public static SpheroidFeature<MultifaceGrowthDecoratorConfig> MULTIFACE_GROWTH = register("multiface_growth", new MultifaceGrowthDecorator(MultifaceGrowthDecoratorConfig.CODEC));
	public static SpheroidFeature<HangingBlockDecoratorConfig> HANGING_BLOCK = register("hanging_block", new HangingBlockDecorator(HangingBlockDecoratorConfig.CODEC));
	public static SpheroidFeature<HangingCaveBlockDecoratorConfig> HANGING_CAVE_BLOCK = register("hanging_cave_block", new HangingCaveBlockDecorator(HangingCaveBlockDecoratorConfig.CODEC));
	public static SpheroidFeature<XMarksTheSpotDecoratorConfig> X_SPOT = register("x_spot", new XMarksTheSpotDecorator(XMarksTheSpotDecoratorConfig.CODEC));
	public static SpheroidFeature<HugePlantDecoratorConfig> HUGE_PLANT = register("huge_plant", new HugePlantDecorator(HugePlantDecoratorConfig.CODEC));
	public static SpheroidFeature<HugePlantDecoratorConfig> HUGE_HANGING_PLANT = register("huge_hanging_plant", new HugePlantDecorator(HugePlantDecoratorConfig.CODEC));
	public static SpheroidFeature<DripleafDecoratorConfig> DRIPLEAF = register("dripleaf", new DripleafDecorator(DripleafDecoratorConfig.CODEC));
	public static SpheroidFeature<SpheroidFeatureConfig.DefaultSpheroidFeatureConfig> COCOA = register("cocoa", new CocoaDecorator(SpheroidFeatureConfig.DefaultSpheroidFeatureConfig.CODEC));
	public static SpheroidFeature<SpheroidFeatureConfig.DefaultSpheroidFeatureConfig> SEA_GREENS = register("sea_greens", new SeaGreensDecorator(SpheroidFeatureConfig.DefaultSpheroidFeatureConfig.CODEC));
	public static SpheroidFeature<RuinedPortalDecoratorConfig> RUINED_PORTAL = register("ruined_portal", new RuinedPortalDecorator(RuinedPortalDecoratorConfig.CODEC));
	public static SpheroidFeature<SpheroidFeatureConfig.DefaultSpheroidFeatureConfig> END_PORTAL = register("end_portal", new EndPortalDecorator(SpheroidFeatureConfig.DefaultSpheroidFeatureConfig.CODEC));
	public static SpheroidFeature<SpheroidFeatureConfig.DefaultSpheroidFeatureConfig> END_GATEWAY = register("end_gateway", new EndGatewayDecorator(SpheroidFeatureConfig.DefaultSpheroidFeatureConfig.CODEC));
	public static SpheroidFeature<ChorusFruitDecoratorConfig> CHORUS_FRUIT = register("chorus_fruit", new ChorusFruitDecorator(ChorusFruitDecoratorConfig.CODEC));
	
	private final MapCodec<ConfiguredSpheroidFeature<FC, SpheroidFeature<FC>>> codec;
	
	public SpheroidFeature(Codec<FC> configCodec) {
		this.codec = configCodec.fieldOf("config").xmap((config) -> new ConfiguredSpheroidFeature(this, config), ConfiguredSpheroidFeature::config);
	}
	
	public static void initialize() {
	
	}
	
	private static <C extends SpheroidFeatureConfig, F extends SpheroidFeature<C>> F register(String name, F feature) {
		return Registry.register(StarryRegistries.SPHEROID_FEATURE, name, feature);
	}
	
	public MapCodec<ConfiguredSpheroidFeature<FC, SpheroidFeature<FC>>> getCodec() {
		return this.codec;
	}
	
	public abstract boolean generate(SpheroidFeatureContext<FC> context);
	
	public boolean generateIfValid(FC config, StructureWorldAccess world, Random random, BlockPos pos, Spheroid spheroid) {
		return world.isValidForSetBlock(pos) && this.generate(new SpheroidFeatureContext<>(world, random, new ChunkPos(pos), spheroid, config));
	}
	
	protected void placeLootChest(@NotNull StructureWorldAccess world, BlockPos blockPos, RegistryKey<LootTable> lootTable, Random random) {
		BlockState chestBlockState = Blocks.CHEST.getDefaultState();
		
		// if the chest is placed in water: waterlog it!
		if (world.getBlockState(blockPos) == Blocks.WATER.getDefaultState()) {
			chestBlockState = chestBlockState.with(ChestBlock.WATERLOGGED, true);
		}
		
		// Random direction placement for the chest
		int r = random.nextInt(4);
		Direction randomDirection;
		switch (r) {
			case 0 -> randomDirection = Direction.NORTH;
			case 1 -> randomDirection = Direction.SOUTH;
			case 2 -> randomDirection = Direction.EAST;
			default -> randomDirection = Direction.WEST;
		}
		
		// set the chest and add loot table
		world.setBlockState(blockPos, chestBlockState.with(ChestBlock.FACING, randomDirection), 3);
		BlockEntity chestBlockEntity = world.getBlockEntity(blockPos);
		if (chestBlockEntity instanceof ChestBlockEntity) {
			((ChestBlockEntity) chestBlockEntity).setLootTable(lootTable, random.nextLong());
		}
	}
	
	protected BlockPos findNextNonAirBlockInDirection(StructureWorldAccess world, BlockPos blockPos, Direction direction, int maxBlocks) {
		for (int i = 0; i < maxBlocks; i++) {
			if (!world.getBlockState(blockPos.offset(direction, i)).isAir()) {
				return blockPos;
			}
		}
		return null;
	}
	
	protected List<BlockPos> getTopBlocks(StructureWorldAccess world, ChunkPos chunkPos, Spheroid spheroid) {
		List<BlockPos> list = new ArrayList<>();
		
		int x = spheroid.getPosition().getX();
		int y = spheroid.getPosition().getY();
		int z = spheroid.getPosition().getZ();
		
		int rad = spheroid.getRadius();
		int maxX = Math.min(chunkPos.getEndX(), x + rad);
		int maxZ = Math.min(chunkPos.getEndZ(), z + rad);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		for (int x2 = Math.max(chunkPos.getStartX(), x - rad); x2 <= maxX; x2++) {
			for (int z2 = Math.max(chunkPos.getStartZ(), z - rad); z2 <= maxZ; z2++) {
				for (int y2 = y + rad; y2 > y; y2--) {
					mutable.set(x2, y2, z2);
					if (!world.getBlockState(mutable).isAir()) {
						list.add(mutable.toImmutable());
						break;
					}
				}
			}
		}
		return list;
	}
	
	protected List<BlockPos> getBottomBlocks(StructureWorldAccess world, ChunkPos chunkPos, Spheroid spheroid) {
		List<BlockPos> list = new ArrayList<>();
		
		int x = spheroid.getPosition().getX();
		int y = spheroid.getPosition().getY();
		int z = spheroid.getPosition().getZ();
		
		int rad = spheroid.getRadius();
		int maxX = Math.min(chunkPos.getEndX(), x + rad);
		int maxZ = Math.min(chunkPos.getEndZ(), z + rad);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		for (int x2 = Math.max(chunkPos.getStartX(), x - rad); x2 <= maxX; x2++) {
			for (int z2 = Math.max(chunkPos.getStartZ(), z - rad); z2 <= maxZ; z2++) {
				for (int y2 = y - rad; y2 < y; y2++) {
					mutable.set(x2, y2, z2);
					if (!world.getBlockState(mutable).isAir()) {
						list.add(mutable.toImmutable());
						break;
					}
				}
			}
		}
		return list;
	}
	
	protected List<BlockPos> getTopBlocks(StructureWorldAccess world, ChunkPos chunkPos, Spheroid spheroid, Random random, int amount) {
		List<BlockPos> list = new ArrayList<>();
		
		int x = spheroid.getPosition().getX();
		int y = spheroid.getPosition().getY();
		int z = spheroid.getPosition().getZ();
		
		int rad = spheroid.getRadius();
		int minX = Math.max(chunkPos.getStartX(), x - rad);
		int minZ = Math.max(chunkPos.getStartZ(), z - rad);
		int maxX = Math.min(chunkPos.getEndX(), x + rad);
		int maxZ = Math.min(chunkPos.getEndZ(), z + rad);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		
		for (int i = 0; i < amount; i++) {
			int x2 = minX + random.nextInt(maxX - minX + 1);
			int z2 = minZ + random.nextInt(maxZ - minZ + 1);
			for (int y2 = y + rad; y2 > y; y2--) {
				mutable.set(x2, y2, z2);
				if (!world.getBlockState(mutable).isAir()) {
					list.add(mutable.toImmutable());
					break;
				}
			}
		}
		
		return list;
	}
	
	protected List<BlockPos> getCaveBottomBlocks(StructureWorldAccess world, ChunkPos chunkPos, Spheroid spheroid) {
		List<BlockPos> list = new ArrayList<>();
		
		int x = spheroid.getPosition().getX();
		int y = spheroid.getPosition().getY();
		int z = spheroid.getPosition().getZ();
		
		int rad = spheroid.getRadius();
		int maxX = Math.min(chunkPos.getEndX(), x + rad);
		int maxZ = Math.min(chunkPos.getEndZ(), z + rad);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		for (int x2 = Math.max(chunkPos.getStartX(), x - rad); x2 <= maxX; x2++) {
			for (int z2 = Math.max(chunkPos.getStartZ(), z - rad); z2 <= maxZ; z2++) {
				boolean hitShell = false;
				for (int y2 = y - rad; y2 < y; y2++) {
					mutable.set(x2, y2, z2);
					BlockState state = world.getBlockState(mutable);
					boolean airOrFluid = state.isAir() || state.getFluidState().getFluid() != Fluids.EMPTY;
					if (airOrFluid && !hitShell) {
					
					} else if (!airOrFluid) {
						hitShell = true;
					} else {
						list.add(mutable.down().toImmutable());
						break;
					}
				}
			}
		}
		
		return list;
	}
	
	protected List<BlockPos> getCaveBottomBlocks(StructureWorldAccess world, ChunkPos chunkPos, Spheroid spheroid, Random random, int amount) {
		List<BlockPos> list = new ArrayList<>();
		
		int x = spheroid.getPosition().getX();
		int y = spheroid.getPosition().getY();
		int z = spheroid.getPosition().getZ();
		
		int rad = spheroid.getRadius();
		int minX = Math.max(chunkPos.getStartX(), x - rad);
		int minZ = Math.max(chunkPos.getStartZ(), z - rad);
		int maxX = Math.min(chunkPos.getEndX(), x + rad);
		int maxZ = Math.min(chunkPos.getEndZ(), z + rad);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		
		for (int i = 0; i < amount; i++) {
			int x2 = minX + random.nextInt(maxX - minX + 1);
			int z2 = minZ + random.nextInt(maxZ - minZ + 1);
			boolean hitShell = false;
			for (int y2 = y - rad; y2 < y; y2++) {
				mutable.set(x2, y2, z2);
				BlockState state = world.getBlockState(mutable);
				boolean airOrFluid = state.isAir() || state.getFluidState().getFluid() != Fluids.EMPTY;
				if (airOrFluid && !hitShell) {
				
				} else if (!airOrFluid) {
					hitShell = true;
				} else {
					list.add(mutable.down().toImmutable());
					break;
				}
			}
		}
		return list;
	}
	
	
}
