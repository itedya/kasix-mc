package com.itedya.kasixmc.deserializers;

import com.google.gson.*;
import com.itedya.kasixmc.dtos.IslandDto;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

public class IslandDtoDeserializer implements JsonDeserializer<IslandDto> {
    @Override
    public IslandDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        World world = Bukkit.getWorld("world_islands");

        Location home = new Location(
                world,
                jsonObject.get("home.x").getAsDouble(),
                jsonObject.get("home.y").getAsDouble(),
                jsonObject.get("home.z").getAsDouble()
        );

        IslandDto islandDto = new IslandDto();
        islandDto.setUuid(jsonObject.get("uuid").getAsString());
        islandDto.setOwnerUUID(jsonObject.get("ownerUuid").getAsString());
        islandDto.setHome(home);
        islandDto.setDeleted(jsonObject.get("deleted").getAsBoolean());
        return islandDto;
    }
}
