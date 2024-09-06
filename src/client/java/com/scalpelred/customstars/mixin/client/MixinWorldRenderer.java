package com.scalpelred.customstars.mixin.client;

import com.scalpelred.customstars.CustomStars;
import com.scalpelred.customstars.CustomStarsClient;
import com.scalpelred.customstars.CustomStarsClientConfig;
import net.minecraft.client.render.*;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.zip.DataFormatException;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {

	@Unique
	private final Random rand = new Random(
			//1414213562
	);

	@Shadow protected abstract void renderStars();

	@Inject(method = "renderStars(Lnet/minecraft/client/render/Tessellator;)Lnet/minecraft/client/render/BuiltBuffer;",
			at = @At("RETURN"), cancellable = true)
	private void injectRenderStars(Tessellator tessellator, CallbackInfoReturnable<BuiltBuffer> cir) {
		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION);

		for (int i = 0; i < 1500; i++) {
			float x = (float)Math.random() * 2f - 1f;
			float y = (float)Math.random() * 2f - 1f;
			float z = (float)Math.random() * 2f - 1f;

			float r = MathHelper.magnitude(x, y, z);
			if ((r > 0.010000001f) && (r < 1.0f)) {
				float size = (float)(Math.random() * 0.1f + 0.15f);
				float rot = (float)(Math.random() * Math.PI * 2.0);
				Vector3f pos = new Vector3f(x, y, z).mul(100f / r);

				Quaternionf q = new Quaternionf().rotateTo(new Vector3f(0f, 0f, -1f), pos).rotateZ(rot);
				bufferBuilder.vertex(new Vector3f(-size, -size, 0.0f).rotate(q).add(pos));
				bufferBuilder.vertex(new Vector3f(+size, +size, 0.0f).rotate(q).add(pos));
				bufferBuilder.vertex(new Vector3f(-size, +size, 0.0f).rotate(q).add(pos));
				bufferBuilder.vertex(new Vector3f(-size, -size, 0.0f).rotate(q).add(pos));
				bufferBuilder.vertex(new Vector3f(+size, -size, 0.0f).rotate(q).add(pos));
				bufferBuilder.vertex(new Vector3f(+size, +size, 0.0f).rotate(q).add(pos));
			}
		}

		cir.setReturnValue(bufferBuilder.end());
	}

	@Inject(method = "reload()V", at = @At("RETURN"))
	public void injectReload(CallbackInfo ci) {
		renderStars();
	}

	@Unique
	private static final Float[] MODEL_FLAT
			= new Float[] { 1f, -1f, 0f,  1f, 1f, 0f,  -1f, 1f, 0f,  1f, -1f, 0f,  -1f, 1f, 0f,  -1f, -1f, 0f };
	@Unique
	private static final Float[] MODEL_CUBE;
	static {
		Float[] modelCube1;
		try {
			modelCube1 = parseObj("""
                    v 1 1 -1
                    v 1 -1 -1
                    v 1 1 1
                    v 1 -1 1
                    v -1 1 -1
                    v -1 -1 -1
                    v -1 1 1
                    v -1 -1 1
                    f 5 3 1
                    f 3 8 4
                    f 7 6 8
                    f 2 8 6
                    f 1 4 2
                    f 5 2 6
                    f 5 7 3
                    f 3 7 8
                    f 7 5 6
                    f 2 4 8
                    f 1 3 4
                    f 5 1 2""");
		}
		catch (DataFormatException e) {
			modelCube1 = new Float[0];
		}
		MODEL_CUBE = modelCube1;
	}

	@Unique
	private static Float[] parseObj(String src) throws DataFormatException {

		List<Float> coords = new ArrayList<>();
		List<Float> res = new ArrayList<>();

		String[] lines = src.split("\\r?\\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();

			if (line.startsWith("v ")) {
				String[] words = line.split("\\s+");
				coords.add(Float.parseFloat(words[1]));
				coords.add(Float.parseFloat(words[2]));
				coords.add(Float.parseFloat(words[3]));
			} else if (line.startsWith("f ")) {
				String[] words = line.split("\\s+");
				if (words.length != 4) throw new DataFormatException("Only 3-vertex faces are supported.");

				for (int wi = 1; wi < words.length; wi++) {
					int index = Integer.parseInt(words[wi].split("/")[0]) - 1;
					index *= 3;
					res.add(coords.get(index));
					res.add(coords.get(index + 1));
					res.add(coords.get(index + 2));
				}
			}
		}
		return res.toArray(new Float[0]);
	}

	@Unique
	private static Float[][] getModels(CustomStarsClientConfig.StarModelEnum filterUsage, List<String> filter) {
		File modelFolder = CustomStarsClient.getStarModelsFolder();
		if (!modelFolder.exists()) {
			CustomStars.LOGGER.error("starmodels directory does not exist, there will no be such thing as stars.");
			return new Float[0][];
		}

		ArrayList<File> objs = new ArrayList<>();
		for (File entry : Objects.requireNonNull(modelFolder.listFiles())) {
			if (entry.isFile() && entry.getName().endsWith(".obj")) objs.add(entry);
		}
		if (filterUsage == CustomStarsClientConfig.StarModelEnum.SELECTED) {
			for (int i = 0; i < objs.size(); i++) {
				if (!filter.contains(objs.get(i).getName().replace(".obj", ""))) {
					objs.remove(i);
					i--;
				}
			}
		}
		else if (filterUsage == CustomStarsClientConfig.StarModelEnum.DESELECTED) {
			for (int i = 0; i < objs.size(); i++) {
				if (filter.contains(objs.get(i).getName().replace(".obj", ""))) {
					objs.remove(i);
					i--;
				}
			}
		}

		ArrayList<Float[]> res = new ArrayList<>();
		for (File file : objs) {
			String content;
			try {
				content = Files.readString(file.toPath());
			}
			catch (IOException e) {
				CustomStars.LOGGER.error("Error reading file {}, {}", file.getName(), e.getMessage());
				continue;
			}
			Float[] mdl;
			try {
				mdl = parseObj(content);
			}
			catch (Exception e) {
				CustomStars.LOGGER.error("Error reading obj file {}: {}", file.getName(), e.getMessage());
				continue;
			}
			res.add(mdl);
		}
		return res.toArray(new Float[0][]);
	}
}