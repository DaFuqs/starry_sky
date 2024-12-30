package de.dafuqs.starryskies.client.sky;

import com.mojang.blaze3d.systems.*;
import de.dafuqs.starryskies.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.block.enums.*;
import net.minecraft.client.render.*;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.*;
import net.minecraft.util.*;
import org.joml.*;

// TODO: Rainbow Skybox setting does not work
@Environment(EnvType.CLIENT)
public class StarrySkyBox implements DimensionRenderingRegistry.SkyRenderer {

	public final Identifier UP;
	public final Identifier DOWN;
	public final Identifier WEST;
	public final Identifier EAST;
	public final Identifier NORTH;
	public final Identifier SOUTH;

	public StarrySkyBox(String up, String down, String west, String east, String north, String south) {
		UP = StarrySkies.id(up);
		DOWN = StarrySkies.id(down);
		WEST = StarrySkies.id(west);
		EAST = StarrySkies.id(east);
		NORTH = StarrySkies.id(north);
		SOUTH = StarrySkies.id(south);
	}

	@Override
	public void render(WorldRenderContext context) {
		CameraSubmersionType cameraSubmersionType = context.camera().getSubmersionType();
		if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW || cameraSubmersionType == CameraSubmersionType.LAVA || hasBlindnessOrDarkness(context.camera())) {
			return;
		}
		renderStarrySky(context);
	}
	
	private boolean hasBlindnessOrDarkness(Camera camera) {
		Entity var3 = camera.getFocusedEntity();
		if (!(var3 instanceof LivingEntity livingEntity)) {
			return false;
		} else {
			return livingEntity.hasStatusEffect(StatusEffects.BLINDNESS) || livingEntity.hasStatusEffect(StatusEffects.DARKNESS);
		}
	}
	
	// See WorldRenderer.renderEndSky() for inspiration
	private void renderStarrySky(WorldRenderContext context) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(false);
		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		
		Matrix4f matrix4f = context.positionMatrix();
		
		/*Vec3d vec3d = context.world().getSkyColor(context.camera().getPos(), 0);  // TODO: how to get tickdelta?
		float f = (float)vec3d.x;
		float g = (float)vec3d.y;
		float h = (float)vec3d.z;
		RenderSystem.setShaderColor(f, g, h, 1.0F);*/
		
		RenderSystem.setShaderTexture(0, DOWN);
		bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, -100.0f).texture(0.0F, 0.0F).color(255, 255, 255, 255);
		bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, 100.0f).texture(0.0F, 1.0F).color(255, 255, 255, 255);
		bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, 100.0f).texture(1.0F, 1.0F).color(255, 255, 255, 255);
		bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, -100.0f).texture(1.0F, 0.0F).color(255, 255, 255, 255);
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		
		RenderSystem.setShaderTexture(0, WEST);
		bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(matrix4f, -100.0f, 100.0f, -100.0f).texture(0.0F, 0.0F).color(255, 255, 255, 255);
		bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, -99.0f).texture(0.0F, 1.0F).color(255, 255, 255, 255);
		bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, -99.0f).texture(1.0F, 1.0F).color(255, 255, 255, 255);
		bufferBuilder.vertex(matrix4f, 100.0f, 100.0f, -100.0f).texture(1.0F, 0.0F).color(255, 255, 255, 255);
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		
		RenderSystem.setShaderTexture(0, EAST);
		bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, 100.0f).texture(0.0F, 0.0F).color(255, 255, 255, 255);
		bufferBuilder.vertex(matrix4f, -100.0f, 100.0f, 100.0f).texture(0.0F, 1.0F).color(255, 255, 255, 255);
		bufferBuilder.vertex(matrix4f, 100.0f, 100.0f, 100.0f).texture(1.0F, 1.0F).color(255, 255, 255, 255);
		bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, 100.0f).texture(1.0F, 0.0F).color(255, 255, 255, 255);
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		
		RenderSystem.setShaderTexture(0, UP);
		bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(matrix4f, -100.0f, 100.0f, 101.0f).texture(0.0F, 0.0F).color(255, 255, 255, 255);
		bufferBuilder.vertex(matrix4f, -100.0f, 100.0f, -100.0f).texture(0.0F, 1.0F).color(255, 255, 255, 255);
		bufferBuilder.vertex(matrix4f, 100.0f, 100.0f, -100.0f).texture(1.0F, 1.0F).color(255, 255, 255, 255);
		bufferBuilder.vertex(matrix4f, 100.0f, 100.0f, 100.0f).texture(1.0F, 0.0F).color(255, 255, 255, 255);
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		
		RenderSystem.setShaderTexture(0, NORTH);
		bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, -100.0f).texture(0.0F, 0.0F).color(255, 255, 255, 255);
		bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, 100.0f).texture(0.0F, 1.0F).color(255, 255, 255, 255);
		bufferBuilder.vertex(matrix4f, 100.0f, 100.0f, 100.0f).texture(1.0F, 1.0F).color(255, 255, 255, 255);
		bufferBuilder.vertex(matrix4f, 100.0f, 100.0f, -100.0f).texture(1.0F, 0.0F).color(255, 255, 255, 255);
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		
		RenderSystem.setShaderTexture(0, SOUTH);
		bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(matrix4f, -100.0f, 100.0f, -100.0f).texture(0.0F, 0.0F).color(255, 255, 255, 255);
		bufferBuilder.vertex(matrix4f, -100.0f, 100.0f, 100.0f).texture(0.0F, 1.0F).color(255, 255, 255, 255);
		bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, 100.0f).texture(1.0F, 1.0F).color(255, 255, 255, 255);
		bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, -100.0f).texture(1.0F, 0.0F).color(255, 255, 255, 255);
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
	}
	
	
}