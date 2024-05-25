package de.dafuqs.starryskies.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.dafuqs.starryskies.StarrySkyDimensionTravelHandler;
import net.kyrptonaught.customportalapi.portal.frame.PortalFrameTester;
import net.kyrptonaught.customportalapi.util.CustomTeleporter;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CustomTeleporter.class)
public class CustomTeleporterMixin {

    @WrapOperation(method = "TPToDim", at = @At(value = "INVOKE", target = "Lnet/kyrptonaught/customportalapi/util/CustomTeleporter;customTPTarget(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/kyrptonaught/customportalapi/portal/frame/PortalFrameTester$PortalFrameTesterFactory;)Lnet/minecraft/world/TeleportTarget;"))
    private static TeleportTarget replaceTarget(ServerWorld destinationWorld, Entity entity, BlockPos enteredPortalPos, Block frameBlock, PortalFrameTester.PortalFrameTesterFactory frameTesterFactory, Operation<TeleportTarget> original) {
        var target = StarrySkyDimensionTravelHandler.handleGetTeleportTarget(entity, destinationWorld);
        return target == null ? original.call(destinationWorld, entity, enteredPortalPos, frameBlock, frameTesterFactory) : target;
    }

}
