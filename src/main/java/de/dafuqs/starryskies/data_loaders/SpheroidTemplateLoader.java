package de.dafuqs.starryskies.data_loaders;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.fabricmc.fabric.api.resource.*;
import net.minecraft.registry.*;
import net.minecraft.resource.*;
import net.minecraft.util.*;
import net.minecraft.util.math.random.*;
import net.minecraft.util.profiler.*;

import java.util.*;

public class SpheroidTemplateLoader extends JsonDataLoader implements IdentifiableResourceReloadListener {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	
	public static final String ID = "starry_skies/spheres";
	public static final SpheroidTemplateLoader INSTANCE = new SpheroidTemplateLoader();
	
	private static LinkedHashMap<Identifier, LinkedHashMap<Spheroid.Template<?>, Float>> WEIGHTED_SPHEROID_TYPES;
	
	protected SpheroidTemplateLoader() {
		super(GSON, ID);
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		// initialize list with empty LinkedHashMaps
		StarryRegistries.SYSTEM_GENERATOR.stream().forEach(systemGenerator -> {
			WEIGHTED_SPHEROID_TYPES = new LinkedHashMap<>();
			for (Identifier spheroidDistributionType : systemGenerator.getGenerationGroups()) {
				WEIGHTED_SPHEROID_TYPES.put(spheroidDistributionType, new LinkedHashMap<>());
			}
		});

		final RegistryOps<JsonElement> ops = StarrySkies.registryManager.getOps(JsonOps.INSTANCE);
		StarryRegistries.SPHEROID_TEMPLATE.reset();
		prepared.forEach((identifier, jsonElement) -> {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			
			Spheroid.Template<?> template;
			Identifier spheroidType;
			try {
				spheroidType = Identifier.tryParse(JsonHelper.getString(jsonObject, "type"));
				
				try {
					SpheroidTemplateType<?> templateClass = StarryRegistries.SPHEROID_TEMPLATE_TYPE.get(spheroidType);
                    template = templateClass.getCodec().codec().parse(ops, jsonObject).getOrThrow();
					Registry.register(StarryRegistries.SPHEROID_TEMPLATE, identifier, template);
				} catch (NullPointerException e) {
					if (StarrySkies.CONFIG.packCreatorMode) {
                        StarrySkies.LOGGER.warn("Error reading sphere json definition {}: Spheroid Type {} is not known.", identifier, spheroidType);
					}
					return;
				}
				
				Identifier generationGroup = null;
				float generationWeight = 0;
				if (JsonHelper.hasString(jsonObject, "generation_group")) {
					generationGroup = Identifier.tryParse(JsonHelper.getString(jsonObject, "generation_group"));
					generationWeight = JsonHelper.getFloat(jsonObject, "generation_weight", 0);
				}
				
				if (generationGroup != null && generationWeight > 0) {
					LinkedHashMap<Spheroid.Template<?>, Float> weightedMap = WEIGHTED_SPHEROID_TYPES.get(generationGroup);
					if (weightedMap == null) {
                        StarrySkies.LOGGER.warn("Spheroid {}specifies non-existing generation_group {}. Will be ignored.", identifier, generationGroup);
					} else {
						weightedMap.put(template, generationWeight);
					}
				} else {
                    StarrySkies.LOGGER.warn("Spheroid {} does not have generation_group and generation_weight set. Will be ignored.", identifier);
				}
			} catch (Exception e) {
                StarrySkies.LOGGER.error("Error reading sphere json definition {}: {}", identifier, e);
				e.printStackTrace();
			}
		});
	}
	
	@Override
	public Identifier getFabricId() {
		return StarrySkies.locate(ID);
	}
	
	public static Spheroid.Template<?> getWeightedRandomSpheroid(Identifier distributionTypeID, ChunkRandom systemRandom) {
		return Support.getWeightedRandom(WEIGHTED_SPHEROID_TYPES.get(distributionTypeID), systemRandom);
	}
	
}