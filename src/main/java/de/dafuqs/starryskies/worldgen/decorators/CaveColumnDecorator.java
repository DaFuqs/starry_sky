package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;


public class CaveColumnDecorator extends SphereDecorator<CaveColumnDecoratorConfig> {
	
	public CaveColumnDecorator(Codec<CaveColumnDecoratorConfig> codec) {
		super(codec);
	}
	
	@Override
	public boolean generate(SphereFeatureContext<CaveColumnDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		PlacedSphere<?> sphere = context.getSphere();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		CaveColumnDecoratorConfig config = context.getConfig();
		
		if (!sphere.isCenterInChunk(origin)) {
			return false;
		}
		
		BlockPos spherePos = sphere.getPosition();
		int sphereY = spherePos.getY();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		
		world.setBlockState(spherePos, config.centerState().get(random, mutable), Block.NOTIFY_ALL);
		
		mutable.set(spherePos.getX(), spherePos.getY() + 1, spherePos.getZ());
		int maxY = findNextNonAirBlockInDirection(world, mutable, Direction.UP, sphere.getRadius()).getY();
		for (int y = sphereY + 1; y < maxY; y++) {
			mutable.set(spherePos.getX(), y, spherePos.getZ());
			world.setBlockState(mutable, config.columnState().get(random, mutable), Block.NOTIFY_ALL);
		}
		
		mutable.set(spherePos.getX(), spherePos.getY() - 1, spherePos.getZ());
		int minY = findNextNonAirBlockInDirection(world, mutable, Direction.DOWN, sphere.getRadius()).getY();
		for (int y = sphereY - 1; y > minY; y--) {
			mutable.set(spherePos.getX(), y, spherePos.getZ());
			world.setBlockState(mutable, config.columnState().get(random, mutable), Block.NOTIFY_ALL);
		}
		
		return true;
	}
	
}
