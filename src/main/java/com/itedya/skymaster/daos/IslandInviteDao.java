package com.itedya.skymaster.daos;

import com.itedya.skymaster.dtos.IslandInviteDto;

import java.util.ArrayList;
import java.util.List;

public class IslandInviteDao {
    private static IslandInviteDao instance;

    public static IslandInviteDao getInstance() {
        if (instance == null) instance = new IslandInviteDao();
        return instance;
    }

    private IslandInviteDao() {
    }

    private final List<IslandInviteDto> invitesMap = new ArrayList<>();

    public IslandInviteDto getByToPlayerUuid(String uuid) {
        return invitesMap.stream()
                .filter(ele -> ele.getToPlayer().getUniqueId().toString().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public boolean doesPlayerHaveInvite(String uuid) {
        return getByToPlayerUuid(uuid) != null;
    }
}
