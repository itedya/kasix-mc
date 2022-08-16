package com.itedya.skymaster.utils;

import com.itedya.skymaster.SkyMaster;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;

public class PlayerUtil {
    public static Integer getMaxAllowedIslands(Player player) {
        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            String permissionId = permission.getPermission();
            if (permissionId.startsWith("skymaster.islands.max-amount.")) {
                String[] splittedPermissionId = permissionId.split("\\.");

                try {
                    return Integer.parseInt(splittedPermissionId[splittedPermissionId.length - 1]);
                } catch (Exception e) {
                    SkyMaster.getPluginLogger().severe("Wrong format in max island amount permission.");
                    e.printStackTrace();
                    return 1;
                }
            }
        }

        return 1;
    }

    public static Integer getStartIslandRadius(Player player) {
        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            String permissionId = permission.getPermission();
            if (permissionId.startsWith("skymaster.islands.start-radius.")) {
                String[] splittedPermissionId = permissionId.split("\\.");

                try {
                    return Integer.parseInt(splittedPermissionId[splittedPermissionId.length - 1]);
                } catch (Exception e) {
                    SkyMaster.getPluginLogger().severe("Wrong format in island start radius permission.");
                    e.printStackTrace();
                    return 50;
                }
            }
        }

        return 50;
    }

    /**
     * Checks how much island members can user have
     *
     * @param player Player to check
     * @return max allowed island members in Integer object
     */
    public static @NotNull Integer getMaxAllowedIslandMembers(Player player) {
        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            String permissionId = permission.getPermission();
            if (permissionId.startsWith("skymaster.islands.max-members.")) {
                String[] splittedPermissionId = permissionId.split("\\.");

                try {
                    return Integer.parseInt(splittedPermissionId[splittedPermissionId.length - 1]);
                } catch (Exception e) {
                    SkyMaster.getPluginLogger().severe("Wrong format in max island members permission.");
                    e.printStackTrace();
                    return 2;
                }
            }
        }

        return 2;
    }
}
