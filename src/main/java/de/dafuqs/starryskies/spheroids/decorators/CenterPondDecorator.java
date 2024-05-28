package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.loot.*;
import net.minecraft.registry.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

import static de.dafuqs.starryskies.Support.BLOCKSTATE_STRING_CODEC;

public class CenterPondDecorator extends SpheroidDecorator {

	public static final MapCodec<CenterPondDecorator> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					BLOCKSTATE_STRING_CODEC.fieldOf("beach_block").forGetter(decorator -> decorator.beachBlock),
					BLOCKSTATE_STRING_CODEC.fieldOf("fluid_block").forGetter(decorator -> decorator.fluidBlock),
					RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).fieldOf("loot_table").forGetter(decorator -> decorator.lootTable),
					Codec.FLOAT.fieldOf("loot_table_chance").forGetter(decorator -> decorator.lootTableChance)
			).apply(instance, CenterPondDecorator::new)
	);
	
	private final RegistryKey<LootTable> lootTable;
	private final float lootTableChance;
	private final BlockState beachBlock;
	private final BlockState fluidBlock;

	public CenterPondDecorator(BlockState beachBlock, BlockState fluidBlock,
							   RegistryKey<LootTable> lootTable, float lootTableChance) {
		this.beachBlock = beachBlock;
		this.fluidBlock = fluidBlock;
		this.lootTable = lootTable;
		this.lootTableChance = lootTableChance;
	}

	@Override
	protected SpheroidDecoratorType<CenterPondDecorator> getType() {
		return SpheroidDecoratorType.CENTER_POND;
	}

	@Override
	public void decorate(StructureWorldAccess world, ChunkPos origin, Spheroid spheroid, Random random) {
		if (!spheroid.isCenterInChunk(origin)) {
			return;
		}
		
		// doesn't make sense on small spheroids
		if (spheroid.getRadius() > 9) {
			int pondRadius = (int) (spheroid.getRadius() / 2.5);
			BlockPos spheroidTop = spheroid.getPosition().up(spheroid.getRadius());
			
			int waterLevelY = spheroidTop.getY();
			boolean waterLevelSet = false;
			
			for (int x = -pondRadius - 1; x <= pondRadius; x++) {
				for (int y = -pondRadius; y < 1; y++) {
					for (int z = -pondRadius - 1; z <= pondRadius; z++) {
						BlockPos currentBlockPos = spheroidTop.add(x, y, z);
						if (world.getBlockState(currentBlockPos).isAir()) {
							waterLevelY = currentBlockPos.getY() - 1;
							waterLevelSet = true;
							break;
						}
					}
					if (waterLevelSet) {
						break;
					}
				}
				if (waterLevelSet) {
					break;
				}
			}
			
			// if there is not enough room for water: just cancel
			// not nice, but eh
			if (waterLevelY - spheroid.getPosition().getY() < pondRadius * 1.5) {
				return;
			}
			
			boolean hasLootChest = random.nextFloat() < this.lootTableChance;
			BlockPos lootChestPosition = null;
			
			int pond15 = (int) Math.round(pondRadius * 1.5);
			for (int x = -pond15; x <= pond15; x++) {
				for (int y = -pondRadius; y < pondRadius; y++) {
					for (int z = -pond15; z <= pond15; z++) {
						BlockPos currentBlockPos = spheroidTop.add(x, y, z);
						
						BlockState blockState = null;
						if (currentBlockPos.getY() > waterLevelY) {
							blockState = Blocks.AIR.getDefaultState();
						} else {
							double distance = Support.getDistance(currentBlockPos, spheroidTop);
							double pondDistance = distance / pondRadius;
							if (pondDistance < 1.1) {
								if (hasLootChest && x == 0 && z == 0 && lootChestPosition == null) {
									lootChestPosition = currentBlockPos;
								}
								blockState = fluidBlock;
							} else if (pondDistance < 1.70) {
								blockState = beachBlock;
							}
						}
						
						if (blockState != null) {
							if (!world.getBlockState(currentBlockPos).isAir()) {
								world.setBlockState(currentBlockPos, blockState, 3);
							}
						}
						
					}
				}
			}
			
			if (lootChestPosition != null) {
				placeLootChest(world, lootChestPosition, lootTable, random);
			}
		}
	}
	
}
