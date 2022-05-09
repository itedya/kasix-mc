package com.itedya.kasixmc.utils;

import com.itedya.kasixmc.daos.IslandSchematicDao;
import com.itedya.kasixmc.dtos.IslandSchematicDto;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;

public class IslandSchematicUtil {
    public static List<IslandSchematicDto> getWithPermission(Player player) throws IOException {
        IslandSchematicDao dao = IslandSchematicDao.getInstance();

        return dao.getAll()
                .stream()
                .filter(ele -> player.hasPermission("kasix-mc.islands.use-schematic." + ele.getId()))
                .toList();
    }

    public static IslandSchematicDto getById(String id) throws IOException {
        IslandSchematicDao dao = IslandSchematicDao.getInstance();

        return dao.getById(id);
    }
}
