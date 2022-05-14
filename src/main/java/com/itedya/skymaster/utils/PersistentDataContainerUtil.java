package com.itedya.skymaster.utils;

import com.itedya.skymaster.SkyMaster;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PersistentDataContainerUtil {
    public static void add(PersistentDataContainer container, String namespace, int value) {
        NamespacedKey key = new NamespacedKey(SkyMaster.getInstance(), namespace);

        container.set(key, PersistentDataType.INTEGER, value);
    }
}
