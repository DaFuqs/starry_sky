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

public class UniqueBlockGroupDataLoader extends JsonDataLoader implements IdentifiableResourceReloadListener {
	
	public static final String LOCATION = "starry_skies/unique_block_group";
	public static final Identifier ID = StarrySkies.id(LOCATION);
	public static final UniqueBlockGroupDataLoader INSTANCE = new UniqueBlockGroupDataLoader();
	
	protected static final Map<String, Block> GROUPS = new Object2ObjectArrayMap<>();
	
	private UniqueBlockGroupDataLoader() {
		super(new Gson(), LOCATION);
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		prepared.forEach((identifier, jsonElement) -> {
			JsonObject o = jsonElement.getAsJsonObject();
			String group = o.get("group").getAsString();
			
			if (GROUPS.containsKey(group)) {
				return;
			}
			
			JsonArray blockArray = o.get("blocks").getAsJsonArray();
			for (JsonElement e : blockArray) {
				Identifier id = Identifier.tryParse(e.getAsString());
				Optional<Block> optionalBlock = Registries.BLOCK.getOrEmpty(id);
				optionalBlock.ifPresent(block -> GROUPS.put(group, block));
				return;
			}
		});
	}
	
	@Override
	public Identifier getFabricId() {
		return ID;
	}
	
	public Block get(String id) {
		return GROUPS.get(id);
	}
	
	public BlockState getEntry(String group, Random random) {
		Block block = UniqueBlockGroupDataLoader.INSTANCE.get(group);
		if (block == null) {
			StarrySkies.LOGGER.warn("Trying to query a nonexistent UniqueBlockGroup: {}", group);
			StarrySkies.LOGGER.error(Arrays.toString(Thread.currentThread().getStackTrace()));
			return Blocks.AIR.getDefaultState();
		}
		return block.getDefaultState();
	}
	
}