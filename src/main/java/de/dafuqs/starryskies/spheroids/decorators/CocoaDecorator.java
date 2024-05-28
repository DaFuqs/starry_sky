package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.MapCodec;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class CocoaDecorator extends SpheroidDecorator {

	public static final MapCodec<CocoaDecorator> CODEC = MapCodec.unit(CocoaDecorator::new);
	
	private final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();
	private final BlockState COCOA;
	
	public CocoaDecorator() {
		super();
		this.COCOA = Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, 2); // 2 = fully grown
	}

	@Override
	protected SpheroidDecoratorType<CocoaDecorator> getType() {
		return SpheroidDecoratorType.COCOA;
	}

	@Override
	public void decorate(StructureWorldAccess world, ChunkPos origin, Spheroid spheroid, Random random) {
		if (!spheroid.isCenterInChunk(origin)) {
			return;
		}
		
		for (int x = -2; x < 3; x++) {
			for (int y = -2; y < 3; y++) {
				for (int z = -2; z < 3; z++) {
					BlockPos bp = spheroid.getPosition().up(y).north(x).east(z);
					if (y == 0 && ((Math.abs(x) == 2) && Math.abs(z) == 1 || (Math.abs(z) == 2 && Math.abs(x) == 1))) {
						Direction direction;
						if (x == 0) {
							if (z < 0) {
								direction = Direction.WEST;
							} else {
								direction = Direction.EAST;
							}
							
						} else {
							if (x < 0) {
								direction = Direction.SOUTH;
							} else {
								direction = Direction.NORTH;
							}
						}
						world.setBlockState(bp, COCOA.with(HorizontalFacingBlock.FACING, direction), 3);
					} else {
						if (Math.abs(y) != 2 || (Math.abs(x) != 2 && Math.abs(z) != 2)) {
							world.setBlockState(bp, AIR, 3);
						}
					}
				}
			}
		}
	}
	
}
