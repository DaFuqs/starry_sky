package de.dafuqs.starryskies.registries;

import com.mojang.serialization.*;
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

public abstract class SpheroidDecorator {

	public static final Codec<SpheroidDecorator> CODEC = StarryRegistries.SPHEROID_DECORATOR.getCodec();
	
	public SpheroidDecorator() {
	}
	
	protected abstract SpheroidDecoratorType<?> getType();
	
	/**
	 * In contrast to vanilla the spheroid decorators are queried by the spheroid
	 * not ran after the chunk generation
	 * The spheroid tracks all blocks that can be decorated and the decorator
	 * takes them, checks for spawning criteria and
	 */
	public abstract void decorate(StructureWorldAccess world, ChunkPos origin, Spheroid spheroid, Random random);
	
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
