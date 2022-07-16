package com.itedya.skymaster.rankings;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IslandSizeRankingManager extends RankingManager {
    private IslandSizeRankingManager() {
        // 30 sec
        ThreadUtil.asyncRepeat(this::refresh, 600);
    }

    private static IslandSizeRankingManager instance = null;

    public static IslandSizeRankingManager getInstance() {
        if (instance == null) instance = new IslandSizeRankingManager();

        return instance;
    }

    /**
     * Refreshes the ranking
     * <p>
     * ASYNC
     */
    public void refresh() {
        try {
            Connection connection = Database.getInstance().getConnection();

            IslandDao islandDao = new IslandDao(connection);

            List<IslandDto> islands = islandDao.getFirstNTHByIslandSize(10);

            ThreadUtil.sync(() -> getNicknamesOfOwners(islands));

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets nicknames of island owners
     *
     * @param islandDtos island dtos
     */
    private void getNicknamesOfOwners(List<IslandDto> islandDtos) {
        List<String> nicks = new ArrayList<>();

        islandDtos.forEach(island -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(island.ownerUuid));

            nicks.add(player.getName());
        });
        ThreadUtil.sync(() -> this.assignNewData(islandDtos, nicks));
    }

    private void assignNewData(List<IslandDto> islandDtos, List<String> nicknames) {
        for (int i = 0; i < islandDtos.size(); i++) {
            data.put(i + 1, Map.of("dto", islandDtos.get(i), "nickname", nicknames.get(i)));
        }
    }

    /**
     * Gets ranking info for place
     *
     * @param place int Place number
     * @return Map
     */
    @Override
    public Map getDataForPlace(int place) {
        return (Map) data.get(place);
    }
}
