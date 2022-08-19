package com.itedya.skymaster.listeners;

import com.itedya.skymaster.daos.IslandInviteDao;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class InviteRemoveOnPlayerQuitListener implements Listener {
    @EventHandler
    public void removeInvite(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        IslandInviteDao dao = IslandInviteDao.getInstance();
        dao.removeOr(player.getUniqueId().toString());
    }
}
