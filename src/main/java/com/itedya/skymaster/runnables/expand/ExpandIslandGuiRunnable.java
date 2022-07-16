package com.itedya.skymaster.runnables.expand;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.runnables.SkymasterRunnable;
import com.itedya.skymaster.runnables.island.ResetWorldGuardPermissionsRunnable;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.util.Objects;
import java.util.UUID;

public class ExpandIslandGuiRunnable extends SkymasterRunnable {
    public ExpandIslandGuiRunnable(Player executor, int islandId, int newIslandRadius, boolean admin) {
        super(executor, true);

        data.put("executor", executor);
        data.put("islandId", islandId);
        data.put("newRadius", newIslandRadius);
        data.put("admin", admin);
    }

    private Connection connection;

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();

            IslandDao islandDao = new IslandDao(connection);

            var islandId = (int) data.get("islandId");
            var dto = islandDao.getById(islandId);
            if (dto == null) {
                var executor = (Player) data.get("executor");
                executor.sendRawMessage(ChatColor.RED + "Hmm... serwer mówi że stoisz na wyspie, która jest usunięta. To błąd, zgłoś do administracji.");
                return;
            }

            var executor = (Player) data.get("executor");
            var admin = (boolean) data.get("admin");
            if (!admin && !Objects.equals(dto.ownerUuid, executor.getUniqueId().toString())) {
                executor.sendMessage(ChatColor.YELLOW + "Ta wyspa nie jest twoja!");
                return;
            }

            dto.radius = (int) data.get("newRadius");
            data.put("islandDto", dto);


            if (!admin) {
                ThreadUtil.sync(this::checkBalance);
            } else {
                ThreadUtil.async(this::saveIntoDatabase);
            }
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void checkBalance() {
        try {
            var dto = (IslandDto) data.get("islandDto");

            var owner = Bukkit.getOfflinePlayer(UUID.fromString(dto.ownerUuid));
            data.put("islandOwner", owner);

            var eco = SkyMaster.getEconomy();
            var bal = eco.getBalance(owner);

            if (bal < 100) {
                var executor = (Player) data.get("executor");
                executor.sendRawMessage(ChatColor.YELLOW + "Potrzebujesz 100$, żeby powiększyć wyspę.");
                super.closeDatabase();
                return;
            }

            eco.withdrawPlayer(owner, 100);

            ThreadUtil.async(this::saveIntoDatabase);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void saveIntoDatabase() {
        try {
            var islandDto = (IslandDto) data.get("islandDto");

            IslandDao islandDao = new IslandDao(connection);
            islandDao.update(islandDto);

            connection.commit();
            connection.close();

            ThreadUtil.sync(this::modifyBalance);
        } catch (Exception e) {
            super.errorHandling(e);

            var executor = (Player) data.get("executor");
            var islandDto = (IslandDto) data.get("islandDto");
            ThreadUtil.async(new ResetWorldGuardPermissionsRunnable(executor, islandDto.id));
        }
    }

    public void modifyBalance() {
        try {
            var admin = (boolean) data.get("admin");
            var executor = (Player) data.get("executor");
            var dto = (IslandDto) data.get("islandDto");
            var owner = (OfflinePlayer) data.get("islandOwner");

            ThreadUtil.async(new ResetWorldGuardPermissionsRunnable(executor, dto.id));

            if (admin) {
                owner = Bukkit.getOfflinePlayer(UUID.fromString(dto.ownerUuid));

                executor.sendRawMessage("%sPowiększyłeś wyspę %s\"%s\"%s gracza %s do %s kratek!".formatted(
                        ChatColor.GREEN, ChatColor.BOLD, dto.name, ChatColor.RESET + "" + ChatColor.GREEN,
                        ChatColor.BOLD + owner.getName() + ChatColor.RESET + "" + ChatColor.GREEN,
                        ChatColor.BOLD + "" + (dto.radius * 2) + "" + ChatColor.RESET + "" + ChatColor.GREEN
                ));
            } else {
                executor.sendRawMessage("%sPowiększyłeś wyspę %s\"%s\"%s do %s kratek!".formatted(
                        ChatColor.GREEN, ChatColor.BOLD, dto.name, ChatColor.RESET + "" + ChatColor.GREEN,
                        ChatColor.BOLD + "" + (dto.radius * 2) + "" + ChatColor.RESET + "" + ChatColor.GREEN
                ));
            }
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }
}
