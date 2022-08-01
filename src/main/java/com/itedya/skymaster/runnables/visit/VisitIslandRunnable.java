package com.itedya.skymaster.runnables.visit;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandHomeDao;
import com.itedya.skymaster.daos.VisitBlockDao;
import com.itedya.skymaster.dtos.database.IslandHomeDto;
import com.itedya.skymaster.runnables.SkymasterRunnable;
import com.itedya.skymaster.utils.IslandHomeUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.entity.Player;

public class VisitIslandRunnable extends SkymasterRunnable {
    public VisitIslandRunnable(Player executor, int islandId) {
        super(executor, true);

        data.put("executor", executor);
        data.put("islandId", islandId);
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();

            VisitBlockDao blockDao = new VisitBlockDao(connection);

            int islandId = (int) data.get("islandId");
            Player executor = (Player) data.get("executor");

            IslandHomeDao homeDao = new IslandHomeDao(connection);

            var home = homeDao.getByIslandId(islandId);

            data.put("home", home);

            this.closeDatabase();

            ThreadUtil.sync(this::teleportToHome);
        } catch (Exception e) {
            this.errorHandling(e);
        }
    }

    public void teleportToHome() {
        Player player = (Player) data.get("executor");
        IslandHomeDto homeDto = (IslandHomeDto) data.get("home");

        IslandHomeUtil.addPlayerToQueue(player, homeDto);
    }
}
