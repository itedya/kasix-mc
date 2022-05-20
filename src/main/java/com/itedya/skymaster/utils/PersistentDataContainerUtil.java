package com.itedya.skymaster.utils;

import com.itedya.skymaster.SkyMaster;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PersistentDataContainerUtil {
    public static void setString(PersistentDataContainer container, String namespace, String value) {
        NamespacedKey key = new NamespacedKey(SkyMaster.getInstance(), namespace);

        container.set(key, PersistentDataType.STRING, value);
    }

    public static void setInt(PersistentDataContainer container, String namespace, int value) {
        NamespacedKey key = new NamespacedKey(SkyMaster.getInstance(), namespace);

        container.set(key, PersistentDataType.INTEGER, value);
    }

    public static String getString(PersistentDataContainer container, String namespace) {
        NamespacedKey key = new NamespacedKey(SkyMaster.getInstance(), namespace);

        return container.get(key, PersistentDataType.STRING);
    }

    public static Integer getInt(PersistentDataContainer container, String namespace) {
        NamespacedKey key = new NamespacedKey(SkyMaster.getInstance(), namespace);

        try {
            return container.get(key, PersistentDataType.INTEGER);
        } catch (NullPointerException e) {
            return null;
        }
    }
}
