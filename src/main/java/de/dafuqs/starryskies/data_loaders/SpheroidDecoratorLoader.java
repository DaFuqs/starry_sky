package de.dafuqs.starryskies.data_loaders;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import net.fabricmc.fabric.api.resource.*;
import net.minecraft.registry.*;
import net.minecraft.resource.*;
import net.minecraft.util.*;
import net.minecraft.util.profiler.*;

import java.lang.reflect.*;
import java.util.*;

public class SpheroidDecoratorLoader extends JsonDataLoader implements IdentifiableResourceReloadListener {
	
	public static final String ID = "starry_skies/sphere_decorators";
	public static final SpheroidDecoratorLoader INSTANCE = new SpheroidDecoratorLoader();
	
	protected SpheroidDecoratorLoader() {
		super(new Gson(), ID);
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		final RegistryOps<JsonElement> ops = StarrySkies.registryManager.getOps(JsonOps.INSTANCE);

		prepared.forEach((identifier, jsonElement) -> {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			
			SpheroidDecorator decorator;
			Identifier decoratorTypeID;
			try {
				decoratorTypeID = Identifier.tryParse(JsonHelper.getString(jsonObject, "type"));
				
				try {
					SpheroidDecoratorType<?> templateClass = StarryRegistries.SPHEROID_DECORATOR_TYPE.get(decoratorTypeID);
					JsonObject typeData = JsonHelper.getObject(jsonObject, "type_data", null);
					decorator = templateClass.getCodec().codec().parse(ops, typeData).getOrThrow();
				} catch (NullPointerException e) {
					if (StarrySkies.CONFIG.packCreatorMode) {
                        StarrySkies.LOGGER.warn("Error reading sphere json definition {}: Spheroid Type {} is not known.", identifier, decoratorTypeID);
					}
					return;
				}

			} catch (Exception e) {
                StarrySkies.LOGGER.error("Error reading decorator json definition {}: {}", identifier, e);
				e.printStackTrace();
			}
		});
	}
	
	@Override
	public Identifier getFabricId() {
		return StarrySkies.locate(ID);
	}
	
	
}