package de.dafuqs.starryskies.mixin;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.worldgen.*;
import de.dafuqs.starryskies.worldgen.dimension.*;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.structure.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
	@Unique
	private final Map<TagKey<Structure>, RegistryKey<ConfiguredSphere<?, ?>>> locatableStarrySpheres = new HashMap<>() {{
		put(StructureTags.EYE_OF_ENDER_LOCATED, RegistryKey.of(StarryRegistryKeys.CONFIGURED_SPHERE, StarrySkies.id("overworld/treasure/stronghold")));
		put(StructureTags.ON_OCEAN_EXPLORER_MAPS, RegistryKey.of(StarryRegistryKeys.CONFIGURED_SPHERE, StarrySkies.id("overworld/treasure/ocean_monument")));
	}};

	@Inject(at = @At("HEAD"), method = "locateStructure(Lnet/minecraft/registry/tag/TagKey;Lnet/minecraft/util/math/BlockPos;IZ)Lnet/minecraft/util/math/BlockPos;", cancellable = true)
	public void starryskies$locateStructure(TagKey<Structure> structureTag, BlockPos pos, int radius, boolean skipReferencedStructures, CallbackInfoReturnable<BlockPos> cir) {
		ServerWorld thisWorld = (ServerWorld) (Object) this;
		ChunkGenerator chunkGenerator = thisWorld.getChunkManager().getChunkGenerator();
		if (chunkGenerator instanceof StarrySkyChunkGenerator && locatableStarrySpheres.containsKey(structureTag)) {
			RegistryKey<ConfiguredSphere<?, ?>> sphereIdentifier = locatableStarrySpheres.get(structureTag);
			Optional<Support.SphereDistance> distance = Support.getClosestSphere3x3(thisWorld, pos, sphereIdentifier, thisWorld.getRegistryManager());
			if (distance.isEmpty()) {
				cir.setReturnValue(null);
			} else {
				cir.setReturnValue(distance.get().sphere.getPosition());
			}
		}
	}


}