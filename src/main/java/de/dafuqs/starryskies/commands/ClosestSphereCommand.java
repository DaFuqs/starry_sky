package de.dafuqs.starryskies.commands;

import com.google.common.base.*;
import com.mojang.brigadier.*;
import com.mojang.brigadier.exceptions.*;
import com.mojang.datafixers.util.Pair;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.command.*;
import net.minecraft.command.argument.*;
import net.minecraft.registry.entry.*;
import net.minecraft.server.command.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import java.util.Optional;

public class ClosestSphereCommand {
	
	private static final DynamicCommandExceptionType SPHERE_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType((id) -> {
		return Text.stringifiedTranslatable("commands.starry_skies.locate.sphere.not_found", id);
	});
	
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(CommandManager.literal("starryskies_locate")
				.requires((source) -> source.hasPermissionLevel(StarrySkies.CONFIG.sphereCommandRequiredPermissionLevel))
				.executes((context -> execute(context.getSource(), null)))
				.then(CommandManager.argument("sphere", RegistryEntryPredicateArgumentType.registryEntryPredicate(registryAccess, StarryRegistryKeys.CONFIGURED_SPHERE))
						.executes(context -> execute(context.getSource(), RegistryEntryPredicateArgumentType.getRegistryEntryPredicate(context, "sphere", StarryRegistryKeys.CONFIGURED_SPHERE)))));
	}
	
	private static int execute(ServerCommandSource source, RegistryEntryPredicateArgumentType.EntryPredicate<ConfiguredSphere<?, ?>> predicate) throws CommandSyntaxException {
		BlockPos pos = BlockPos.ofFloored(source.getPosition());
		
		Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
		Optional<Pair<BlockPos, RegistryEntry<ConfiguredSphere<?, ?>>>> result = Support.getClosestSphere3x3(source.getWorld(), pos, predicate, source.getRegistryManager());
		stopwatch.stop();
		
		if (result.isPresent()) {
			return LocateCommand.sendCoordinates(source, predicate, pos, result.get(), "commands.starry_skies.locate.sphere.success", true, stopwatch.elapsed());
		}
		
		throw SPHERE_NOT_FOUND_EXCEPTION.create(predicate.asString());
	}


}