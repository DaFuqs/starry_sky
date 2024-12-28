package de.dafuqs.starryskies.client.sky;

import com.mojang.blaze3d.systems.*;
import de.dafuqs.starryskies.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
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
		MatrixStack matrixStack = new MatrixStack();
		matrixStack.multiplyPositionMatrix(context.projectionMatrix());
		renderStarrySky(matrixStack);
	}
	
	// See WorldRenderer.renderEndSky() for inspiration
	private void renderStarrySky(MatrixStack matrices) {
		RenderSystem.enableBlend();
		RenderSystem.depthMask(false);
		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
		Tessellator tessellator = Tessellator.getInstance();
		
		for (int i = 0; i < 6; ++i) {
			matrices.push();
			
			if (i == 0) {
				RenderSystem.setShaderTexture(0, DOWN);
			}
			
			if (i == 1) {
				RenderSystem.setShaderTexture(0, WEST);
				matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
			}
			
			if (i == 2) {
				RenderSystem.setShaderTexture(0, EAST);
				matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
			}
			
			if (i == 3) {
				RenderSystem.setShaderTexture(0, UP);
				matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
			}
			
			if (i == 4) {
				RenderSystem.setShaderTexture(0, NORTH);
				matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
			}
			
			if (i == 5) {
				RenderSystem.setShaderTexture(0, SOUTH);
				matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0F));
			}
			
			Matrix4f matrix4f = matrices.peek().getPositionMatrix();
			BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(-14145496);
			bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 16.0F).color(-14145496);
			bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(16.0F, 16.0F).color(-14145496);
			bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(16.0F, 0.0F).color(-14145496);
			BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
			matrices.pop();
		}
		
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
	}

}