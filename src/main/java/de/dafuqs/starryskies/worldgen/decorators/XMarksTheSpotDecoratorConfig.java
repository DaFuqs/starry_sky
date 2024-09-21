package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;
import net.minecraft.loot.*;
import net.minecraft.registry.*;

import static de.dafuqs.starryskies.Support.*;

public record XMarksTheSpotDecoratorConfig(RegistryKey<LootTable> lootTable,
										   BlockState markingState) implements SphereDecoratorConfig {

	public static final Codec<XMarksTheSpotDecoratorConfig> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).fieldOf("loot_table").forGetter(decorator -> decorator.lootTable),
					BLOCKSTATE_STRING_CODEC.fieldOf("marking_block").forGetter(decorator -> decorator.markingState)
			).apply(instance, XMarksTheSpotDecoratorConfig::new)
	);

}
