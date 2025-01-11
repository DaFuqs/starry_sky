package de.dafuqs.starryskies;

import de.dafuqs.starryskies.advancements.*;
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
import net.fabricmc.fabric.api.entity.event.v1.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.resource.*;
import net.minecraft.registry.*;
import net.minecraft.resource.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.gen.chunk.*;
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
		
		ServerTickEvents.END_SERVER_TICK.register(new ProximityAdvancementCheckEvent());
		
		// Build a final map of sphere generation data for each chunk generator
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			Registry<GenerationGroup> generationGroupRegistry = server.getRegistryManager().getOrThrow(StarryRegistryKeys.GENERATION_GROUP);
			Registry<SystemGenerator> systemGeneratorRegistry = server.getRegistryManager().getOrThrow(StarryRegistryKeys.SYSTEM_GENERATOR);
			Registry<ConfiguredSphere<?, ?>> configuredSphereRegistry = server.getRegistryManager().getOrThrow(StarryRegistryKeys.CONFIGURED_SPHERE);
			
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
		
		LOGGER.info("Finished loading.");
	}
	
}
