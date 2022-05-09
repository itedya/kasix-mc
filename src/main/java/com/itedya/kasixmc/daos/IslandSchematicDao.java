package com.itedya.kasixmc.daos;

import com.google.gson.Gson;
import com.itedya.kasixmc.KasixMC;
import com.itedya.kasixmc.dtos.IslandSchematicDto;
import org.bukkit.ChatColor;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class IslandSchematicDao {
    private static IslandSchematicDao instance;

    public static IslandSchematicDao getInstance() throws IOException {
        if (instance == null) instance = new IslandSchematicDao();
        return instance;
    }

    private List<IslandSchematicDto> data;

    private IslandSchematicDao() throws IOException {
        KasixMC kasixMC = KasixMC.getInstance();

        String path = Paths.get(kasixMC.getDataFolder().getAbsolutePath(), "data", "schematics.json").toString();

        Reader fileReader = new FileReader(path);

        Gson gson = new Gson();
        data = new CopyOnWriteArrayList<>(gson.fromJson(fileReader, IslandSchematicDto[].class));

        fileReader.close();
    }

    public List<IslandSchematicDto> getAll() {
        return data;
    }

    public IslandSchematicDto getById(String id) {
        return data.stream()
                .filter(ele -> ele.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void create(IslandSchematicDto islandSchematicDto) {
        data.add(islandSchematicDto);
    }

    public void saveToFile() throws IOException {
        Gson gson = new Gson();

        KasixMC kasixMC = KasixMC.getInstance();
        String path = Paths.get(kasixMC.getDataFolder().getAbsolutePath(), "data", "schematics.json").toString();

        Writer fileWriter = new FileWriter(path);

        List<IslandSchematicDto> dataToSave = this.getAll();

        gson.toJson(dataToSave, fileWriter);

        fileWriter.flush();
        fileWriter.close();
    }
}
