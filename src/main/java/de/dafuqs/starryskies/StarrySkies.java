package de.dafuqs.starryskies;

import com.google.gson.*;
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
import net.minecraft.server.world.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import static org.apache.logging.log4j.Level.*;

public class StarrySkies implements ModInitializer {
	
	public static final String MOD_ID = "starry_skies";
	
	public static StarrySkyConfig CONFIG;
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static ServerWorld starryWorld;
	public static ServerWorld starryWorldNether;
	public static ServerWorld starryWorldEnd;
	
	@Override
	public void onInitialize() {
		
		//Set up config
		log(INFO, "Starting up...");
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
		
		SpheroidTypes.initialize();
		SpheroidDecoratorType.initialize();
		
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ClosestSpheroidCommand.register(dispatcher);
		});
		
		// triggers everytime a world is loaded
		// so for overworld, nether, ... (they all share the same seed)
		ServerWorldEvents.LOAD.register((server, world) -> {
			if (world.getRegistryKey().equals(StarrySkyDimension.OVERWORLD_KEY)) {
				StarrySkies.starryWorld = world;
			} else if (world.getRegistryKey().equals(StarrySkyDimension.NETHER_KEY)) {
				StarrySkies.starryWorldNether = world;
			} else if (world.getRegistryKey().equals(StarrySkyDimension.END_KEY)) {
				StarrySkies.starryWorldEnd = world;
			}
		});
		
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(UniqueBlockGroupsLoader.INSTANCE);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(WeightedBlockGroupsLoader.INSTANCE);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SpheroidDecoratorLoader.INSTANCE);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SpheroidDistributionLoader.INSTANCE);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SpheroidTemplateLoader.INSTANCE);
		
		ServerTickEvents.END_SERVER_TICK.register(new ProximityAdvancementCheckEvent());
		
		log(INFO, "Finished loading.");
	}
	
	public static Identifier locate(String name) {
		return new Identifier(MOD_ID, name);
	}
	
	public static String locatePlain(String name) {
		return locate(name).toString();
	}
	
	public static void log(Level logLevel, String message) {
		LOGGER.log(logLevel, "[StarrySkies] " + message);
	}
	
	public static BlockState getStateFromString(String s) throws CommandSyntaxException {
		return BlockArgumentParser.block(Registries.BLOCK.getReadOnlyWrapper(), s, false).blockState();
	}
	
	public static BlockArgumentParser.BlockResult getBlockResult(JsonObject json, String element) throws CommandSyntaxException {
		return BlockArgumentParser.block(Registries.BLOCK.getReadOnlyWrapper(), JsonHelper.getString(json, element), true);
	}
	
	public static BlockArgumentParser.BlockResult getBlockResult(String element) throws CommandSyntaxException {
		return BlockArgumentParser.block(Registries.BLOCK.getReadOnlyWrapper(), element, true);
	}
	
	public static Block getBlockFromString(String s) {
		return Registries.BLOCK.get(Identifier.tryParse(s));
	}
	
	public static boolean inStarryWorld(ServerPlayerEntity serverPlayerEntity) {
		RegistryKey<World> worldRegistryKey = serverPlayerEntity.getEntityWorld().getRegistryKey();
		return isStarryWorld(worldRegistryKey);
	}
	
	public static ServerWorld getStarryWorld(SpheroidDimensionType dimensionType) {
		switch (dimensionType) {
			case OVERWORLD -> {
				return starryWorld;
			}
			case NETHER -> {
				return starryWorldNether;
			}
			default -> {
				return starryWorldEnd;
			}
		}
	}
	
	public static boolean isStarryWorld(RegistryKey<World> worldRegistryKey) {
		if (StarrySkies.starryWorld == null || StarrySkies.starryWorldNether == null || StarrySkies.starryWorldEnd == null) {
			log(Level.ERROR, "The Starry Dimensions could not be loaded. If this is your first launch this is probably related to a known vanilla bug where custom dimensions are not loaded when first generating the world. Restarting / quitting and reloading will fix this issue.");
			return false;
		} else {
			return worldRegistryKey.equals(StarrySkies.starryWorld.getRegistryKey())
					|| worldRegistryKey.equals(starryWorldNether.getRegistryKey())
					|| worldRegistryKey.equals(starryWorldEnd.getRegistryKey());
		}
	}
	
}
