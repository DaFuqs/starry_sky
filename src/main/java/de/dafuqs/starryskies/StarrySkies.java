package de.dafuqs.starryskies;

import com.mojang.brigadier.exceptions.*;
import de.dafuqs.starryskies.advancements.*;
import de.dafuqs.starryskies.commands.*;
import de.dafuqs.starryskies.configs.*;
import de.dafuqs.starryskies.data_loaders.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.state_providers.*;
import de.dafuqs.starryskies.worldgen.*;
import de.dafuqs.starryskies.worldgen.dimension.*;
import it.unimi.dsi.fastutil.objects.*;
import me.shedaniel.autoconfig.*;
import me.shedaniel.autoconfig.serializer.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.command.v2.*;
import net.fabricmc.fabric.api.entity.event.v1.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.resource.*;
import net.kyrptonaught.customportalapi.*;
import net.kyrptonaught.customportalapi.util.*;
import net.minecraft.block.*;
import net.minecraft.command.argument.*;
import net.minecraft.registry.*;
import net.minecraft.resource.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.gen.chunk.*;
import org.jetbrains.annotations.*;
import org.slf4j.*;

import java.util.*;

public class StarrySkies implements ModInitializer {
	
	public static final String MOD_ID = "starry_skies";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static StarrySkyConfig CONFIG;
	
	public static Identifier id(String name) {
		return Identifier.of(MOD_ID, name);
	}
	
	public static String idPlain(String name) {
		return id(name).toString();
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
	
	public static boolean isStarryWorld(ServerWorld world) {
		ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
		return chunkGenerator instanceof StarrySkyChunkGenerator;
	}
	
	@Override
	public void onInitialize() {
		//Set up config
		LOGGER.info("Starting up...");
		AutoConfig.register(StarrySkyConfig.class, JanksonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(StarrySkyConfig.class).getConfig();
		
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(UniqueBlockGroupDataLoader.INSTANCE);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(WeightedBlockGroupDataLoader.INSTANCE);
		
		// Register all the stuff
		Registry.register(Registries.CHUNK_GENERATOR, StarrySkies.id("starry_skies"), StarrySkyChunkGenerator.CODEC);
		
		StarryRegistries.register();
		StarryStateProviders.register();
		Spheres.initialize();
		StarryFeatures.initialize();
		SphereDecorators.initialize();
		StarryAdvancementCriteria.register();
		
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ClosestSphereCommand.register(dispatcher, registryAccess));
		ServerTickEvents.END_SERVER_TICK.register(new ProximityAdvancementCheckEvent());
		
		// Build a final map of sphere generation data for each chunk generator
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			Registry<GenerationGroup> generationGroupRegistry = server.getRegistryManager().get(StarryRegistryKeys.GENERATION_GROUP);
			Registry<SystemGenerator> systemGeneratorRegistry = server.getRegistryManager().get(StarryRegistryKeys.SYSTEM_GENERATOR);
			Registry<ConfiguredSphere<?, ?>> configuredSphereRegistry = server.getRegistryManager().get(StarryRegistryKeys.CONFIGURED_SPHERE);
			
			for (GenerationGroup generationGroup : generationGroupRegistry) {
				// cursed generator group id lookup. Using getEntries() does return random order, making worldgen undeterministic :C
				Identifier generationGroupId = generationGroupRegistry.getKey(generationGroup).get().getValue();
				Identifier systemGeneratorId = generationGroup.systemGeneratorId();
				
				SystemGenerator systemGenerator = systemGeneratorRegistry.get(systemGeneratorId);
				if (systemGenerator == null) {
					LOGGER.error("System generator with id {} referenced in starry skies generation group {} was not found", generationGroup.systemGeneratorId(), generationGroupId);
					continue;
				}
				
				Map<ConfiguredSphere<?, ?>, Float> weightedSpheres = new Object2ObjectArrayMap<>();
				for (ConfiguredSphere<?, ?> sphere : configuredSphereRegistry) {
					Optional<SphereConfig.Generation> sphereGenerationGroup = sphere.getGenerationGroup();
					if (sphereGenerationGroup.isPresent() && sphereGenerationGroup.get().group().equals(generationGroupId)) {
						weightedSpheres.put(sphere, sphereGenerationGroup.get().weight());
					}
				}
				
				if (!weightedSpheres.isEmpty()) {
					systemGenerator.addGenerationGroup(weightedSpheres, generationGroup.weight());
				}
			}
		});
		
		/*
			Workaround for https://bugs.mojang.com/browse/MC-188578:
			Sleeping in a bed in a custom dimension doesn't set time to day
			Weather and time of day is also only tracked in the overworld
		 */
		EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
			if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
				ServerWorld world = serverPlayerEntity.getServerWorld();
				if (isStarryWorld(world) && serverPlayerEntity.canResetTimeBySleeping()) {
					long nextDay = world.getTimeOfDay() + 24000L;
					long mod = nextDay - nextDay % 24000L;
					world.getServer().getOverworld().setTimeOfDay(mod);
					
					if (world.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE) && world.isRaining()) {
						world.getServer().getOverworld().resetWeather();
					}
				}
			}
		});
		
		
		if (CONFIG.registerStarryPortal) {
			setupPortals();
		}
		
		LOGGER.info("Finished loading.");
	}
	
	public static void setupPortals() {
		StarrySkies.LOGGER.info("Setting up Portal to Starry Skies...");
		
		Identifier portalFrameBlockIdentifier = Identifier.tryParse(StarrySkies.CONFIG.starrySkyPortalFrameBlock.toLowerCase());
		Block portalFrameBlock = Registries.BLOCK.get(portalFrameBlockIdentifier);
		
		PortalLink portalLink = new PortalLink(portalFrameBlockIdentifier, StarryDimensionKeys.STARRY_SKIES_DIMENSION_ID, StarrySkies.CONFIG.starrySkyPortalColor);
		CustomPortalApiRegistry.addPortal(portalFrameBlock, portalLink);
	}
	
}
