package com.itedya.kasixmc.daos;

import com.itedya.kasixmc.dtos.IslandInviteDto;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class IslandInviteDao {
    private static IslandInviteDao instance;

    public static IslandInviteDao getInstance() throws IOException {
        if (instance == null) instance = new IslandInviteDao();
        return instance;
    }

    private IslandInviteDao() {}

    private final List<IslandInviteDto> invitesMap = new CopyOnWriteArrayList<>();

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
