package com.itedya.skymaster.utils;

import com.itedya.skymaster.daos.IslandSchematicDao;
import com.itedya.skymaster.dtos.IslandSchematicDto;
import com.itedya.skymaster.exceptions.ServerError;
import org.bukkit.entity.Player;

import java.util.List;

public class IslandSchematicUtil {
    public static List<IslandSchematicDto> getWithPermission(Player player) throws ServerError {
        IslandSchematicDao dao = IslandSchematicDao.getInstance();

        return dao.getAll()
                .stream()
                .filter(ele -> player.hasPermission("kasix-mc.islands.use-schematic." + ele.getId()))
                .toList();
    }

    public static IslandSchematicDto getById(int id) throws ServerError {
        IslandSchematicDao dao = IslandSchematicDao.getInstance();

        return dao.getById(id);
    }
}
