package de.dafuqs.starryskies.data_loaders;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
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

public class UniqueBlockGroupDataLoader extends JsonDataLoader<UniqueBlockGroupDataLoader.Entry> implements IdentifiableResourceReloadListener {
	
	public static final String LOCATION = "starry_skies/unique_block_group";
	public static final Identifier ID = StarrySkies.id(LOCATION);
	public static final UniqueBlockGroupDataLoader INSTANCE = new UniqueBlockGroupDataLoader();
	
	protected static final Map<String, Block> GROUPS = new Object2ObjectArrayMap<>();
	
	public record Entry(String group, List<Identifier> blockIDs) {
		public static final Codec<Entry> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				Codec.STRING.fieldOf("group").forGetter(Entry::group),
				Identifier.CODEC.listOf().fieldOf("blocks").forGetter(Entry::blockIDs)
		).apply(instance, Entry::new));
	}
	
	private UniqueBlockGroupDataLoader() {
		super(Entry.CODEC, ResourceFinder.json(LOCATION));
	}
	
	@Override
	protected void apply(Map<Identifier, Entry> prepared, ResourceManager manager, Profiler profiler) {
		for (Map.Entry<Identifier, Entry> entry : prepared.entrySet()) {
			String groupName = entry.getValue().group;
			if (GROUPS.containsKey(groupName)) {
				return;
			}
			
			for (Identifier blockId : entry.getValue().blockIDs) {
				Optional<Block> optionalBlock = Registries.BLOCK.getOptionalValue(blockId);
				optionalBlock.ifPresent(block -> GROUPS.put(groupName, block));
				return;
			}
		}
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