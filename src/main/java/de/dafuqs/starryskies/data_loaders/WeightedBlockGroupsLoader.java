package de.dafuqs.starryskies.data_loaders;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import net.fabricmc.fabric.api.resource.*;
import net.minecraft.block.*;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.*;
import net.minecraft.resource.*;
import net.minecraft.util.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.*;

import java.util.*;

public class WeightedBlockGroupsLoader extends JsonDataLoader implements IdentifiableResourceReloadListener {
	
	public record WeightedBlockGroup(Map<BlockState, Float> weights) {
		
		public static WeightedBlockGroup register(Identifier id, WeightedBlockGroup block) {
			return Registry.register(StarryRegistries.WEIGHTED_BLOCK_GROUP, id, block);
		}
		
		public static BlockState getRandomState(Identifier groupId, Random random) {
			WeightedBlockGroup group = StarryRegistries.WEIGHTED_BLOCK_GROUP.get(groupId);
			if (group == null || group.weights.isEmpty()) {
                StarrySkies.LOGGER.warn("Referencing empty/non-existing WeightedBlockGroup: {}. Using AIR instead.", groupId);
				return Blocks.AIR.getDefaultState();
			}
			return Support.getWeightedRandom(group.weights, random);
		}
		
	}
	
	public static final String ID = "starry_skies/weighted_block_groups";
	public static final WeightedBlockGroupsLoader INSTANCE = new WeightedBlockGroupsLoader();
	
	protected WeightedBlockGroupsLoader() {
		super(new Gson(), ID);
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		prepared.forEach((identifier, jsonElement) -> {
			Map<BlockState, Float> weights = new Object2FloatArrayMap<>();
			for (Map.Entry<String, JsonElement> weight : jsonElement.getAsJsonObject().entrySet()) {
				try {
					BlockState state = StarrySkies.getStateFromString(weight.getKey());
					float weightValue = weight.getValue().getAsFloat();
					weights.put(state, weightValue);
				} catch (CommandSyntaxException ex) {
					if (StarrySkies.CONFIG.packCreatorMode) {
                        StarrySkies.LOGGER.warn("'Weighted Block group {} tries to load a non-existing block: {}. Will be ignored.", identifier, weight.getKey());
					}
				}
			}
			
			Optional<RegistryEntry.Reference<WeightedBlockGroup>> key = StarryRegistries.WEIGHTED_BLOCK_GROUP.getEntry(identifier);
			WeightedBlockGroup blockGroup;
			
			if (key.isPresent()) {
				blockGroup = key.get().value();
				blockGroup.weights.putAll(weights);
			} else {
				blockGroup = new WeightedBlockGroup(weights);
				WeightedBlockGroup.register(identifier, blockGroup);
			}
		});
	}
	
	@Override
	public Identifier getFabricId() {
		return StarrySkies.locate(ID);
	}
	
}