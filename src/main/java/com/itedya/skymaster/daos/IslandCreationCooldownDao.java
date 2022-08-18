package com.itedya.skymaster.daos;

import com.itedya.skymaster.dtos.IslandCreationCooldownDto;
import com.itedya.skymaster.utils.ThreadUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IslandCreationCooldownDao {
    private List<IslandCreationCooldownDto> data;
    private static IslandCreationCooldownDao instance;

    public static IslandCreationCooldownDao getInstance() {
        if (instance == null) instance = new IslandCreationCooldownDao();
        return instance;
    }

    private IslandCreationCooldownDao() {
        this.data = new ArrayList<>();
        ThreadUtil.syncRepeat(this::removeOneSecond, 20, 20);
    }

    private void removeOneSecond() {
        Iterator<IslandCreationCooldownDto> iterator = data.iterator();

        while (iterator.hasNext()) {
            IslandCreationCooldownDto dto = iterator.next();

            dto.expiresIn -= 1;

            if (dto.expiresIn <= 0) iterator.remove();
        }
    }

    public void add(IslandCreationCooldownDto dto) {
        this.data.add(dto);
    }

    public IslandCreationCooldownDto getByPlayerUuid(String playerUuid) {
        return data.stream().filter(ele -> ele.playerUuid.equals(playerUuid)).findFirst().orElse(null);
    }

    public void remove(String playerUuid) {
        data.removeIf((dto) -> dto.playerUuid.equals(playerUuid));
    }
}