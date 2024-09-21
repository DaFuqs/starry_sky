package de.dafuqs.starryskies.advancements;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.advancement.criterion.*;
import net.minecraft.predicate.entity.*;
import net.minecraft.server.network.*;
import net.minecraft.util.*;

import java.util.*;

public class SphereDiscoveredCriterion extends AbstractCriterion<SphereDiscoveredCriterion.Conditions> {

	@Override
	public Codec<Conditions> getConditionsCodec() {
		return Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, PlacedSphere sphere) {
		this.trigger(player, (conditions) -> conditions.matches(sphere.getID(player.getRegistryManager())));
	}

	public record Conditions(Optional<LootContextPredicate> player,
							 List<Identifier> identifiers) implements AbstractCriterion.Conditions {

		public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(
				(instance) -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
						Identifier.CODEC.listOf().optionalFieldOf("ids", List.of()).forGetter(Conditions::identifiers)
				).apply(instance, Conditions::new)
		);

		public boolean matches(Identifier id) {
			if (id == null) {
				return true;
			}
			if (this.identifiers.isEmpty()) {
				return true;
			}
			for (Identifier identifier : identifiers) {
				if (identifier.equals(id)) {
					return true;
				}
			}
			return false;
		}

	}

}
