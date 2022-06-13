package com.itedya.skymaster.daos;

import com.itedya.skymaster.dtos.IslandInviteDto;
import com.itedya.skymaster.utils.ThreadUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

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

    private final List<IslandInviteDto> data = new ArrayList<>();

    public IslandInviteDto getByToPlayerUuid(String uuid) {
        return data.stream()
                .filter(ele -> ele.getToPlayer().getUniqueId().toString().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public void remove(String toPlayerUuid) {
        data.removeIf(ele -> ele.getToPlayer().getUniqueId().toString().equals(toPlayerUuid));
    }

    public boolean doesPlayerHaveInvite(String uuid) {
        return getByToPlayerUuid(uuid) != null;
    }

    public void addToQueue(IslandInviteDto inviteDto) {
        data.add(inviteDto);

        ThreadUtil.sync(() -> this.tick(inviteDto, 60, 60));
    }

    private void tick(IslandInviteDto inviteDto, int seconds, int rmsec) {
        if (seconds <= 0) {
            var fromPlayer = inviteDto.getFromPlayer();
            var toPlayer = inviteDto.getToPlayer();

            fromPlayer.sendMessage(new ComponentBuilder()
                    .color(ChatColor.YELLOW)
                    .append("Zaproszenie do gracza ")
                    .append(toPlayer.getName()).bold(true)
                    .append(" wygasło").bold(false)
                    .create());

            toPlayer.sendMessage(new ComponentBuilder()
                    .color(ChatColor.YELLOW)
                    .append("Zaproszenie od gracza ")
                    .append(fromPlayer.getName()).bold(true)
                    .append(" wygasło").bold(false)
                    .create());

            this.remove(toPlayer.getUniqueId().toString());
        } else {
            ThreadUtil.syncDelay(() -> this.tick(inviteDto, seconds - rmsec, rmsec), rmsec * 20);
        }
    }
}
