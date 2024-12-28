package de.dafuqs.starryskies.commands;

import com.mojang.brigadier.*;
import de.dafuqs.starryskies.*;
import net.minecraft.command.argument.*;
import net.minecraft.server.command.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import java.util.*;

public class ClosestSphereCommand {

	// TODO: add id autocomplete based on dynamic registry content
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("starryskies_sphere")
				.requires((source) -> source.hasPermissionLevel(StarrySkies.CONFIG.sphereCommandRequiredPermissionLevel))
				.executes((context -> execute(context.getSource(), null)))
				.then(CommandManager.argument("identifier", IdentifierArgumentType.identifier())
						.executes((context -> execute(context.getSource(), IdentifierArgumentType.getIdentifier(context, "identifier"))))));
	}

	private static int execute(ServerCommandSource source, Identifier identifier) {
		ServerPlayerEntity caller = source.getPlayer();
		
		Optional<Support.SphereDistance> distance;
		if (identifier == null) {
			distance = Support.getClosestSphere(caller);
		} else {
			distance = Support.getClosestSphere3x3(source.getWorld(), BlockPos.ofFloored(source.getPosition()), identifier, source.getRegistryManager());
		}
		
		if (distance.isPresent()) {
			source.sendFeedback(() -> Text.translatable(distance.get().sphere.getDescription(source.getRegistryManager())), false);
		} else {
			source.sendFeedback(() -> Text.translatable("commands.starry_skies.locate.fail"), false);
		}

		return 1;
	}


}