package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.loot.*;
import net.minecraft.registry.*;

public record BrushableBlockDecoratorConfig(BlockState state, RegistryKey<LootTable> lootTable,
											float chance) implements SphereDecoratorConfig {
	
	public static final Codec<BrushableBlockDecoratorConfig> CODEC = RecordCodecBuilder.create((instance) ->
			instance.group(
					BlockState.CODEC.fieldOf("block").forGetter(decorator -> decorator.state),
					RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).fieldOf("loot_table").forGetter((t) -> t.lootTable),
					Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(decorator -> decorator.chance)
			).apply(instance, BrushableBlockDecoratorConfig::new));

}
