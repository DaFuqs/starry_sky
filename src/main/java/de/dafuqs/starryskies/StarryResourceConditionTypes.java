package de.dafuqs.starryskies;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.data_loaders.*;
import net.fabricmc.fabric.api.resource.conditions.v1.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class StarryResourceConditionTypes {
	
	public static final ResourceConditionType<UniqueBlockGroupExistsResourceCondition> UNIQUE_BLOCK_GROUP_EXISTS = createResourceConditionType("unique_block_group_exist", UniqueBlockGroupExistsResourceCondition.CODEC);
	public static final ResourceConditionType<WeightedBlockGroupExistsResourceCondition> WEIGHTED_BLOCK_GROUP_EXISTS = createResourceConditionType("weighted_block_group_exist", WeightedBlockGroupExistsResourceCondition.CODEC);
	
	private static <T extends ResourceCondition> ResourceConditionType<T> createResourceConditionType(String name, MapCodec<T> codec) {
		return ResourceConditionType.create(StarrySkies.locate(name), codec);
	}
	
	public static void register() {
		ResourceConditions.register(UNIQUE_BLOCK_GROUP_EXISTS);
		ResourceConditions.register(WEIGHTED_BLOCK_GROUP_EXISTS);
	}
	
	public record UniqueBlockGroupExistsResourceCondition(List<Identifier> blockGroupId) implements ResourceCondition {
		
		public static final MapCodec<UniqueBlockGroupExistsResourceCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Identifier.CODEC.listOf().fieldOf("values").forGetter(UniqueBlockGroupExistsResourceCondition::blockGroupId)
		).apply(instance, UniqueBlockGroupExistsResourceCondition::new));
		
		@Override
		public ResourceConditionType<?> getType() {
			return StarryResourceConditionTypes.UNIQUE_BLOCK_GROUP_EXISTS;
		}
		
		@Override
		public boolean test(@Nullable RegistryWrapper.WrapperLookup registryLookup) {
			for (Identifier id : blockGroupId) {
				return UniqueBlockGroupsLoader.existsGroup(id);
			}
			return false;
		}
	}
	
	public record WeightedBlockGroupExistsResourceCondition(
			List<Identifier> blockGroupId) implements ResourceCondition {
		
		public static final MapCodec<WeightedBlockGroupExistsResourceCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Identifier.CODEC.listOf().fieldOf("values").forGetter(WeightedBlockGroupExistsResourceCondition::blockGroupId)
		).apply(instance, WeightedBlockGroupExistsResourceCondition::new));
		
		@Override
		public ResourceConditionType<?> getType() {
			return StarryResourceConditionTypes.WEIGHTED_BLOCK_GROUP_EXISTS;
		}
		
		@Override
		public boolean test(@Nullable RegistryWrapper.WrapperLookup registryLookup) {
			for (Identifier id : blockGroupId) {
				return WeightedBlockGroupsLoader.existsGroup(id);
			}
			return false;
		}
	}
	
}