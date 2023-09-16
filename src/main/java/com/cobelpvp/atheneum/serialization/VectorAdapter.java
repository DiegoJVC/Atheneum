package com.cobelpvp.atheneum.serialization;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;
import org.bukkit.util.Vector;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;

public class VectorAdapter implements JsonDeserializer<Vector>, JsonSerializer<Vector>
{
    public Vector deserialize(final JsonElement src, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        return fromJson(src);
    }

    public JsonElement serialize(final Vector src, final Type type, final JsonSerializationContext context) {
        return (JsonElement)toJson(src);
    }

    public static JsonObject toJson(final Vector src) {
        if (src == null) {
            return null;
        }
        final JsonObject object = new JsonObject();
        object.addProperty("x", (Number)src.getX());
        object.addProperty("y", (Number)src.getY());
        object.addProperty("z", (Number)src.getZ());
        return object;
    }

    public static Vector fromJson(final JsonElement src) {
        if (src == null || !src.isJsonObject()) {
            return null;
        }
        final JsonObject json = src.getAsJsonObject();
        final double x = json.get("x").getAsDouble();
        final double y = json.get("y").getAsDouble();
        final double z = json.get("z").getAsDouble();
        return new Vector(x, y, z);
    }
}
