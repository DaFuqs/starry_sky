package de.dafuqs.starryskies.data_loaders;

import com.google.gson.*;
import de.dafuqs.starryskies.*;
import it.unimi.dsi.fastutil.objects.*;
import net.fabricmc.fabric.api.resource.*;
import net.minecraft.block.*;
import net.minecraft.registry.*;
import net.minecraft.resource.*;
import net.minecraft.util.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.*;

import java.util.*;

public class WeightedBlockGroupDataLoader extends JsonDataLoader implements IdentifiableResourceReloadListener {
	
	public static final String LOCATION = "starry_skies/weighted_block_group";
	public static final Identifier ID = StarrySkies.id(LOCATION);
	public static final WeightedBlockGroupDataLoader INSTANCE = new WeightedBlockGroupDataLoader();
	
	protected static final Map<String, Map<Block, Float>> GROUPS = new Object2ObjectArrayMap<>();
	
	private WeightedBlockGroupDataLoader() {
		super(new Gson(), LOCATION);
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		prepared.forEach((identifier, jsonElement) -> {
			JsonObject o = jsonElement.getAsJsonObject();
			String group = o.get("group").getAsString();
			
			JsonObject blockArray = o.get("blocks").getAsJsonObject();
			for (Map.Entry<String, JsonElement> e : blockArray.asMap().entrySet()) {
				Identifier id = Identifier.tryParse(e.getKey());
				Optional<Block> optionalBlock = Registries.BLOCK.getOrEmpty(id);
				if (optionalBlock.isPresent()) {
					Block block = optionalBlock.get();
					float weight = e.getValue().getAsFloat();
					GROUPS.computeIfAbsent(group, k -> new Object2FloatArrayMap<>()).put(block, weight);
				}
			}
		});
	}
	
	@Override
	public Identifier getFabricId() {
		return ID;
	}
	
	public Map<Block, Float> get(String blockGroup) {
		return GROUPS.get(blockGroup);
	}
	
	public BlockState getEntry(String group, Random random) {
		Map<Block, Float> weightedBlocks = get(group);
		if (weightedBlocks == null) {
			StarrySkies.LOGGER.warn("Trying to query a nonexistent WeightedBlockGroup: {}", group);
			StarrySkies.LOGGER.error(Arrays.toString(Thread.currentThread().getStackTrace()));
			return Blocks.AIR.getDefaultState();
		} else if (weightedBlocks.isEmpty()) {
			StarrySkies.LOGGER.warn("Trying to query an empty WeightedBlockGroup: {}", group);
			StarrySkies.LOGGER.error(Arrays.toString(Thread.currentThread().getStackTrace()));
			return Blocks.AIR.getDefaultState();
		}
		return Support.getWeightedRandom(weightedBlocks, random).getDefaultState();
	}
	
}