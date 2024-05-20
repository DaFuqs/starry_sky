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

public class ClosestSpheroidCommand {
	
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("starryskies_sphere")
				.requires((source) -> source.hasPermissionLevel(StarrySkies.CONFIG.sphereCommandRequiredPermissionLevel))
				.executes((context -> execute(context.getSource(), null)))
				.then(CommandManager.argument("identifier", IdentifierArgumentType.identifier())
						.executes((context -> execute(context.getSource(), IdentifierArgumentType.getIdentifier(context, "identifier"))))));
	}
	
	private static int execute(ServerCommandSource source, Identifier identifier) {
		ServerPlayerEntity caller = source.getPlayer();
		
		Optional<Support.SpheroidDistance> spheroidDistance;
		if (identifier == null) {
			spheroidDistance = Support.getClosestSpheroidToPlayer(caller);
		} else {
			spheroidDistance = Optional.ofNullable(Support.getClosestSpheroid3x3(source.getWorld(), BlockPos.ofFloored(source.getPosition()), identifier));
		}
		
		if (spheroidDistance.isPresent()) {
			source.sendFeedback(() -> Text.translatable(spheroidDistance.get().spheroid.getDescription()), false);
		} else {
			source.sendFeedback(() -> Text.translatable("Could not determine closest spheroid. :("), false);
		}
		
		return 1;
	}
	
	
}