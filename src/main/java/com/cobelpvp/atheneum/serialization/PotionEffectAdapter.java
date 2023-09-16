package com.cobelpvp.atheneum.serialization;

import org.bukkit.potion.PotionEffectType;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;

public class PotionEffectAdapter implements JsonDeserializer<PotionEffect>, JsonSerializer<PotionEffect>
{
    public JsonElement serialize(final PotionEffect src, final Type typeOfSrc, final JsonSerializationContext context) {
        return (JsonElement)toJson(src);
    }

    public PotionEffect deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        return fromJson(json);
    }

    public static JsonObject toJson(final PotionEffect potionEffect) {
        if (potionEffect == null) {
            return null;
        }
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", (Number)potionEffect.getType().getId());
        jsonObject.addProperty("duration", (Number)potionEffect.getDuration());
        jsonObject.addProperty("amplifier", (Number)potionEffect.getAmplifier());
        jsonObject.addProperty("ambient", Boolean.valueOf(potionEffect.isAmbient()));
        return jsonObject;
    }

    public static PotionEffect fromJson(final JsonElement jsonElement) {
        if (jsonElement == null || !jsonElement.isJsonObject()) {
            return null;
        }
        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        final PotionEffectType effectType = PotionEffectType.getById(jsonObject.get("id").getAsInt());
        final int duration = jsonObject.get("duration").getAsInt();
        final int amplifier = jsonObject.get("amplifier").getAsInt();
        final boolean ambient = jsonObject.get("ambient").getAsBoolean();
        return new PotionEffect(effectType, duration, amplifier, ambient);
    }
}
