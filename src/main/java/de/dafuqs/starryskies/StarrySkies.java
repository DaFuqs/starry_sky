package de.dafuqs.starryskies;

import com.mojang.brigadier.exceptions.*;
import de.dafuqs.starryskies.advancements.*;
import de.dafuqs.starryskies.commands.*;
import de.dafuqs.starryskies.configs.*;
import de.dafuqs.starryskies.data_loaders.*;
import de.dafuqs.starryskies.dimension.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.*;
import me.shedaniel.autoconfig.*;
import me.shedaniel.autoconfig.serializer.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.command.v2.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.resource.*;
import net.minecraft.block.*;
import net.minecraft.command.argument.*;
import net.minecraft.registry.*;
import net.minecraft.resource.*;
import net.minecraft.server.network.*;
import net.minecraft.util.*;
import net.minecraft.world.gen.chunk.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StarrySkies implements ModInitializer {
	
	public static final String MOD_ID = "starry_skies";
	
	public static StarrySkyConfig CONFIG;
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	public static DynamicRegistryManager registryManager = DynamicRegistryManager.of(Registries.REGISTRIES);
	
	@Override
	public void onInitialize() {
		
		//Set up config
		LOGGER.info("Starting up...");
		AutoConfig.register(StarrySkyConfig.class, JanksonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(StarrySkyConfig.class).getConfig();
		
		
		// Register all the stuff
		StarryRegistries.register();
		StarryResourceConditionTypes.register();
		Registry.register(Registries.CHUNK_GENERATOR, new Identifier(MOD_ID, "starry_skies_chunk_generator"), StarrySkyChunkGenerator.CODEC);
		StarrySkyBiomes.initialize();
		if (CONFIG.registerStarryPortal) {
			StarrySkyDimension.setupPortals();
		}
		DecoratorFeatures.initialize();
		StarryAdvancementCriteria.register();

		SpheroidDecoratorType.initialize();
		SpheroidTemplateType.initialize();
		
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ClosestSpheroidCommand.register(dispatcher));
		
		// triggers everytime a world is loaded
		// so for overworld, nether, ... (they all share the same seed)
		ServerWorldEvents.LOAD.register((server, world) -> {
			registryManager = server.getRegistryManager();
		});
		
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(UniqueBlockGroupsLoader.INSTANCE);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(WeightedBlockGroupsLoader.INSTANCE);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SpheroidDecoratorLoader.INSTANCE);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(GenerationGroupLoader.INSTANCE);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SpheroidTemplateLoader.INSTANCE);
		
		ServerTickEvents.END_SERVER_TICK.register(new ProximityAdvancementCheckEvent());

		LOGGER.info("Finished loading.");
	}
	
	public static Identifier locate(String name) {
		return new Identifier(MOD_ID, name);
	}
	
	public static String locatePlain(String name) {
		return locate(name).toString();
	}
	
	public static BlockState getStateFromString(String s) throws CommandSyntaxException {
		return BlockArgumentParser.block(Registries.BLOCK.getReadOnlyWrapper(), s, false).blockState();
	}

	public static @Nullable BlockState getNullableStateFromString(String s) {
		try {
			return BlockArgumentParser.block(Registries.BLOCK.getReadOnlyWrapper(), s, false).blockState();
		} catch (Exception ignored) {
			StarrySkies.LOGGER.error("Encountered invalid blockstate: {}", s);
			return null;
		}
	}

	public static @Nullable BlockArgumentParser.BlockResult getBlockResult(String element) {
		try {
			return BlockArgumentParser.block(Registries.BLOCK.getReadOnlyWrapper(), element, true);
		} catch (Exception ignored) {
			StarrySkies.LOGGER.error("Encountered invalid block result: {}", element);
			return null;
		}
	}
	
	public static boolean inStarryWorld(ServerPlayerEntity serverPlayerEntity) {
		ChunkGenerator chunkGenerator = serverPlayerEntity.getServerWorld().getChunkManager().getChunkGenerator();
		return chunkGenerator instanceof StarrySkyChunkGenerator;
	}
	
}
