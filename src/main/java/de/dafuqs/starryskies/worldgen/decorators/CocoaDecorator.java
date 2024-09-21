package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class CocoaDecorator extends SphereDecorator<SphereDecoratorConfig.DefaultSphereDecoratorConfig> {

	private final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();
	private final BlockState COCOA = Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, 2); // 2 = fully grown

	public CocoaDecorator(Codec<SphereDecoratorConfig.DefaultSphereDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(SphereFeatureContext<SphereDecoratorConfig.DefaultSphereDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		PlacedSphere sphere = context.getSpheroid();
		ChunkPos origin = context.getChunkPos();

		if (!sphere.isCenterInChunk(origin)) {
			return false;
		}

		for (int x = -2; x < 3; x++) {
			for (int y = -2; y < 3; y++) {
				for (int z = -2; z < 3; z++) {
					BlockPos bp = sphere.getPosition().up(y).north(x).east(z);
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

		return true;
	}

}
