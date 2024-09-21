package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;

import java.util.*;

import static de.dafuqs.starryskies.Support.*;

public class HugePlantDecoratorConfig implements SphereDecoratorConfig {

	public static final Codec<HugePlantDecoratorConfig> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					BLOCKSTATE_STRING_CODEC.fieldOf("block").forGetter(decorator -> decorator.block),
					BLOCKSTATE_STRING_CODEC.lenientOptionalFieldOf("first_block").forGetter(decorator -> Optional.ofNullable(decorator.firstBlock)),
					BLOCKSTATE_STRING_CODEC.lenientOptionalFieldOf("last_block").forGetter(decorator -> Optional.ofNullable(decorator.lastBlock)),
					Codec.FLOAT.fieldOf("chance").forGetter(decorator -> decorator.chance),
					Codec.INT.fieldOf("min_height").forGetter(decorator -> decorator.minHeight),
					Codec.INT.fieldOf("max_height").forGetter(decorator -> decorator.maxHeight)
			).apply(instance, HugePlantDecoratorConfig::new)
	);

	public final BlockState block;
	public final BlockState firstBlock;
	public final BlockState lastBlock;
	public final float chance;
	public final int minHeight;
	public final int maxHeight;

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public HugePlantDecoratorConfig(BlockState block, Optional<BlockState> firstBlock, Optional<BlockState> lastBlock, float chance, int minHeight, int maxHeight) {
		this.block = block;
		this.firstBlock = firstBlock.orElse(null);
		this.lastBlock = lastBlock.orElse(null);
		this.chance = chance;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
	}
}
