package com.itedya.skymaster.daos;

import com.itedya.skymaster.dtos.IslandCreationCooldownDto;
import com.itedya.skymaster.utils.ThreadUtil;

import java.util.ArrayList;
import java.util.List;

public class IslandCreationCooldownDao {
    private List<IslandCreationCooldownDto> data = new ArrayList<>();
    private static IslandCreationCooldownDao instance;

    public static IslandCreationCooldownDao getInstance() {
        if (instance == null) instance = new IslandCreationCooldownDao();
        return instance;
    }

    private IslandCreationCooldownDao() {
        ThreadUtil.syncDelay(this::removeOneSecond, 20);
    }

    private void removeOneSecond() {
        for (int i = 0; i < data.size(); i++) {
            IslandCreationCooldownDto dto = data.get(i);

            dto.expiresIn -= 1;

            data.set(i, dto);
        }

        data = data.stream().filter(ele -> ele.expiresIn >= 1).toList();
    }

    public void add(IslandCreationCooldownDto dto) {
        data.add(dto);
    }

    public IslandCreationCooldownDto getByPlayerUuid(String playerUuid) {
        return data.stream().filter(ele -> ele.playerUuid.equals(playerUuid)).findFirst().orElse(null);
    }
}