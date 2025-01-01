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

public class WeightedBlockGroupDataLoader extends JsonDataLoader<WeightedBlockGroupDataLoader.Entry> implements IdentifiableResourceReloadListener {
	
	public static final String LOCATION = "starry_skies/weighted_block_group";
	public static final Identifier ID = StarrySkies.id(LOCATION);
	public static final WeightedBlockGroupDataLoader INSTANCE = new WeightedBlockGroupDataLoader();
	
	protected static final Map<String, Map<Block, Float>> GROUPS = new Object2ObjectArrayMap<>();
	
	public record Entry(Map<Identifier, Float> weightedBlockIDs) {
		public static final Codec<WeightedBlockGroupDataLoader.Entry> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				Codec.unboundedMap(Identifier.CODEC, Codec.FLOAT).fieldOf("blocks").forGetter(WeightedBlockGroupDataLoader.Entry::weightedBlockIDs)
		).apply(instance, WeightedBlockGroupDataLoader.Entry::new));
	}
	
	private WeightedBlockGroupDataLoader() {
		super(Entry.CODEC, ResourceFinder.json(LOCATION));
	}
	
	@Override
	protected void apply(Map<Identifier, WeightedBlockGroupDataLoader.Entry> prepared, ResourceManager manager, Profiler profiler) {
		for (Map.Entry<Identifier, WeightedBlockGroupDataLoader.Entry> entry : prepared.entrySet()) {
			String path = entry.getKey().getPath();
			
			for (Map.Entry<Identifier, Float> e : entry.getValue().weightedBlockIDs.entrySet()) {
				Optional<Block> optionalBlock = Registries.BLOCK.getOptionalValue(e.getKey());
				if (optionalBlock.isPresent()) {
					Block block = optionalBlock.get();
					float weight = e.getValue();
					GROUPS.computeIfAbsent(path, k -> new Object2FloatArrayMap<>()).put(block, weight);
				}
			}
		}
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