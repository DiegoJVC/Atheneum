package com.cobelpvp.atheneum.serialization;

import com.cobelpvp.atheneum.Atheneum;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;

public class LocationAdapter implements JsonDeserializer<Location>, JsonSerializer<Location>
{
    public JsonElement serialize(final Location src, final Type typeOfSrc, final JsonSerializationContext context) {
        return (JsonElement)toJson(src);
    }

    public Location deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        return fromJson(json);
    }

    public static JsonObject toJson(final Location location) {
        if (location == null) {
            return null;
        }
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("world", location.getWorld().getName());
        jsonObject.addProperty("x", (Number)location.getX());
        jsonObject.addProperty("y", (Number)location.getY());
        jsonObject.addProperty("z", (Number)location.getZ());
        jsonObject.addProperty("yaw", (Number)location.getYaw());
        jsonObject.addProperty("pitch", (Number)location.getPitch());
        return jsonObject;
    }

    public static Location fromJson(final JsonElement jsonElement) {
        if (jsonElement == null || !jsonElement.isJsonObject()) {
            return null;
        }
        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        final World world = Atheneum.getInstance().getServer().getWorld(jsonObject.get("world").getAsString());
        final double x = jsonObject.get("x").getAsDouble();
        final double y = jsonObject.get("y").getAsDouble();
        final double z = jsonObject.get("z").getAsDouble();
        final float yaw = jsonObject.get("yaw").getAsFloat();
        final float pitch = jsonObject.get("pitch").getAsFloat();
        return new Location(world, x, y, z, yaw, pitch);
    }
}
