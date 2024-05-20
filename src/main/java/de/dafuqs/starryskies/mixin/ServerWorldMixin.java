package de.dafuqs.starryskies.mixin;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.dimension.*;
import net.minecraft.registry.tag.*;
import net.minecraft.server.world.*;
import net.minecraft.util.*;
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
	private final Map<TagKey<Structure>, Identifier> locatableStarrySpheres = new HashMap<>() {{
		put(StructureTags.EYE_OF_ENDER_LOCATED, StarrySkies.locate("overworld/treasure/stronghold"));
		put(StructureTags.ON_OCEAN_EXPLORER_MAPS, StarrySkies.locate("overworld/treasure/ocean_monument"));
	}};
	
	@Inject(at = @At("HEAD"), method = "locateStructure(Lnet/minecraft/registry/tag/TagKey;Lnet/minecraft/util/math/BlockPos;IZ)Lnet/minecraft/util/math/BlockPos;", cancellable = true)
	public void starryskies$locateStructure(TagKey<Structure> structureTag, BlockPos pos, int radius, boolean skipReferencedStructures, CallbackInfoReturnable<BlockPos> cir) {
		ServerWorld thisWorld = (ServerWorld) (Object) this;
		ChunkGenerator chunkGenerator = thisWorld.getChunkManager().getChunkGenerator();
		if (chunkGenerator instanceof StarrySkyChunkGenerator && locatableStarrySpheres.containsKey(structureTag)) {
			Identifier sphereIdentifier = locatableStarrySpheres.get(structureTag);
			Support.SpheroidDistance spheroidDistance = Support.getClosestSpheroid3x3(thisWorld, pos, sphereIdentifier);
			if (spheroidDistance == null) {
				cir.setReturnValue(null);
			} else {
				cir.setReturnValue(spheroidDistance.spheroid.getPosition());
			}
		}
	}
	
	
}