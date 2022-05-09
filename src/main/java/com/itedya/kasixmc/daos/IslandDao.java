package com.itedya.kasixmc.daos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itedya.kasixmc.KasixMC;
import com.itedya.kasixmc.deserializers.IslandDtoDeserializer;
import com.itedya.kasixmc.dtos.IslandDto;
import com.itedya.kasixmc.serializers.IslandDtoSerializer;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class IslandDao {
    private static IslandDao instance;

    public static IslandDao getInstance() throws IOException {
        if (instance == null) instance = new IslandDao();
        return instance;
    }
    private Gson gson;

    private List<IslandDto> data;

    private IslandDao() throws IOException {
        KasixMC kasixMC = KasixMC.getInstance();

        String path = Paths.get(kasixMC.getDataFolder().getAbsolutePath(), "data", "islands.json").toString();

        Reader fileReader = new FileReader(path);

        this.gson = new GsonBuilder()
                .registerTypeAdapter(IslandDto.class, new IslandDtoDeserializer())
                .registerTypeAdapter(IslandDto.class, new IslandDtoSerializer())
                .create();

        data = new CopyOnWriteArrayList<>(gson.fromJson(fileReader, IslandDto[].class));

        fileReader.close();
    }

    public void saveToFile() throws IOException {
        KasixMC kasixMC = KasixMC.getInstance();
        String path = Paths.get(kasixMC.getDataFolder().getAbsolutePath(), "data", "islands.json").toString();

        Writer fileWriter = new FileWriter(path);

        gson.toJson(data, fileWriter);

        fileWriter.flush();
        fileWriter.close();
    }

    public List<IslandDto> getByOwnerUuid(String ownerUuid) {
        return getByOwnerUuid(ownerUuid, false);
    }

    public List<IslandDto> getByOwnerUuid(String ownerUuid, Boolean withDeleted) {
        if (withDeleted) {
            return data.stream()
                    .filter(ele -> ele.getOwnerUUID().equals(ownerUuid))
                    .toList();
        }

        return data.stream()
                .filter(ele -> ele.getOwnerUUID().equals(ownerUuid) && !ele.getDeleted())
                .toList();
    }

    public int getCount() {
        return data.size();
    }

    public void create(IslandDto islandDto) {
        data.add(islandDto);
    }

    public IslandDto getByUuid(String uuid) {
        return getByUuid(uuid, false);
    }

    public IslandDto getByUuid(String uuid, Boolean withDeleted) {
        if (withDeleted) {
            return data.stream()
                    .filter(ele -> ele.getUuid().equals(uuid))
                    .findFirst()
                    .orElse(null);
        }

        return data.stream()
                .filter(ele -> ele.getUuid().equals(uuid) && !ele.getDeleted())
                .findFirst()
                .orElse(null);
    }

    public void update(IslandDto islandDto) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getUuid().equals(islandDto.getUuid())) {
                data.set(i, islandDto);
                return;
            }
        }
    }
}
