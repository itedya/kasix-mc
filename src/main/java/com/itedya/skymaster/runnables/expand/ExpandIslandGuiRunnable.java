package com.itedya.skymaster.runnables.expand;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.runnables.SkymasterRunnable;
import com.itedya.skymaster.runnables.island.ResetWorldGuardPermissionsRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.IslandUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class ExpandIslandGuiRunnable extends SkymasterRunnable {
    private final Player executor;
    private final int islandId;
    private final int newIslandRadius;
    private final boolean admin;
    private IslandDto islandDto;
    private OfflinePlayer owner;

    public ExpandIslandGuiRunnable(Player executor, int islandId, int newIslandRadius, boolean admin) {
        super(executor, true);

        this.executor = executor;
        this.islandId = islandId;
        this.newIslandRadius = newIslandRadius;
        this.admin = admin;
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();

            IslandDao islandDao = new IslandDao(connection);

            islandDto = islandDao.getById(islandId);
            if (islandDto == null) {
                ChatUtil.sendWarning(executor, "Hmm... serwer mówi że stoisz na wyspie, która jest usunięta. To błąd, zgłoś do administracji.");
                return;
            }

            if (!admin && !Objects.equals(islandDto.ownerUuid, executor.getUniqueId().toString())) {
                ChatUtil.sendWarning(executor, "Ta wyspa nie jest twoja!");
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
                ChatUtil.sendWarning(executor, new ComponentBuilder()
                        .append("Potrzebujesz ")
                        .append(cost + "$")
                        .append(", żeby powiększyć wyspę.")
                        .create());
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

                executor.sendMessage(new ComponentBuilder()
                        .append(ChatUtil.PREFIX + " ")
                        .append("Powiększyłeś wyspę ").color(ChatColor.GREEN)
                        .append("\"%s\"".formatted(islandDto.name)).bold(true)
                        .append(" gracza ").bold(false)
                        .append(owner.getName()).bold(true)
                        .append(" do ").bold(false)
                        .append(islandDto.radius + "").bold(true)
                        .append(" kratek!").bold(false)
                        .create());
            } else {
                executor.sendMessage(new ComponentBuilder()
                        .append(ChatUtil.PREFIX + " ")
                        .append("Powiększyłeś wyspę ").color(ChatColor.GREEN)
                        .append("\"%s\"".formatted(islandDto.name)).bold(true)
                        .append(" do ").bold(false)
                        .append(islandDto.radius + "").bold(true)
                        .append(" kratek!").bold(false)
                        .create());
            }
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }
}
