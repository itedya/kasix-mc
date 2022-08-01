package com.itedya.skymaster.runnables.rankings;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.dtos.IslandSizeRankingDto;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.rankings.IslandSizeRankingManager;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RefreshIslandRankingRunnable extends BukkitRunnable {
    private List<IslandDto> islands;
    private List<String> nicks;

    @Override
    public void run() {
        try {
            Connection connection = Database.getInstance().getConnection();

            IslandDao islandDao = new IslandDao(connection);

            islands = islandDao.getFirstNTHByIslandSize(10);

            ThreadUtil.sync(this::fetchNicknames);

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchNicknames() {
        nicks = new ArrayList<>();

        islands.forEach(island -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(island.ownerUuid));

            nicks.add(player.getName());
        });
        ThreadUtil.sync(this::generateDtos);
    }

    public void generateDtos() {
        List<IslandSizeRankingDto> dtos = new ArrayList<>();

        for (int i = 0; i < islands.size(); i++) {
            IslandDto islandDto = islands.get(i);
            String nickname = nicks.get(i);

            IslandSizeRankingDto dto = new IslandSizeRankingDto();
            dto.islandDto = islandDto;
            dto.islandOwnerName = nickname;
            dtos.add(dto);
        }

        IslandSizeRankingManager manager = IslandSizeRankingManager.getInstance();
        manager.saveData(dtos);
    }
}
