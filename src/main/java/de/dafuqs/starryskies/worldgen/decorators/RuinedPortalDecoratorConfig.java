package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.loot.*;
import net.minecraft.registry.*;

public record RuinedPortalDecoratorConfig(RegistryKey<LootTable> lootTable) implements SphereDecoratorConfig {

	public static final Codec<RuinedPortalDecoratorConfig> CODEC = RecordCodecBuilder.create((instance) ->
			instance.group(
					RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).fieldOf("loot_table").forGetter(config -> config.lootTable)
			).apply(instance, RuinedPortalDecoratorConfig::new));

}
