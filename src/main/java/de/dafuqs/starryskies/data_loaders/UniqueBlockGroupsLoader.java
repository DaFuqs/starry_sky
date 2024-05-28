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
import net.minecraft.util.profiler.*;

import java.util.*;

public class UniqueBlockGroupsLoader extends JsonDataLoader implements IdentifiableResourceReloadListener {
	
	public record UniqueBlockGroup(List<BlockState> states) {
		
		public static UniqueBlockGroup register(Identifier id, UniqueBlockGroup block) {
			return Registry.register(StarryRegistries.UNIQUE_BLOCK_GROUP, id, block);
		}
		
		public static BlockState getFirstState(Identifier groupId) {
			UniqueBlockGroup group = StarryRegistries.UNIQUE_BLOCK_GROUP.get(groupId);
			if (group == null || group.states.isEmpty()) {
                StarrySkies.LOGGER.warn("Referencing empty/non-existing UniqueBlockGroup: {}. Using AIR instead.", groupId);
				return Blocks.AIR.getDefaultState();
			}
			return group.states.getFirst();
		}
		
	}
	
	public static final String ID = "starry_skies/unique_block_groups";
	public static final UniqueBlockGroupsLoader INSTANCE = new UniqueBlockGroupsLoader();
	
	protected UniqueBlockGroupsLoader() {
		super(new Gson(), ID);
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		StarryRegistries.UNIQUE_BLOCK_GROUP.reset();
		prepared.forEach((identifier, jsonElement) -> {
			List<BlockState> states = new ArrayList<>();
			for (JsonElement e : jsonElement.getAsJsonArray()) {
				try {
					BlockState state = StarrySkies.getStateFromString(e.getAsString());
					states.add(state);
				} catch (CommandSyntaxException ex) {
					if (StarrySkies.CONFIG.packCreatorMode) {
                        StarrySkies.LOGGER.warn("Unique Block group {} tries to load a non-existing block: {}. Will be ignored.", identifier, e);
					}
				}
			}
			
			Optional<RegistryEntry.Reference<UniqueBlockGroup>> key = StarryRegistries.UNIQUE_BLOCK_GROUP.getEntry(identifier);
			UniqueBlockGroup blockGroup;
			
			if (key.isPresent()) {
				blockGroup = key.get().value();
				blockGroup.states().addAll(states);
			} else {
				blockGroup = new UniqueBlockGroup(states);
				UniqueBlockGroup.register(identifier, blockGroup);
			}
		});
	}
	
	@Override
	public Identifier getFabricId() {
		return StarrySkies.locate(ID);
	}
	
}