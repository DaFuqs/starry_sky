package de.dafuqs.starryskies.data_loaders;

import com.google.gson.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import net.fabricmc.fabric.api.resource.*;
import net.minecraft.registry.*;
import net.minecraft.resource.*;
import net.minecraft.util.*;
import net.minecraft.util.math.random.*;
import net.minecraft.util.profiler.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class SpheroidDistributionLoader extends JsonDataLoader implements IdentifiableResourceReloadListener {
	
	public static final String ID = "starry_skies/distribution_type";
	public static final SpheroidDistributionLoader INSTANCE = new SpheroidDistributionLoader();
	
	public record SpheroidDistributionType(SpheroidDimensionType type, Map<Identifier, Float> distribution) {}
	
	protected SpheroidDistributionLoader() {
		super(new Gson(), ID);
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		StarryRegistries.SPHEROID_DISTRIBUTION_TYPE.reset();
		prepared.forEach((identifier, jsonElement) -> {
			final JsonObject jsonObject = jsonElement.getAsJsonObject();

			final Identifier dimensionTypeId = new Identifier(JsonHelper.getString(jsonObject, "dimension"));
			final SpheroidDimensionType dimensionType = StarryRegistries.STARRY_DIMENSION_TYPE.get(dimensionTypeId);
			float weight = JsonHelper.getFloat(jsonObject, "weight");

			SpheroidDistributionType distributionType = StarryRegistries.SPHEROID_DISTRIBUTION_TYPE.get(dimensionTypeId);
			if (distributionType == null) {
				distributionType = new SpheroidDistributionType(dimensionType, new Object2FloatArrayMap<>(1));
				Registry.register(StarryRegistries.SPHEROID_DISTRIBUTION_TYPE, dimensionTypeId, distributionType);
			}
			distributionType.distribution.put(identifier, weight);
		});
	}
	
	@Override
	public Identifier getFabricId() {
		return StarrySkies.locate(ID);
	}
	
	public static Identifier getWeightedRandomDistributionType(SpheroidDimensionType dimensionType, ChunkRandom systemRandom) {
		final var entry = Objects.requireNonNull(StarryRegistries.SPHEROID_DISTRIBUTION_TYPE.get(dimensionType.id()));
		final var map = entry.distribution;
		return Support.getWeightedRandom(map, systemRandom);
	}
	
	public static List<Identifier> getAll() {
		List<Identifier> ids = new ArrayList<>();
		StarryRegistries.SPHEROID_DISTRIBUTION_TYPE.stream().forEach(e -> ids.addAll(e.distribution.keySet()));
		return ids;
	}
	
}