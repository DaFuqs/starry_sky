package de.dafuqs.starryskies.data_loaders;

import com.google.gson.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
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
	
	public record SpheroidDistributionType(SpheroidDimensionType dimensionType, Map<Identifier, Float> distribution) {
	
	}
	
	protected SpheroidDistributionLoader() {
		super(new Gson(), ID);
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		prepared.forEach((identifier, jsonElement) -> {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			
			SpheroidDimensionType dimensionType = SpheroidDimensionType.of(JsonHelper.getString(jsonObject, "dimension"));
			float weight = JsonHelper.getFloat(jsonObject, "weight");
			
			register(dimensionType, identifier, weight);
			
			SpheroidDistributionType distributionType = new SpheroidDistributionType();
			
			Registry.register(StarryRegistries.SPHEROID_DISTRIBUTION_TYPE, identifier, distributionType);
		});
	}
	
	public void register(@NotNull SpheroidDimensionType dimensionType, Identifier identifier, float weight) {
		DISTRIBUTION_TYPES.get(dimensionType).put(identifier, weight);
	}
	
	@Override
	public Identifier getFabricId() {
		return StarrySkies.locate(ID);
	}
	
	public static Identifier getWeightedRandomDistributionType(SpheroidDimensionType dimensionType, ChunkRandom systemRandom) {
		Map<Identifier, Float> entry = DISTRIBUTION_TYPES.get(dimensionType);
		return Support.getWeightedRandom(entry, systemRandom);
	}
	
	public static List<Identifier> getAll() {
		List<Identifier> ids = new ArrayList<>();
		for (Map<Identifier, Float> entry : DISTRIBUTION_TYPES.values()) {
			ids.addAll(entry.keySet());
		}
		return ids;
	}
	
}