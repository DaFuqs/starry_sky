package de.dafuqs.starryskies.worldgen;

import de.dafuqs.starryskies.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.random.*;
import net.minecraft.util.math.random.Random;

import java.util.*;

public interface SphereConfig {

	DefaultSphereConfig DEFAULT = DefaultSphereConfig.INSTANCE;

	static float randomBetween(net.minecraft.util.math.random.Random random, int min, int max) {
		return min + random.nextFloat() * (max - min);
	}

	static List<ConfiguredSphereDecorator<?, ?>> selectDecorators(net.minecraft.util.math.random.Random random, ConfiguredSphere.SharedConfig sharedConfig) {
		List<ConfiguredSphereDecorator<?, ?>> decorators = new ArrayList<>();
		for (Map.Entry<ConfiguredSphereDecorator<?, ?>, Float> entry : sharedConfig.decorators().entrySet()) {
			if (random.nextFloat() < entry.getValue()) {
				decorators.add(entry.getKey());
			}
		}
		return decorators;
	}

	static List<Pair<EntityType<?>, Integer>> selectSpawns(Random random, ConfiguredSphere.SharedConfig sharedConfig) {
		List<Pair<EntityType<?>, Integer>> spawns = new ArrayList<>();
		for (SphereEntitySpawnDefinition entry : sharedConfig.spawns()) {
			if (random.nextFloat() < entry.chance) {
				int count = Support.getRandomBetween(random, entry.minCount, entry.maxCount);
				spawns.add(new Pair<>(entry.entityType, count));
			}
		}
		return spawns;
	}
	
}
