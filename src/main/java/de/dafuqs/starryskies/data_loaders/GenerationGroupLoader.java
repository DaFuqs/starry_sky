package de.dafuqs.starryskies.data_loaders;

import com.google.gson.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.worldgen.dimension.*;
import net.fabricmc.fabric.api.resource.*;
import net.minecraft.resource.*;
import net.minecraft.util.*;
import net.minecraft.util.profiler.*;

import java.util.*;

public class GenerationGroupLoader extends JsonDataLoader implements IdentifiableResourceReloadListener {

	public static final String ID = "starry_skies/distribution_type";
	public static final GenerationGroupLoader INSTANCE = new GenerationGroupLoader();

	protected GenerationGroupLoader() {
		super(new Gson(), ID);
	}

	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		prepared.forEach((identifier, jsonElement) -> {
			final JsonObject jsonObject = jsonElement.getAsJsonObject();

			final Identifier systemGeneratorId = new Identifier(JsonHelper.getString(jsonObject, "system_generator"));
			final SystemGenerator systemGenerator = StarryRegistries.SYSTEM_GENERATOR.get(systemGeneratorId);

			if (systemGenerator == null) {
				StarrySkies.LOGGER.error("Distribution Type {} is asking for System Generator {}, which does not exist", identifier, systemGeneratorId);
				return;
			}

			float weight = JsonHelper.getFloat(jsonObject, "weight");
			systemGenerator.addGenerationGroup(identifier, weight);
		});
	}

	@Override
	public Identifier getFabricId() {
		return StarrySkies.locate(ID);
	}

}