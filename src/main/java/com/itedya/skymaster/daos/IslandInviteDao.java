package com.itedya.skymaster.daos;

import com.itedya.skymaster.dtos.database.IslandInviteDto;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IslandInviteDao {
    private static IslandInviteDao instance;

    public static IslandInviteDao getInstance() {
        if (instance == null) instance = new IslandInviteDao();
        return instance;
    }

    private IslandInviteDao() {
        ThreadUtil.syncRepeat(this::removeOneSecond, 20, 20);
    }

    private final List<IslandInviteDto> data = new ArrayList<>();

    public List<IslandInviteDto> get(String toPlayerUuid) {
        return data.stream()
                .filter(ele -> ele.toPlayer.getUniqueId().toString().equals(toPlayerUuid))
                .toList();
    }

    public IslandInviteDto get(String toPlayerUuid, String fromPlayerUuid) {
        return data.stream()
                .filter(ele -> ele.toPlayer.getUniqueId().toString().equals(toPlayerUuid) && ele.fromPlayer.getUniqueId().toString().equals(fromPlayerUuid))
                .findFirst()
                .orElse(null);
    }

    public void remove(String toPlayerUuid) {
        data.removeIf(ele -> ele.toPlayer.getUniqueId().toString().equals(toPlayerUuid));
    }

    public void remove(String toPlayerUuid, String fromPlayerUuid) {
        data.removeIf(ele -> ele.toPlayer.getUniqueId().toString().equals(toPlayerUuid) && ele.fromPlayer.getUniqueId().toString().equals(fromPlayerUuid));
    }

    public void removeOr(String playerUuid) {
        data.removeIf(ele -> ele.toPlayer.getUniqueId().toString().equals(playerUuid) || ele.fromPlayer.getUniqueId().toString().equals(playerUuid));
    }

    public void addToQueue(IslandInviteDto inviteDto) {
        data.add(inviteDto);
    }

    private void removeOneSecond() {
        Iterator<IslandInviteDto> iterator = data.iterator();

        while (iterator.hasNext()) {
            IslandInviteDto dto = iterator.next();

            dto.ttl -= 1;

            if (dto.ttl <= 0) {
                Player fromPlayer = dto.fromPlayer;
                Player toPlayer = dto.toPlayer;

                fromPlayer.sendMessage(new ComponentBuilder()
                        .append(ChatUtil.PREFIX + " ")
                        .append("Zaproszenie do gracza ").color(ChatColor.YELLOW)
                        .append(toPlayer.getName()).bold(true)
                        .append(" wygasło").bold(false)
                        .create());

                toPlayer.sendMessage(new ComponentBuilder()
                        .append(ChatUtil.PREFIX + " ")
                        .color(ChatColor.YELLOW)
                        .append("Zaproszenie od gracza ")
                        .append(fromPlayer.getName()).bold(true)
                        .append(" wygasło").bold(false)
                        .create());

                iterator.remove();
            }
        }
    }
}
