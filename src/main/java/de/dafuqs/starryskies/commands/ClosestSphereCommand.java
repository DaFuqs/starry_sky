package de.dafuqs.starryskies.commands;

import com.mojang.brigadier.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.command.*;
import net.minecraft.command.argument.*;
import net.minecraft.registry.entry.*;
import net.minecraft.server.command.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import net.minecraft.util.math.*;

import java.util.*;

public class ClosestSphereCommand {
	
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(CommandManager.literal("starryskies_locate")
				.requires((source) -> source.hasPermissionLevel(StarrySkies.CONFIG.sphereCommandRequiredPermissionLevel))
				.executes((context -> execute(context.getSource(), null)))
				.then(CommandManager.argument("sphere", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, StarryRegistryKeys.CONFIGURED_SPHERE))
						.executes(context -> execute(context.getSource(), RegistryEntryReferenceArgumentType.getRegistryEntry(context, "sphere", StarryRegistryKeys.CONFIGURED_SPHERE)))));
	}
	
	private static int execute(ServerCommandSource source, RegistryEntry.Reference<ConfiguredSphere<?, ?>> sphereKey) {
		ServerPlayerEntity caller = source.getPlayer();
		
		Optional<Support.SphereDistance> distance;
		if (sphereKey == null && caller != null) {
			distance = Support.getClosestSphere(caller);
		} else {
			distance = Support.getClosestSphere3x3(source.getWorld(), BlockPos.ofFloored(source.getPosition()), sphereKey.getKey().get(), source.getRegistryManager());
		}
		
		if (distance.isPresent()) {
			source.sendFeedback(() -> Text.translatable(distance.get().sphere.getDescription(source.getRegistryManager())), false);
		} else {
			source.sendFeedback(() -> Text.translatable("commands.starry_skies.locate.fail"), false);
		}

		return 1;
	}


}