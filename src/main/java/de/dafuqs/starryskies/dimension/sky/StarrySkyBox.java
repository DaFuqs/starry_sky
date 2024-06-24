package de.dafuqs.starryskies.dimension.sky;

import com.google.common.collect.*;
import com.mojang.blaze3d.systems.*;
import de.dafuqs.starryskies.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.*;
import net.minecraft.client.option.*;
import net.minecraft.client.render.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.joml.*;

import java.lang.Math;
import java.util.*;

@Environment(EnvType.CLIENT)
public class StarrySkyBox implements DimensionRenderingRegistry.SkyRenderer {
	
	public final Identifier UP;
	public final Identifier DOWN;
	public final Identifier WEST;
	public final Identifier EAST;
	public final Identifier NORTH;
	public final Identifier SOUTH;
	
	public StarrySkyBox(String up, String down, String west, String east, String north, String south) {
		UP = new Identifier(StarrySkies.MOD_ID, up);
		DOWN = new Identifier(StarrySkies.MOD_ID, down);
		WEST = new Identifier(StarrySkies.MOD_ID, west);
		EAST = new Identifier(StarrySkies.MOD_ID, east);
		NORTH = new Identifier(StarrySkies.MOD_ID, north);
		SOUTH = new Identifier(StarrySkies.MOD_ID, south);
	}
	
	@Override
	public void render(WorldRenderContext context) {
		MinecraftClient client = MinecraftClient.getInstance();
		World world = client.world;
		
		if (world == null) {
			return;
		}
		
		GameOptions options = MinecraftClient.getInstance().options;
		float distance = 16F * (float) options.getViewDistance().getValue() - 8F;
		int color = (int) Math.abs(((Math.abs((world.getTimeOfDay() - 6000) % 24000) - 12000) / 47)); // 47 = 12000 (half day)  /255 (max hue)
		int rawLight = (int) ((world.getTimeOfDay() / 12000) % 15); // a day is 24000; max light level = 15
		int vertexLight = 0x00f000f0 >> 2 | rawLight >> 3 | rawLight;
		
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(false);
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		
		Matrix4f posMatrix = context.positionMatrix();
		Deque<Matrix4f> matrices = Queues.newArrayDeque();
		for (int i = 0; i < 6; ++i) {
			matrices.push(posMatrix);
			posMatrix = new Matrix4f(posMatrix);
			if (i == 0) {
				RenderSystem.setShaderTexture(0, DOWN);
			}
			if (i == 1) {
				RenderSystem.setShaderTexture(0, WEST);
				posMatrix.rotate(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
			}
			if (i == 2) {
				RenderSystem.setShaderTexture(0, EAST);
				posMatrix.rotate(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
			}
			if (i == 3) {
				RenderSystem.setShaderTexture(0, UP);
				posMatrix.rotate(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
			}
			if (i == 4) {
				RenderSystem.setShaderTexture(0, NORTH);
				posMatrix.rotate(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
			}
			if (i == 5) {
				RenderSystem.setShaderTexture(0, SOUTH);
				posMatrix.rotate(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0F));
			}
			
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			
			buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
			buffer.vertex(posMatrix, -distance, -distance, -distance).texture(0.0F, 0.0F).color(color, color, color, color).light(vertexLight).next();
			buffer.vertex(posMatrix, -distance, -distance, distance).texture(0.0F, 1.0F).color(color, color, color, color).light(vertexLight).next();
			buffer.vertex(posMatrix, distance, -distance, distance).texture(1.0F, 1.0F).color(color, color, color, color).light(vertexLight).next();
			buffer.vertex(posMatrix, distance, -distance, -distance).texture(1.0F, 0.0F).color(color, color, color, color).light(vertexLight).next();
			tessellator.draw();
			posMatrix = matrices.pop();
		}
		
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
	}
	
}