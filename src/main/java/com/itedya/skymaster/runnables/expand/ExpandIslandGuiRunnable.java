package com.itedya.skymaster.runnables.expand;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.runnables.SkymasterRunnable;
import com.itedya.skymaster.runnables.island.ResetWorldGuardPermissionsRunnable;
import com.itedya.skymaster.utils.IslandUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.util.Objects;
import java.util.UUID;

public class ExpandIslandGuiRunnable extends SkymasterRunnable {
    private Player executor;
    private int islandId;
    private int newIslandRadius;
    private boolean admin;
    private IslandDto islandDto;
    private OfflinePlayer owner;

    public ExpandIslandGuiRunnable(Player executor, int islandId, int newIslandRadius, boolean admin) {
        super(executor, true);

        this.executor = executor;
        this.islandId = islandId;
        this.newIslandRadius = newIslandRadius;
        this.admin = admin;
    }

    private Connection connection;

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();

            IslandDao islandDao = new IslandDao(connection);

            islandDto = islandDao.getById(islandId);
            if (islandDto == null) {
                executor.sendRawMessage(ChatColor.RED + "Hmm... serwer mówi że stoisz na wyspie, która jest usunięta. To błąd, zgłoś do administracji.");
                return;
            }

            if (!admin && !Objects.equals(islandDto.ownerUuid, executor.getUniqueId().toString())) {
                executor.sendMessage(ChatColor.YELLOW + "Ta wyspa nie jest twoja!");
                return;
            }

            islandDto.radius = newIslandRadius;


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
            owner = Bukkit.getOfflinePlayer(UUID.fromString(islandDto.ownerUuid));

            var eco = SkyMaster.getEconomy();
            var bal = eco.getBalance(owner);

            var cost = IslandUtil.getExpandCost(newIslandRadius);

            if (bal < cost) {
                executor.sendRawMessage(ChatColor.YELLOW + "Potrzebujesz " + cost + ", żeby powiększyć wyspę.");
                super.closeDatabase();
                return;
            }

            eco.withdrawPlayer(owner, cost);

            ThreadUtil.async(this::saveIntoDatabase);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void saveIntoDatabase() {
        try {
            IslandDao islandDao = new IslandDao(connection);
            islandDao.update(islandDto);

            connection.commit();
            connection.close();

            ThreadUtil.sync(this::modifyBalance);
        } catch (Exception e) {
            super.errorHandling(e);
            ThreadUtil.async(new ResetWorldGuardPermissionsRunnable(executor, islandDto.id));
        }
    }

    public void modifyBalance() {
        try {
            ThreadUtil.async(new ResetWorldGuardPermissionsRunnable(executor, islandDto.id));

            if (admin) {
                owner = Bukkit.getOfflinePlayer(UUID.fromString(islandDto.ownerUuid));

                executor.sendRawMessage("%sPowiększyłeś wyspę %s\"%s\"%s gracza %s do %s kratek!".formatted(
                        ChatColor.GREEN, ChatColor.BOLD, islandDto.name, ChatColor.RESET + "" + ChatColor.GREEN,
                        ChatColor.BOLD + owner.getName() + ChatColor.RESET + "" + ChatColor.GREEN,
                        ChatColor.BOLD + "" + (islandDto.radius * 2) + "" + ChatColor.RESET + "" + ChatColor.GREEN
                ));
            } else {
                executor.sendRawMessage("%sPowiększyłeś wyspę %s\"%s\"%s do %s kratek!".formatted(
                        ChatColor.GREEN, ChatColor.BOLD, islandDto.name, ChatColor.RESET + "" + ChatColor.GREEN,
                        ChatColor.BOLD + "" + (islandDto.radius * 2) + "" + ChatColor.RESET + "" + ChatColor.GREEN
                ));
            }
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }
}
