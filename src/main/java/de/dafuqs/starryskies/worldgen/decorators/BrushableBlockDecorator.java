package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;

public class BrushableBlockDecorator extends SphereDecorator<BrushableBlockDecoratorConfig> {
	
	public BrushableBlockDecorator(Codec<BrushableBlockDecoratorConfig> codec) {
		super(codec);
	}
	
	@Override
	public boolean generate(SphereFeatureContext<BrushableBlockDecoratorConfig> context) {
		StructureWorldAccess world = context.getWorld();
		PlacedSphere<?> sphere = context.getSphere();
		ChunkPos origin = context.getChunkPos();
		Random random = context.getRandom();
		BrushableBlockDecoratorConfig config = context.getConfig();
		
		for (BlockPos bp : getTopBlocks(world, origin, sphere)) {
			if (random.nextFloat() < config.chance()) {
				world.setBlockState(bp, config.state(), 3);
				if (world.getBlockEntity(bp) instanceof BrushableBlockEntity brushableBlockEntity) {
					brushableBlockEntity.setLootTable(config.lootTable(), random.nextLong());
				}
			}
		}
		
		return true;
	}
	
}
