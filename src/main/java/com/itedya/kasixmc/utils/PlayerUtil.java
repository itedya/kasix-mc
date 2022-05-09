package com.itedya.kasixmc.utils;

import com.itedya.kasixmc.KasixMC;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PlayerUtil {
    public static Integer getMaxAllowedIslands(Player player) {
        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            String permissionId = permission.getPermission();
            if (permissionId.startsWith("kasix-mc.islands.max-amount.")) {
                String[] splittedPermissionId = permissionId.split("\\.");

                try {
                    return Integer.parseInt(splittedPermissionId[splittedPermissionId.length - 1]);
                } catch (Exception e) {
                    KasixMC.getPluginLogger().severe("Wrong format in max island amount permission.");
                    e.printStackTrace();
                    return null;
                }
            }
        }

        return 1;
    }

    public static Integer getStartIslandRadius(Player player) {
        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            String permissionId = permission.getPermission();
            if (permissionId.startsWith("kasix-mc.islands.start-radius.")) {
                String[] splittedPermissionId = permissionId.split("\\.");

                try {
                    return Integer.parseInt(splittedPermissionId[splittedPermissionId.length - 1]);
                } catch (Exception e) {
                    KasixMC.getPluginLogger().severe("Wrong format in island start radius permission.");
                    e.printStackTrace();
                    return null;
                }
            }
        }

        return 50;
    }

    public static Integer getMaxAllowedIslandMembers(Player player) {
        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            String permissionId = permission.getPermission();
            if (permissionId.startsWith("kasix-mc.islands.max-members.")) {
                String[] splittedPermissionId = permissionId.split("\\.");

                try {
                    return Integer.parseInt(splittedPermissionId[splittedPermissionId.length - 1]);
                } catch (Exception e) {
                    KasixMC.getPluginLogger().severe("Wrong format in max island members permission.");
                    e.printStackTrace();
                    return null;
                }
            }
        }

        return 2;
    }
}
