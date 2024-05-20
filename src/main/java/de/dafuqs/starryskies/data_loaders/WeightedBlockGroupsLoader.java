package de.dafuqs.starryskies.data_loaders;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
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
	
	public record WeightedBlockGroup(List<BlockState> states) {
		
		public static WeightedBlockGroup register(Identifier id, WeightedBlockGroup block) {
			return Registry.register(StarryRegistries.WEIGHTED_BLOCK_GROUP, id, block);
		}
		
		public static BlockState getRandomState(Identifier groupId, Random random) {
			Map<BlockState, Float> group = BLOCK_GROUPS.get(groupId);
			if (group == null || group.isEmpty()) {
				StarrySkies.log(Level.WARN, "Referencing empty/non-existing WeightedBlockGroup: " + groupId + ". Using AIR instead.");
				return Blocks.AIR.getDefaultState();
			}
			return Support.getWeightedRandom(group, random);
		}
		
	}
	
	public static final String ID = "starry_skies/weighted_block_groups";
	public static final WeightedBlockGroupsLoader INSTANCE = new WeightedBlockGroupsLoader();
	
	private static final Map<Identifier, Map<BlockState, Float>> BLOCK_GROUPS = new HashMap<>();
	
	protected WeightedBlockGroupsLoader() {
		super(new Gson(), ID);
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		prepared.forEach((identifier, jsonElement) -> {
			List<BlockState> states = new ArrayList<>();
			for (JsonElement e : jsonElement.getAsJsonArray()) {
				try {
					BlockState state = StarrySkies.getStateFromString(e.getAsString());
					states.add(state);
				} catch (CommandSyntaxException ex) {
					if (StarrySkies.CONFIG.packCreatorMode) {
						StarrySkies.log(Level.WARN, "'Weighted Block group " + identifier + " tries to load a non-existing block: " + e + ". Will be ignored.");
					}
				}
			}
			
			Optional<RegistryEntry.Reference<WeightedBlockGroup>> key = StarryRegistries.WEIGHTED_BLOCK_GROUP.getEntry(identifier);
			WeightedBlockGroup blockGroup;
			
			if (key.isPresent()) {
				blockGroup = key.get().value();
				blockGroup.states().addAll(states);
			} else {
				blockGroup = new WeightedBlockGroup(states);
				WeightedBlockGroup.register(identifier, blockGroup);
			}
		});
	}
	
	@Override
	public Identifier getFabricId() {
		return StarrySkies.locate(ID);
	}
	
}