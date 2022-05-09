package com.itedya.kasixmc.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.itedya.kasixmc.dtos.IslandDto;

import java.lang.reflect.Type;

public class IslandDtoSerializer implements JsonSerializer<IslandDto> {
    @Override
    public JsonElement serialize(IslandDto src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();

        obj.addProperty("uuid", src.getUuid());
        obj.addProperty("ownerUuid", src.getOwnerUUID());
        obj.addProperty("deleted", src.getDeleted());
        obj.addProperty("home.x", src.getHome().getX());
        obj.addProperty("home.y", src.getHome().getY());
        obj.addProperty("home.z", src.getHome().getZ());

        return obj;
    }
}
