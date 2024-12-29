package de.dafuqs.starryskies.mixin;

import com.mojang.datafixers.util.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import de.dafuqs.starryskies.worldgen.dimension.*;
import net.minecraft.registry.entry.*;
import net.minecraft.registry.tag.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.structure.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;
import java.util.function.*;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
	
	/**
	 * When we are in a Starry world the structure location logic auto-remaps structure tags to sphere tags,
	 * for example, "eye_of_ender_located" structure tag (Eye of Ender) now locates spheres in the sphere tag "starry_skies:eye_of_ender_located"
	 */
	@Inject(at = @At("HEAD"), method = "locateStructure(Lnet/minecraft/registry/tag/TagKey;Lnet/minecraft/util/math/BlockPos;IZ)Lnet/minecraft/util/math/BlockPos;", cancellable = true)
	public void starryskies$locateStructure(TagKey<Structure> structureTag, BlockPos pos, int radius, boolean skipReferencedStructures, CallbackInfoReturnable<BlockPos> cir) {
		ServerWorld thisWorld = (ServerWorld) (Object) this;
		ChunkGenerator chunkGenerator = thisWorld.getChunkManager().getChunkGenerator();
		if (chunkGenerator instanceof StarrySkyChunkGenerator) {
			TagKey<ConfiguredSphere<?, ?>> targetSphereTag = SphereTags.getForVanillaStructure(structureTag);
			Predicate<RegistryEntry<ConfiguredSphere<?, ?>>> predicate = configuredSphereRegistryEntry -> configuredSphereRegistryEntry.isIn(targetSphereTag);
			Optional<Pair<BlockPos, RegistryEntry<ConfiguredSphere<?, ?>>>> distance = Support.getClosestSphere3x3(thisWorld, pos, predicate, thisWorld.getRegistryManager());
			if (distance.isEmpty()) {
				cir.setReturnValue(null);
			} else {
				cir.setReturnValue(distance.get().getFirst());
			}
		}
	}


}